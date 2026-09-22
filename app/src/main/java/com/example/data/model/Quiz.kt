package com.example.data.model

enum class QuestionType(val label: String) {
  MCQ("Multiple Choice"),
  OUTPUT_PREDICTION("Output Prediction"),
  COMPLEXITY("Complexity Identification"),
  DEBUGGING("Code Debugging"),
  TRUE_FALSE("True / False")
}

data class QuizQuestion(
  val id: String,
  val topic: String,
  val questionText: String,
  val codeSnippet: String? = null,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String,
  val type: QuestionType
)

data class Quiz(
  val id: String,
  val title: String,
  val category: String,
  val description: String,
  val questions: List<QuizQuestion>
)

data class QuizResult(
  val quizId: String,
  val score: Int,
  val totalQuestions: Int,
  val percentage: Int,
  val userAnswers: Map<Int, Int>,
  val weakTopics: List<String>,
  val recommendedTopics: List<String>
)
