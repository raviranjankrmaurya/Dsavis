package com.example.data.model

data class Badge(
  val id: String,
  val title: String,
  val description: String,
  val iconEmoji: String,
  val category: String,
  val requiredCount: Int,
  val currentCount: Int,
  val isUnlocked: Boolean
)

data class RoadmapStage(
  val id: String,
  val title: String,
  val subtitle: String,
  val order: Int,
  val iconEmoji: String,
  val topics: List<String>,
  val algorithmIds: List<String>,
  val problemIds: List<String>,
  val quizId: String? = null
)

data class CareerItem(
  val id: String,
  val category: String, // "Resume", "GitHub", "Projects", "CS Fundamentals", "Interview Prep"
  val title: String,
  val detail: String,
  val isCompleted: Boolean = false
)

data class UserProfile(
  val name: String = "DSA Explorer",
  val streakDays: Int = 12,
  val totalXp: Int = 2450,
  val level: Int = 5,
  val solvedProblemIds: Set<String> = setOf("two-sum", "reverse-linked-list", "valid-parentheses", "binary-search"),
  val completedAlgorithmIds: Set<String> = setOf("binary-search", "bubble-sort", "stack-operations"),
  val unlockedBadgeIds: Set<String> = setOf("first-step", "streak-7", "sorting-starter"),
  val lastChallengeDate: String = "2026-09-21",
  val preferredLanguage: String = "Python",
  val animationSpeed: Float = 1.0f,
  val weakTopics: Map<String, Int> = mapOf(
    "Dynamic Programming" to 32,
    "Graphs" to 45,
    "Trees" to 71,
    "Arrays" to 88,
    "Sorting" to 92
  )
)
