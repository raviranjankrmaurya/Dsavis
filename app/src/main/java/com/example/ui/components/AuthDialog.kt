package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.AuthResult
import com.example.service.AuthUser
import com.example.service.FirebaseAuthService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class AuthMode {
  SIGN_IN,
  SIGN_UP,
  ACCOUNT_DETAILS
}

@Composable
fun AuthDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  authService: FirebaseAuthService,
  currentUser: AuthUser?,
  onUserChanged: (AuthUser?) -> Unit
) {
  if (!isOpen) return

  val isFirebaseActive by authService.isFirebaseActive.collectAsState()
  var authMode by remember(currentUser) {
    mutableStateOf(if (currentUser != null && !currentUser.isAnonymous) AuthMode.ACCOUNT_DETAILS else AuthMode.SIGN_IN)
  }

  var nameInput by remember { mutableStateOf("") }
  var emailInput by remember { mutableStateOf("") }
  var passwordInput by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  var isLoading by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  var isError by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = BgCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderAccent),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PurpleAI.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = CyanBright,
                modifier = Modifier.size(18.dp)
              )
            }

            Column {
              Text(
                text = when (authMode) {
                  AuthMode.SIGN_IN -> "Sign In to DSAFlow"
                  AuthMode.SIGN_UP -> "Create DSAFlow Account"
                  AuthMode.ACCOUNT_DETAILS -> "Firebase Account"
                },
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isFirebaseActive) SuccessGreen else CyanBright)
                )
                Text(
                  text = if (isFirebaseActive) "Firebase Auth Linked" else "Secure Local Auth",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        // Status / Error message
        if (statusMessage != null) {
          Surface(
            color = if (isError) ErrorBg else SuccessBg,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isError) ErrorRed else SuccessGreen),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = statusMessage!!,
              color = if (isError) ErrorRed else SuccessGreen,
              fontSize = 12.sp,
              modifier = Modifier.padding(10.dp)
            )
          }
        }

        if (authMode == AuthMode.ACCOUNT_DETAILS && currentUser != null) {
          // Account Details View
          Surface(
            color = BgInput,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CyanBright.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = currentUser.displayName?.take(1)?.uppercase() ?: "U",
                    color = CyanBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  )
                }

                Column {
                  Text(
                    text = currentUser.displayName ?: "Developer",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Text(
                    text = currentUser.email ?: "Anonymous Guest Session",
                    color = TextSecondary,
                    fontSize = 12.sp
                  )
                }
              }

              Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("User UID:", color = TextMuted, fontSize = 11.sp)
                Text(
                  text = currentUser.uid.take(16) + "...",
                  color = CyanBright,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Sync Status:", color = TextMuted, fontSize = 11.sp)
                Text("Personalized Active", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }

          // Sign Out Button
          Button(
            onClick = {
              authService.signOut()
              onUserChanged(null)
              statusMessage = "Signed out successfully"
              isError = false
              authMode = AuthMode.SIGN_IN
            },
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.2f), contentColor = ErrorRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign Out of Account", fontWeight = FontWeight.Bold)
          }

          TextButton(onClick = onDismiss) {
            Text("Close", color = TextSecondary)
          }
        } else {
          // SIGN IN / SIGN UP FORM
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                authMode = AuthMode.SIGN_IN
                statusMessage = null
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (authMode == AuthMode.SIGN_IN) PurpleAI else BgInput,
                contentColor = if (authMode == AuthMode.SIGN_IN) Color.White else TextMuted
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
              onClick = {
                authMode = AuthMode.SIGN_UP
                statusMessage = null
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (authMode == AuthMode.SIGN_UP) PurpleAI else BgInput,
                contentColor = if (authMode == AuthMode.SIGN_UP) Color.White else TextMuted
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }

          if (authMode == AuthMode.SIGN_UP) {
            TextField(
              value = nameInput,
              onValueChange = { nameInput = it },
              label = { Text("Full Name", fontSize = 12.sp) },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = BgInput,
                unfocusedContainerColor = BgInput,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyanBright,
                unfocusedIndicatorColor = Color.Transparent
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .testTag("auth_name_input")
            )
          }

          TextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email Address", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp)) },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = BgInput,
              unfocusedContainerColor = BgInput,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedIndicatorColor = CyanBright,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .testTag("auth_email_input")
          )

          TextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password visibility",
                  tint = TextMuted
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = BgInput,
              unfocusedContainerColor = BgInput,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedIndicatorColor = CyanBright,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .testTag("auth_password_input")
          )

          if (authMode == AuthMode.SIGN_IN) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Text(
                text = "Forgot password?",
                color = CyanBright,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                  if (emailInput.isNotBlank()) {
                    scope.launch {
                      isLoading = true
                      val res = authService.sendPasswordReset(emailInput)
                      isLoading = false
                      when (res) {
                        is AuthResult.Success -> {
                          statusMessage = "Password reset instructions sent to $emailInput"
                          isError = false
                        }
                        is AuthResult.Error -> {
                          statusMessage = res.message
                          isError = true
                        }
                      }
                    }
                  } else {
                    statusMessage = "Please enter your email above first."
                    isError = true
                  }
                }
              )
            }
          }

          // Main Submit Button
          Button(
            onClick = {
              scope.launch {
                isLoading = true
                statusMessage = null
                val res = if (authMode == AuthMode.SIGN_UP) {
                  authService.signUpWithEmail(emailInput, passwordInput, nameInput)
                } else {
                  authService.signInWithEmail(emailInput, passwordInput)
                }
                isLoading = false

                when (res) {
                  is AuthResult.Success -> {
                    onUserChanged(res.user)
                    statusMessage = "Welcome, ${res.user.displayName}!"
                    isError = false
                    authMode = AuthMode.ACCOUNT_DETAILS
                  }
                  is AuthResult.Error -> {
                    statusMessage = res.message
                    isError = true
                  }
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_submit_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(color = BgDeepDark, modifier = Modifier.size(16.dp))
            } else {
              Text(
                text = if (authMode == AuthMode.SIGN_UP) "Create Account" else "Sign In",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          // Guest Sign In Option
          OutlinedButton(
            onClick = {
              scope.launch {
                isLoading = true
                val res = authService.signInAnonymously()
                isLoading = false
                when (res) {
                  is AuthResult.Success -> {
                    onUserChanged(res.user)
                    statusMessage = "Signed in as Guest"
                    isError = false
                    onDismiss()
                  }
                  is AuthResult.Error -> {
                    statusMessage = res.message
                    isError = true
                  }
                }
              }
            },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Continue as Guest", color = TextSecondary, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
