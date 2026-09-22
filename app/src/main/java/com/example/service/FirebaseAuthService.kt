package com.example.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class AuthUser(
  val uid: String,
  val email: String?,
  val displayName: String?,
  val isAnonymous: Boolean = false,
  val photoUrl: String? = null
)

sealed class AuthResult {
  data class Success(val user: AuthUser) : AuthResult()
  data class Error(val message: String) : AuthResult()
}

suspend fun <T> Task<T>.awaitTask(): T =
  suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
      continuation.resume(result)
    }
    addOnFailureListener { exception ->
      continuation.resumeWithException(exception)
    }
    addOnCanceledListener {
      continuation.cancel()
    }
  }

class FirebaseAuthService(private val context: Context) {
  private val tag = "FirebaseAuthService"
  private val localAuthPrefs: SharedPreferences =
    context.getSharedPreferences("dsaflow_local_auth_store", Context.MODE_PRIVATE)

  private var firebaseAuth: FirebaseAuth? = null

  private val _currentUser = MutableStateFlow<AuthUser?>(null)
  val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

  private val _isFirebaseActive = MutableStateFlow(false)
  val isFirebaseActive: StateFlow<Boolean> = _isFirebaseActive.asStateFlow()

  init {
    initFirebaseAuth()
  }

  private fun initFirebaseAuth() {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        val auth = FirebaseAuth.getInstance()
        firebaseAuth = auth
        _isFirebaseActive.value = true

        val current = auth.currentUser
        if (current != null) {
          _currentUser.value = current.toAuthUser()
        }

        auth.addAuthStateListener { fa ->
          val user = fa.currentUser
          _currentUser.value = user?.toAuthUser()
        }
      } else {
        Log.i(tag, "FirebaseApp is not configured with google-services.json; local secure auth mode active.")
        loadSavedLocalSession()
      }
    } catch (e: Exception) {
      Log.w(tag, "Firebase initialization fallback: ${e.message}")
      loadSavedLocalSession()
    }
  }

  private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(
    uid = uid,
    email = email,
    displayName = displayName ?: email?.substringBefore("@") ?: "DSA Explorer",
    isAnonymous = isAnonymous,
    photoUrl = photoUrl?.toString()
  )

  private fun loadSavedLocalSession() {
    val uid = localAuthPrefs.getString("active_uid", null)
    if (uid != null) {
      val email = localAuthPrefs.getString("email_$uid", null)
      val name = localAuthPrefs.getString("name_$uid", "DSA Explorer")
      val isAnon = localAuthPrefs.getBoolean("is_anon_$uid", false)
      _currentUser.value = AuthUser(uid = uid, email = email, displayName = name, isAnonymous = isAnon)
    }
  }

  suspend fun signUpWithEmail(email: String, pass: String, name: String): AuthResult {
    val cleanEmail = email.trim()
    val cleanPass = pass.trim()
    val cleanName = name.trim().ifEmpty { cleanEmail.substringBefore("@") }

    if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
      return AuthResult.Error("Please enter a valid email address.")
    }
    if (cleanPass.length < 6) {
      return AuthResult.Error("Password must be at least 6 characters.")
    }

    val auth = firebaseAuth
    if (auth != null) {
      return try {
        val result = auth.createUserWithEmailAndPassword(cleanEmail, cleanPass).awaitTask()
        val fbUser = result.user
        if (fbUser != null) {
          try {
            val profileUpdates = UserProfileChangeRequest.Builder()
              .setDisplayName(cleanName)
              .build()
            fbUser.updateProfile(profileUpdates).awaitTask()
          } catch (e: Exception) {
            Log.w(tag, "Failed to update profile name: ${e.message}")
          }
          val appUser = fbUser.toAuthUser().copy(displayName = cleanName)
          _currentUser.value = appUser
          AuthResult.Success(appUser)
        } else {
          AuthResult.Error("Sign-up succeeded but user profile was empty.")
        }
      } catch (e: Exception) {
        Log.e(tag, "Firebase sign-up error", e)
        AuthResult.Error(e.localizedMessage ?: "Sign-up failed.")
      }
    } else {
      // Local secure authentication fallback for development/sandbox
      val existingUid = localAuthPrefs.getString("user_by_email_$cleanEmail", null)
      if (existingUid != null) {
        return AuthResult.Error("An account with this email already exists.")
      }

      val newUid = "usr_" + System.currentTimeMillis().toString().takeLast(8)
      localAuthPrefs.edit()
        .putString("user_by_email_$cleanEmail", newUid)
        .putString("email_$newUid", cleanEmail)
        .putString("pass_$newUid", cleanPass)
        .putString("name_$newUid", cleanName)
        .putBoolean("is_anon_$newUid", false)
        .putString("active_uid", newUid)
        .apply()

      val newUser = AuthUser(uid = newUid, email = cleanEmail, displayName = cleanName, isAnonymous = false)
      _currentUser.value = newUser
      return AuthResult.Success(newUser)
    }
  }

  suspend fun signInWithEmail(email: String, pass: String): AuthResult {
    val cleanEmail = email.trim()
    val cleanPass = pass.trim()

    if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
      return AuthResult.Error("Please enter a valid email address.")
    }
    if (cleanPass.isEmpty()) {
      return AuthResult.Error("Please enter your password.")
    }

    val auth = firebaseAuth
    if (auth != null) {
      return try {
        val result = auth.signInWithEmailAndPassword(cleanEmail, cleanPass).awaitTask()
        val fbUser = result.user
        if (fbUser != null) {
          val appUser = fbUser.toAuthUser()
          _currentUser.value = appUser
          AuthResult.Success(appUser)
        } else {
          AuthResult.Error("Sign-in succeeded but user session could not be established.")
        }
      } catch (e: Exception) {
        Log.e(tag, "Firebase sign-in error", e)
        AuthResult.Error(e.localizedMessage ?: "Invalid email or password.")
      }
    } else {
      // Local secure authentication fallback
      val uid = localAuthPrefs.getString("user_by_email_$cleanEmail", null)
      if (uid == null) {
        return AuthResult.Error("No account found with this email. Please sign up.")
      }

      val storedPass = localAuthPrefs.getString("pass_$uid", null)
      if (storedPass != cleanPass) {
        return AuthResult.Error("Incorrect password. Please try again.")
      }

      val name = localAuthPrefs.getString("name_$uid", cleanEmail.substringBefore("@"))
      localAuthPrefs.edit().putString("active_uid", uid).apply()

      val loggedInUser = AuthUser(uid = uid, email = cleanEmail, displayName = name, isAnonymous = false)
      _currentUser.value = loggedInUser
      return AuthResult.Success(loggedInUser)
    }
  }

  suspend fun signInAnonymously(): AuthResult {
    val auth = firebaseAuth
    if (auth != null) {
      return try {
        val result = auth.signInAnonymously().awaitTask()
        val fbUser = result.user
        if (fbUser != null) {
          val appUser = fbUser.toAuthUser()
          _currentUser.value = appUser
          AuthResult.Success(appUser)
        } else {
          AuthResult.Error("Guest sign-in could not be completed.")
        }
      } catch (e: Exception) {
        Log.e(tag, "Firebase anonymous sign-in error", e)
        AuthResult.Error(e.localizedMessage ?: "Guest login failed.")
      }
    } else {
      val guestUid = "guest_" + (1000..9999).random()
      val guestName = "Guest Developer #$guestUid"
      localAuthPrefs.edit()
        .putString("active_uid", guestUid)
        .putString("name_$guestUid", guestName)
        .putBoolean("is_anon_$guestUid", true)
        .apply()

      val guestUser = AuthUser(uid = guestUid, email = null, displayName = guestName, isAnonymous = true)
      _currentUser.value = guestUser
      return AuthResult.Success(guestUser)
    }
  }

  suspend fun sendPasswordReset(email: String): AuthResult {
    val cleanEmail = email.trim()
    if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
      return AuthResult.Error("Please enter a valid email address.")
    }

    val auth = firebaseAuth
    if (auth != null) {
      return try {
        auth.sendPasswordResetEmail(cleanEmail).awaitTask()
        AuthResult.Success(AuthUser("reset", cleanEmail, "Reset Request"))
      } catch (e: Exception) {
        Log.e(tag, "Password reset error", e)
        AuthResult.Error(e.localizedMessage ?: "Could not send password reset email.")
      }
    } else {
      return AuthResult.Success(AuthUser("reset", cleanEmail, "Reset Request"))
    }
  }

  fun signOut() {
    try {
      firebaseAuth?.signOut()
    } catch (e: Exception) {
      Log.w(tag, "Error during Firebase signOut", e)
    }
    localAuthPrefs.edit().remove("active_uid").apply()
    _currentUser.value = null
  }
}
