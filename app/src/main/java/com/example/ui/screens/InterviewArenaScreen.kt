package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.GeminiApiService
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InterviewArenaScreen(
  onBack: () -> Unit,
  geminiService: GeminiApiService,
  modifier: Modifier = Modifier
) {
  var remainingSeconds by remember { mutableStateOf(45 * 60) }
  var isTimerRunning by remember { mutableStateOf(true) }
  var candidateCode by remember {
    mutableStateOf(
      """def twoSum(nums: list[int], target: int) -> list[int]:
    # Write optimal solution
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []"""
    )
  }
  var isEvaluating by remember { mutableStateOf(false) }
  var interviewResult by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(isTimerRunning) {
    while (isTimerRunning && remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds--
    }
  }

  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
  ) {
    // Header & Timer
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
          }
          Column {
            Text("Technical Interview Simulation", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Google / Meta Style • DSA Round", color = TextMuted, fontSize = 11.sp)
          }
        }

        // Timer Pill
        Surface(
          color = if (remainingSeconds < 300) ErrorBg else BgCardElevated,
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (remainingSeconds < 300) ErrorRed else CyanBright)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = if (remainingSeconds < 300) ErrorRed else CyanBright, modifier = Modifier.size(16.dp))
            Text(timeFormatted, color = if (remainingSeconds < 300) ErrorRed else CyanBright, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
          }
        }
      }
    }

    // Interviewer Prompt Card
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PurpleAI))
            Text("INTERVIEWER PROMPT", color = PurpleGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
          Text(
            text = "Problem: Two Sum with Memory & Edge Constraints",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "Given an array of integers nums and an integer target, return indices of two numbers adding to target. You cannot use the same element twice. What is your time and space complexity strategy?",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      }
    }

    // Candidate Code Input
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Candidate Solution (Python)", color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          TextField(
            value = candidateCode,
            onValueChange = { candidateCode = it },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = BgInput,
              unfocusedContainerColor = BgInput,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(8.dp))
          )
        }
      }
    }

    // Submit to Interviewer Action
    item {
      Button(
        onClick = {
          isEvaluating = true
          isTimerRunning = false
          scope.launch {
            val eval = geminiService.askDsaMentor(
              userPrompt = "Act as senior technical interviewer evaluating this candidate code: $candidateCode. Give structured score on Correctness, Time/Space efficiency, and hire recommendation.",
              currentContext = "Two Sum Interview",
              hintLevel = 0
            )
            interviewResult = eval
            isEvaluating = false
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        if (isEvaluating) {
          CircularProgressIndicator(color = BgDeepDark, modifier = Modifier.size(16.dp))
        } else {
          Text("Submit Solution to Interviewer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Interview Feedback Result Card
    if (interviewResult != null) {
      item {
        Surface(
          color = BgCardElevated,
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
              Text("Interviewer Evaluation & Feedback", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text(
              text = interviewResult!!,
              color = TextPrimary,
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
          }
        }
      }
    }
  }
}
