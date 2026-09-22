package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("dsaflow_user_prefs", Context.MODE_PRIVATE)

  private var activeUid: String = prefs.getString("current_active_uid", "default_user") ?: "default_user"
  private var activeDisplayName: String = prefs.getString("current_display_name", "DSA Explorer") ?: "DSA Explorer"

  private val _userProfile = MutableStateFlow(loadProfile(activeUid, activeDisplayName))
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  fun switchUser(uid: String?, displayName: String?) {
    val newUid = uid ?: "default_user"
    val newName = displayName ?: if (uid != null) "User ${uid.take(6)}" else "DSA Explorer"
    activeUid = newUid
    activeDisplayName = newName

    prefs.edit()
      .putString("current_active_uid", newUid)
      .putString("current_display_name", newName)
      .apply()

    _userProfile.value = loadProfile(newUid, newName)
  }

  private fun loadProfile(uid: String, name: String): UserProfile {
    val prefix = "${uid}_"
    val streak = prefs.getInt("${prefix}streak_days", if (uid == "default_user") 12 else 1)
    val xp = prefs.getInt("${prefix}total_xp", if (uid == "default_user") 2450 else 0)
    val level = prefs.getInt("${prefix}level", if (uid == "default_user") 5 else 1)
    val solved = prefs.getStringSet(
      "${prefix}solved_problems",
      if (uid == "default_user") setOf("two-sum", "reverse-linked-list", "valid-parentheses", "binary-search") else emptySet()
    ) ?: emptySet()
    val completed = prefs.getStringSet(
      "${prefix}completed_algorithms",
      if (uid == "default_user") setOf("binary-search", "bubble-sort", "stack-operations") else emptySet()
    ) ?: emptySet()
    val badges = prefs.getStringSet(
      "${prefix}unlocked_badges",
      if (uid == "default_user") setOf("first-step", "streak-7", "sorting-starter") else setOf("first-step")
    ) ?: setOf("first-step")
    val lastDate = prefs.getString("${prefix}last_challenge_date", "2026-09-21") ?: ""
    val lang = prefs.getString("${prefix}preferred_lang", "Python") ?: "Python"
    val speed = prefs.getFloat("${prefix}animation_speed", 1.0f)

    return UserProfile(
      name = name,
      streakDays = streak,
      totalXp = xp,
      level = level,
      solvedProblemIds = solved,
      completedAlgorithmIds = completed,
      unlockedBadgeIds = badges,
      lastChallengeDate = lastDate,
      preferredLanguage = lang,
      animationSpeed = speed
    )
  }

  fun addXp(amount: Int) {
    val current = _userProfile.value
    val newXp = current.totalXp + amount
    val newLevel = (newXp / 500) + 1
    val prefix = "${activeUid}_"

    prefs.edit()
      .putInt("${prefix}total_xp", newXp)
      .putInt("${prefix}level", newLevel)
      .apply()

    _userProfile.value = current.copy(totalXp = newXp, level = newLevel)
  }

  fun markProblemSolved(problemId: String, xpAward: Int = 25) {
    val current = _userProfile.value
    val newSolved = current.solvedProblemIds + problemId
    val newXp = current.totalXp + xpAward
    val newLevel = (newXp / 500) + 1
    val prefix = "${activeUid}_"

    // Check for badges
    val newBadges = current.unlockedBadgeIds.toMutableSet()
    if (newSolved.size >= 1) newBadges.add("first-step")
    if (newSolved.size >= 5) newBadges.add("streak-7")
    if (newSolved.size >= 10) newBadges.add("speed-demon")

    prefs.edit()
      .putStringSet("${prefix}solved_problems", newSolved)
      .putStringSet("${prefix}unlocked_badges", newBadges)
      .putInt("${prefix}total_xp", newXp)
      .putInt("${prefix}level", newLevel)
      .apply()

    _userProfile.value = current.copy(
      solvedProblemIds = newSolved,
      unlockedBadgeIds = newBadges,
      totalXp = newXp,
      level = newLevel
    )
  }

  fun markAlgorithmCompleted(algorithmId: String) {
    val current = _userProfile.value
    val newCompleted = current.completedAlgorithmIds + algorithmId
    val prefix = "${activeUid}_"

    val newBadges = current.unlockedBadgeIds.toMutableSet()
    if (algorithmId.contains("sort")) newBadges.add("sorting-starter")
    if (algorithmId.contains("tree") || algorithmId.contains("graph")) newBadges.add("tree-climber")
    if (algorithmId.contains("fibonacci")) newBadges.add("dp-architect")

    prefs.edit()
      .putStringSet("${prefix}completed_algorithms", newCompleted)
      .putStringSet("${prefix}unlocked_badges", newBadges)
      .apply()

    _userProfile.value = current.copy(
      completedAlgorithmIds = newCompleted,
      unlockedBadgeIds = newBadges
    )
  }

  fun completeDailyChallenge(problemId: String, xp: Int = 25) {
    val current = _userProfile.value
    val today = "2026-09-22"
    val newStreak = current.streakDays + 1
    val newXp = current.totalXp + xp
    val newSolved = current.solvedProblemIds + problemId
    val prefix = "${activeUid}_"

    prefs.edit()
      .putInt("${prefix}streak_days", newStreak)
      .putInt("${prefix}total_xp", newXp)
      .putString("${prefix}last_challenge_date", today)
      .putStringSet("${prefix}solved_problems", newSolved)
      .apply()

    _userProfile.value = current.copy(
      streakDays = newStreak,
      totalXp = newXp,
      lastChallengeDate = today,
      solvedProblemIds = newSolved
    )
  }

  fun updateLanguage(lang: String) {
    val prefix = "${activeUid}_"
    prefs.edit().putString("${prefix}preferred_lang", lang).apply()
    _userProfile.value = _userProfile.value.copy(preferredLanguage = lang)
  }

  fun updateSpeed(speed: Float) {
    val prefix = "${activeUid}_"
    prefs.edit().putFloat("${prefix}animation_speed", speed).apply()
    _userProfile.value = _userProfile.value.copy(animationSpeed = speed)
  }

  fun resetData() {
    val prefix = "${activeUid}_"
    prefs.edit()
      .remove("${prefix}streak_days")
      .remove("${prefix}total_xp")
      .remove("${prefix}level")
      .remove("${prefix}solved_problems")
      .remove("${prefix}completed_algorithms")
      .remove("${prefix}unlocked_badges")
      .apply()

    _userProfile.value = UserProfile(
      name = activeDisplayName,
      streakDays = 1,
      totalXp = 0,
      level = 1,
      solvedProblemIds = emptySet(),
      completedAlgorithmIds = emptySet(),
      unlockedBadgeIds = setOf("first-step")
    )
  }
}
