package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Problem
import com.example.data.model.ProblemDifficulty
import com.example.service.GeminiApiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
  problems: List<Problem>,
  initialProblemId: String? = null,
  solvedProblemIds: Set<String>,
  onSolveProblem: (String) -> Unit,
  geminiService: GeminiApiService,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedCategory by remember { mutableStateOf<String?>(null) }
  var activeProblem by remember {
    mutableStateOf<Problem?>(
      problems.find { it.id == initialProblemId }
    )
  }

  LaunchedEffect(initialProblemId) {
    if (initialProblemId != null) {
      problems.find { it.id == initialProblemId }?.let { activeProblem = it }
    }
  }

  // If a problem is selected, render the Problem Workspace!
  if (activeProblem != null) {
    val problem = activeProblem!!
    var selectedLanguage by remember { mutableStateOf("Python") }
    var currentCode by remember(problem, selectedLanguage) {
      mutableStateOf(problem.starterCode[selectedLanguage] ?: "# Write your solution here")
    }
    var testResults by remember { mutableStateOf<List<Boolean>?>(null) }
    var revealedHintLevel by remember { mutableStateOf(0) }
    var isReviewingAI by remember { mutableStateOf(false) }
    var aiReviewText by remember { mutableStateOf<String?>(null) }
    var showSolution by remember { mutableStateOf(false) }

    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(BgDeepDark)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
      // Top Bar: Back to problems list & Problem Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            IconButton(
              onClick = { activeProblem = null },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(
              text = problem.title,
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
          }

          val diffColor = when (problem.difficulty) {
            ProblemDifficulty.EASY -> SuccessGreen
            ProblemDifficulty.MEDIUM -> WarningOrange
            ProblemDifficulty.HARD -> ErrorRed
          }
          Surface(
            color = diffColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, diffColor.copy(alpha = 0.5f))
          ) {
            Text(
              text = problem.difficulty.displayName,
              color = diffColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Problem Details Card
      item {
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = problem.description,
              color = TextPrimary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )

            // Examples
            Text("Examples", color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            problem.examples.forEachIndexed { idx, ex ->
              Surface(
                color = BgInput,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text("Example ${idx + 1}:", color = IndigoAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  Text("Input: ${ex.input}", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                  Text("Output: ${ex.output}", color = SuccessGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                  if (ex.explanation.isNotEmpty()) {
                    Text("Explanation: ${ex.explanation}", color = TextMuted, fontSize = 10.sp)
                  }
                }
              }
            }

            // Constraints
            Text("Constraints", color = WarningOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            problem.constraints.forEach { c ->
              Text("• $c", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // Code Editor & Language Switcher
      item {
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            // Editor Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
              ) {
                listOf("Python", "Java", "TypeScript").forEach { lang ->
                  val isSelected = selectedLanguage == lang
                  Text(
                    text = lang,
                    color = if (isSelected) CyanBright else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSelected) BgCardElevated else Color.Transparent)
                      .clickable { selectedLanguage = lang }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              // Ask AI button
              Button(
                onClick = {
                  isReviewingAI = true
                  scope.launch {
                    val review = geminiService.reviewCode(currentCode, problem.title)
                    aiReviewText = "Time: ${review.timeComplexity} | Space: ${review.spaceComplexity}\n" +
                        if (review.potentialIssues.isNotEmpty()) "Issues: ${review.potentialIssues.joinToString(", ")}" else "Optimal approach!"
                    isReviewingAI = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAI, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                  Text("AI Review", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Code input field
            TextField(
              value = currentCode,
              onValueChange = { currentCode = it },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = BgInput,
                unfocusedContainerColor = BgInput,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
              ),
              textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp
              ),
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = 260.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                .testTag("code_editor_input")
            )

            if (aiReviewText != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                color = BgCardElevated,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAI),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = aiReviewText!!,
                  color = PurpleGlow,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(8.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Run & Submit Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  // Simulate running test cases
                  testResults = listOf(true, true, true)
                  Toast.makeText(context, "All sample test cases passed!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("run_code_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderAccent)
              ) {
                Text("Run Sample Tests", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  testResults = listOf(true, true, true)
                  onSolveProblem(problem.id)
                  Toast.makeText(context, "🎉 Solution Accepted! +25 XP Earned!", Toast.LENGTH_LONG).show()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("submit_solution_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
              ) {
                Text("Submit Solution", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Test Results View
            if (testResults != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SuccessBg)
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Text("3/3 Tests Passed • Runtime: 42ms • Memory: 14.2 MB", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Hints & Optimal Solution Section
      item {
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Progressive Hints", color = PurpleGlow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              if (revealedHintLevel < problem.hints.size) {
                TextButton(
                  onClick = { revealedHintLevel++ }
                ) {
                  Text("Reveal Next Hint (${revealedHintLevel + 1}/${problem.hints.size})", color = CyanBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            for (i in 0 until revealedHintLevel) {
              Text(
                text = problem.hints[i],
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(BgInput)
                  .padding(8.dp)
              )
            }

            Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Optimal Solution & Analysis", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              TextButton(onClick = { showSolution = !showSolution }) {
                Text(if (showSolution) "Hide Solution" else "View Solution", color = CyanBright, fontSize = 11.sp)
              }
            }

            if (showSolution) {
              Text("Complexity: Time ${problem.timeComplexity} | Space ${problem.spaceComplexity}", color = CyanBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(problem.solutionExplanation, color = TextSecondary, fontSize = 12.sp)
              Surface(
                color = BgInput,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = problem.optimalCode["Python"] ?: "",
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(8.dp)
                )
              }
            }
          }
        }
      }
    }
  } else {
    // Problem Catalog View
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(BgDeepDark)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
      item {
        Text(
          text = "Coding Practice Arena",
          color = TextPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Curated algorithmic problems with multi-language starter code & progressive hints.",
          color = TextSecondary,
          fontSize = 12.sp
        )
      }

      // Category filters
      item {
        val categories = listOf("All", "Arrays", "Stack", "Linked List", "Dynamic Programming", "Binary Tree", "Searching")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { cat ->
            val isSelected = (cat == "All" && selectedCategory == null) || selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = if (cat == "All") null else cat },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanBright,
                selectedLabelColor = BgDeepDark,
                containerColor = BgCard,
                labelColor = TextSecondary
              )
            )
          }
        }
      }

      // Problem items
      val filtered = problems.filter {
        selectedCategory == null || it.category.equals(selectedCategory, ignoreCase = true)
      }

      items(filtered) { prob ->
        val isSolved = solvedProblemIds.contains(prob.id)
        val diffColor = when (prob.difficulty) {
          ProblemDifficulty.EASY -> SuccessGreen
          ProblemDifficulty.MEDIUM -> WarningOrange
          ProblemDifficulty.HARD -> ErrorRed
        }

        Surface(
          color = BgCard,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSolved) SuccessGreen.copy(alpha = 0.5f) else BorderSubtle),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { activeProblem = prob }
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(if (isSolved) SuccessGreen else BgInput),
                contentAlignment = Alignment.Center
              ) {
                if (isSolved) {
                  Icon(Icons.Default.Check, contentDescription = "Solved", tint = Color.White, modifier = Modifier.size(14.dp))
                }
              }

              Column {
                Text(
                  text = prob.title,
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${prob.category} • ${prob.estimatedMinutes} mins • Acceptance: ${prob.acceptanceRate}",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }

            Surface(
              color = diffColor.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = prob.difficulty.displayName,
                color = diffColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
