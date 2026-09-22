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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Algorithm
import com.example.data.model.AlgorithmCategory
import com.example.data.model.VisualizerStep
import com.example.service.GeminiApiService
import com.example.ui.components.AlgorithmCanvas
import com.example.ui.components.VisualizerControls
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VisualizerTab(val label: String) {
  PSEUDOCODE("Pseudocode"),
  CODE_VIEW("Code"),
  COMPLEXITY("Complexity & Notes")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizerScreen(
  algorithms: List<Algorithm>,
  initialAlgorithmId: String? = null,
  geminiService: GeminiApiService,
  onAlgorithmCompleted: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scope = rememberCoroutineScope()

  var selectedCategory by remember { mutableStateOf<AlgorithmCategory?>(null) }
  var currentAlgorithm by remember {
    mutableStateOf(
      algorithms.find { it.id == initialAlgorithmId } ?: algorithms.first()
    )
  }

  // Update if initialAlgorithmId changes
  LaunchedEffect(initialAlgorithmId) {
    if (initialAlgorithmId != null) {
      algorithms.find { it.id == initialAlgorithmId }?.let {
        currentAlgorithm = it
        selectedCategory = it.category
      }
    }
  }

  var currentInput by remember { mutableStateOf(currentAlgorithm.defaultInput) }
  var steps by remember(currentAlgorithm, currentInput) {
    mutableStateOf(currentAlgorithm.stepGenerator(currentInput))
  }
  var currentStepIndex by remember(currentAlgorithm, currentInput) { mutableStateOf(0) }
  var isPlaying by remember { mutableStateOf(false) }
  var playbackSpeed by remember { mutableStateOf(1.0f) }
  var selectedTab by remember { mutableStateOf(VisualizerTab.PSEUDOCODE) }
  var selectedLanguage by remember { mutableStateOf("Python") }
  var showCustomInputDialog by remember { mutableStateOf(false) }
  var customInputText by remember { mutableStateOf(currentInput) }

  // Automatic playback coroutine loop
  LaunchedEffect(isPlaying, currentStepIndex, playbackSpeed) {
    if (isPlaying) {
      if (currentStepIndex < steps.size - 1) {
        val delayMillis = (1000L / playbackSpeed).toLong()
        delay(delayMillis)
        currentStepIndex++
      } else {
        isPlaying = false
        onAlgorithmCompleted(currentAlgorithm.id)
      }
    }
  }

  val activeStep = steps.getOrElse(currentStepIndex) {
    VisualizerStep(1, 1, "Ready")
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
  ) {
    // 1. Category Filter Pills
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { selectedCategory = null },
            label = { Text("All Categories", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CyanBright,
              selectedLabelColor = BgDeepDark,
              containerColor = BgCard,
              labelColor = TextSecondary
            )
          )
        }
        items(AlgorithmCategory.values()) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat.displayName, fontSize = 12.sp) },
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

    // 2. Algorithm Selector Row
    item {
      val filteredList = algorithms.filter {
        selectedCategory == null || it.category == selectedCategory
      }

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(filteredList) { algo ->
          val isCurrent = algo.id == currentAlgorithm.id
          Surface(
            color = if (isCurrent) BgCardElevated else BgCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) CyanBright else BorderSubtle),
            modifier = Modifier.clickable {
              currentAlgorithm = algo
              currentInput = algo.defaultInput
              customInputText = algo.defaultInput
              steps = algo.stepGenerator(algo.defaultInput)
              currentStepIndex = 0
              isPlaying = false
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              if (isCurrent) {
                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(CyanBright))
              }
              Text(
                text = algo.name,
                color = if (isCurrent) CyanBright else TextPrimary,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 3. Header & Input Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = currentAlgorithm.name,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = currentAlgorithm.summary,
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        // Custom Input Trigger
        IconButton(
          onClick = { showCustomInputDialog = true },
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .testTag("custom_input_button")
        ) {
          Icon(Icons.Default.Tune, contentDescription = "Custom Input", tint = CyanBright, modifier = Modifier.size(18.dp))
        }
      }
    }

    // 4. Interactive Canvas
    item {
      AlgorithmCanvas(
        visualType = currentAlgorithm.visualType,
        step = activeStep
      )
    }

    // 5. Visualizer Playback Controls
    item {
      VisualizerControls(
        step = activeStep,
        isPlaying = isPlaying,
        speed = playbackSpeed,
        onPlayPauseToggle = { isPlaying = !isPlaying },
        onNextStep = {
          if (currentStepIndex < steps.size - 1) currentStepIndex++
        },
        onPrevStep = {
          if (currentStepIndex > 0) currentStepIndex--
        },
        onRestart = {
          currentStepIndex = 0
          isPlaying = false
        },
        onSpeedChange = { playbackSpeed = it }
      )
    }

    // 6. Secondary Tabs (Pseudocode | Code | Complexity)
    item {
      TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = BgCard,
        contentColor = CyanBright,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
      ) {
        VisualizerTab.values().forEach { tab ->
          Tab(
            selected = selectedTab == tab,
            onClick = { selectedTab = tab },
            text = {
              Text(
                text = tab.label,
                fontSize = 12.sp,
                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
              )
            }
          )
        }
      }
    }

    // Tab Contents
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        when (selectedTab) {
          VisualizerTab.PSEUDOCODE -> {
            Column(modifier = Modifier.padding(14.dp)) {
              currentAlgorithm.pseudocode.forEachIndexed { lineIdx, line ->
                val isActive = lineIdx == activeStep.activePseudocodeLine
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) PurpleAI.copy(alpha = 0.35f) else Color.Transparent)
                    .border(1.dp, if (isActive) PurpleGlow else Color.Transparent, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${lineIdx + 1}  ",
                    color = if (isActive) CyanBright else TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = line,
                    color = if (isActive) Color.White else TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }

          VisualizerTab.CODE_VIEW -> {
            Column(modifier = Modifier.padding(14.dp)) {
              // Language Selector & Copy button
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Languages
                Row(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                  listOf("Python", "Java", "C++", "TypeScript", "JavaScript").forEach { lang ->
                    val isLangSelected = selectedLanguage == lang
                    Text(
                      text = lang,
                      color = if (isLangSelected) CyanBright else TextMuted,
                      fontSize = 11.sp,
                      fontWeight = if (isLangSelected) FontWeight.Bold else FontWeight.Medium,
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isLangSelected) BgCardElevated else Color.Transparent)
                        .clickable { selectedLanguage = lang }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                // Copy Button
                IconButton(
                  onClick = {
                    val code = currentAlgorithm.codeImplementations[selectedLanguage] ?: ""
                    clipboardManager.setText(AnnotatedString(code))
                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(30.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Code content
              val activeCode = currentAlgorithm.codeImplementations[selectedLanguage]
                ?: currentAlgorithm.codeImplementations.values.firstOrNull() ?: "# Code not available"

              Surface(
                color = BgInput,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = activeCode,
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  lineHeight = 16.sp,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }

          VisualizerTab.COMPLEXITY -> {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Complexity Grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ComplexityBadge("Best Time", currentAlgorithm.timeComplexityBest, SuccessGreen, Modifier.weight(1f))
                ComplexityBadge("Avg Time", currentAlgorithm.timeComplexityAvg, WarningOrange, Modifier.weight(1f))
                ComplexityBadge("Worst Time", currentAlgorithm.timeComplexityWorst, ErrorRed, Modifier.weight(1f))
                ComplexityBadge("Space", currentAlgorithm.spaceComplexity, CyanBright, Modifier.weight(1f))
              }

              Text(
                text = "Detailed Explanation",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = currentAlgorithm.explanation,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )

              Text(
                text = "Common Pitfalls & Mistakes",
                color = WarningOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              currentAlgorithm.commonMistakes.forEach { mistake ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text("•", color = WarningOrange, fontWeight = FontWeight.Bold)
                  Text(mistake, color = TextSecondary, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  // Custom Input Dialog
  if (showCustomInputDialog) {
    AlertDialog(
      onDismissRequest = { showCustomInputDialog = false },
      title = { Text("Customize Input", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Enter comma-separated values (e.g. 5, 2, 8, 1, 9; target)",
            color = TextSecondary,
            fontSize = 12.sp
          )
          TextField(
            value = customInputText,
            onValueChange = { customInputText = it },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = BgInput,
              unfocusedContainerColor = BgInput,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            currentInput = customInputText
            steps = currentAlgorithm.stepGenerator(currentInput)
            currentStepIndex = 0
            isPlaying = false
            showCustomInputDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark)
        ) {
          Text("Apply & Visualize", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCustomInputDialog = false }) {
          Text("Cancel", color = TextMuted)
        }
      },
      containerColor = BgDark
    )
  }
}

@Composable
fun ComplexityBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Surface(
    color = BgCardElevated,
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
  }
}
