package com.example.data.model

enum class InterviewMode(val title: String, val durationMinutes: Int, val description: String) {
  QUICK("Quick Warmup", 15, "Rapid-fire core DSA concepts & complexity analysis"),
  DSA_TECHNICAL("DSA Technical Screen", 30, "Coding problem with live approach probing & edge-case testing"),
  FULL_MOCK("Company-Style Mock", 45, "Full Big-Tech style interview with follow-up optimization")
}

data class InterviewMessage(
  val id: String,
  val sender: String, // "AI Interviewer" or "Candidate"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isCode: Boolean = false
)

data class InterviewEvaluation(
  val problemSolvingScore: Int, // 0..100
  val dsaKnowledgeScore: Int,
  val codeQualityScore: Int,
  val complexityUnderstandingScore: Int,
  val communicationScore: Int,
  val overallPercentage: Int,
  val strengths: List<String>,
  val areasToImprove: List<String>,
  val recommendationVerdict: String // "Strong Hire", "Hire", "Lean Hire", "Needs Practice"
)
