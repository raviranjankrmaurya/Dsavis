package com.example.data.model

enum class ProblemDifficulty(val displayName: String, val xp: Int) {
  EASY("Easy", 10),
  MEDIUM("Medium", 25),
  HARD("Hard", 50)
}

data class ProblemExample(
  val input: String,
  val output: String,
  val explanation: String = ""
)

data class TestCase(
  val input: String,
  val expectedOutput: String,
  val isHidden: Boolean = false
)

data class TestCaseResult(
  val index: Int,
  val input: String,
  val expected: String,
  val actual: String,
  val passed: Boolean,
  val error: String? = null
)

data class ExecutionResult(
  val passedAll: Boolean,
  val testResults: List<TestCaseResult>,
  val executionTimeMs: Long,
  val outputMessage: String
)

data class Problem(
  val id: String,
  val title: String,
  val category: String,
  val difficulty: ProblemDifficulty,
  val acceptanceRate: String,
  val estimatedMinutes: Int,
  val description: String,
  val examples: List<ProblemExample>,
  val constraints: List<String>,
  val starterCode: Map<String, String>,
  val testCases: List<TestCase>,
  val hints: List<String>, // Progressive hints (1: Concept, 2: Approach, 3: Pseudocode, 4: Detailed)
  val solutionExplanation: String,
  val timeComplexity: String,
  val spaceComplexity: String,
  val optimalCode: Map<String, String>
)
