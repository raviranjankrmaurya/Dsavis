package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.GeminiApiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingAIAssistant(
  currentContext: String = "",
  geminiService: GeminiApiService,
  modifier: Modifier = Modifier
) {
  var isSheetOpen by remember { mutableStateOf(false) }
  var userMessage by remember { mutableStateOf("") }
  var assistantReply by remember { mutableStateOf("Ask DSA Mentor for progressive hints, dry-run explanation, or complexity analysis!") }
  var isLoading by remember { mutableStateOf(false) }
  var currentHintLevel by remember { mutableStateOf(0) }
  val scope = rememberCoroutineScope()

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomEnd
  ) {
    // Floating Action Button
    FloatingActionButton(
      onClick = { isSheetOpen = true },
      containerColor = Color.Transparent,
      elevation = FloatingActionButtonDefaults.elevation(6.dp),
      shape = CircleShape,
      modifier = Modifier
        .padding(end = 16.dp, bottom = 80.dp)
        .size(54.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(listOf(PurpleAI, CyanBright))
        )
        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        .testTag("floating_ai_assistant_button")
    ) {
      Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = "Ask DSA Mentor",
        tint = Color.White,
        modifier = Modifier.size(24.dp)
      )
    }

    // Modal Bottom Sheet for Instant AI Mentorship
    if (isSheetOpen) {
      ModalBottomSheet(
        onDismissRequest = { isSheetOpen = false },
        containerColor = BgDark,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
        ) {
          // Sheet Header
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
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(PurpleAI),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
              Column {
                Text(
                  text = "DSA Mentor",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = if (currentContext.isNotEmpty()) "Context: $currentContext" else "Progressive AI Guidance",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }

            IconButton(onClick = { isSheetOpen = false }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Progressive Hint System Pills
          Text(
            text = "PROGRESSIVE HINTS (NO SPOILERS)",
            color = PurpleGlow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              1 to "Hint 1: Concept",
              2 to "Hint 2: Approach",
              3 to "Hint 3: Pseudocode",
              4 to "Hint 4: Full"
            ).forEach { (lvl, title) ->
              val isSelected = currentHintLevel == lvl
              Surface(
                color = if (isSelected) PurpleAI else BgCard,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanBright else BorderSubtle),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    currentHintLevel = lvl
                    isLoading = true
                    scope.launch {
                      assistantReply = geminiService.askDsaMentor(
                        userPrompt = "Provide level $lvl progressive hint",
                        currentContext = currentContext,
                        hintLevel = lvl
                      )
                      isLoading = false
                    }
                  }
              ) {
                Text(
                  text = title,
                  color = if (isSelected) Color.White else TextSecondary,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Response Display Area
          Surface(
            color = BgInput,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 100.dp, max = 220.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  color = PurpleGlow,
                  modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
                )
              } else {
                Text(
                  text = assistantReply,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Input Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(BgCard)
              .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            TextField(
              value = userMessage,
              onValueChange = { userMessage = it },
              placeholder = { Text("Ask anything or paste code...", color = TextMuted, fontSize = 13.sp) },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              modifier = Modifier.weight(1f)
            )

            IconButton(
              onClick = {
                if (userMessage.isNotBlank()) {
                  val query = userMessage
                  userMessage = ""
                  isLoading = true
                  scope.launch {
                    assistantReply = geminiService.askDsaMentor(
                      userPrompt = query,
                      currentContext = currentContext,
                      hintLevel = 0
                    )
                    isLoading = false
                  }
                }
              },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PurpleAI)
            ) {
              Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}
