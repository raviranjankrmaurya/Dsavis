package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.ui.theme.*

@Composable
fun QuizScreen(
  quizzes: List<Quiz>,
  onBack: () -> Unit,
  onAwardXp: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedQuiz by remember { mutableStateOf(quizzes.firstOrNull()) }
  var currentQuestionIdx by remember { mutableStateOf(0) }
  var selectedOptionIdx by remember { mutableStateOf<Int?>(null) }
  var score by remember { mutableStateOf(0) }
  var quizCompleted by remember { mutableStateOf(false) }

  if (selectedQuiz == null) return
  val quiz = selectedQuiz!!
  val questions = quiz.questions
  val currentQuestion = questions.getOrNull(currentQuestionIdx)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
  ) {
    // Top Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Column {
          Text(quiz.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Question ${currentQuestionIdx + 1} of ${questions.size} • Score: $score", color = TextSecondary, fontSize = 12.sp)
        }
      }
    }

    if (!quizCompleted && currentQuestion != null) {
      // Question Card
      item {
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = currentQuestion.topic.uppercase(),
              color = CyanBright,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )

            Text(
              text = currentQuestion.questionText,
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 22.sp
            )
          }
        }
      }

      // Options
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          currentQuestion.options.forEachIndexed { optIdx, optionText ->
            val isChosen = selectedOptionIdx == optIdx
            val isCorrectAnswer = optIdx == currentQuestion.correctOptionIndex
            val hasAnswered = selectedOptionIdx != null

            val borderColor = when {
              hasAnswered && isCorrectAnswer -> SuccessGreen
              hasAnswered && isChosen && !isCorrectAnswer -> ErrorRed
              else -> BorderSubtle
            }
            val bgColor = when {
              hasAnswered && isCorrectAnswer -> SuccessBg
              hasAnswered && isChosen && !isCorrectAnswer -> ErrorBg
              else -> BgCard
            }

            Surface(
              color = bgColor,
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
              modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !hasAnswered) {
                  selectedOptionIdx = optIdx
                  if (optIdx == currentQuestion.correctOptionIndex) {
                    score += 10
                  }
                }
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = optionText,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium
                )

                if (hasAnswered) {
                  if (isCorrectAnswer) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                  } else if (isChosen) {
                    Icon(Icons.Default.Close, contentDescription = "Wrong", tint = ErrorRed, modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }
      }

      // Explanation & Next Button
      if (selectedOptionIdx != null) {
        item {
          Surface(
            color = BgCardElevated,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Explanation", color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(currentQuestion.explanation, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              if (currentQuestionIdx < questions.size - 1) {
                currentQuestionIdx++
                selectedOptionIdx = null
              } else {
                quizCompleted = true
                onAwardXp(score)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (currentQuestionIdx < questions.size - 1) "Next Question" else "Finish Quiz",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    } else {
      // Quiz Finished Screen
      item {
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("🎉", fontSize = 42.sp)
            Text("Quiz Complete!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Your Final Score: $score / ${questions.size * 10}", color = CyanBright, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("+$score XP Earned and added to your profile!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = {
                currentQuestionIdx = 0
                selectedOptionIdx = null
                score = 0
                quizCompleted = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Retake Quiz", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = onBack,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Back to Home", color = TextPrimary)
            }
          }
        }
      }
    }
  }
}
