package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.CodeReviewResult
import com.example.service.GeminiApiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
  val sender: String, // "user" or "mentor"
  val text: String,
  val isCodeReview: Boolean = false,
  val reviewResult: CodeReviewResult? = null
)

enum class MentorMode { CHAT, CODE_REVIEW }

@Composable
fun AIMentorScreen(
  geminiService: GeminiApiService,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var mentorMode by remember { mutableStateOf(MentorMode.CHAT) }
  var userPrompt by remember { mutableStateOf("") }
  var isThinking by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        sender = "mentor",
        text = "Hello! I am **DSA Mentor**, your personal algorithms coach. I'm here to build your intuition with progressive hints, dry-run walkthroughs, and code reviews without spoiling the solution!"
      )
    )
  }

  // Code review specific states
  var pastedCode by remember {
    mutableStateOf(
      """def find_pair(nums, target):
    for i in range(len(nums)):
        for j in range(i + 1, len(nums)):
            if nums[i] + nums[j] == target:
                return [i, j]
    return []"""
    )
  }
  var codeReviewResult by remember { mutableStateOf<CodeReviewResult?>(null) }
  var isReviewing by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
  ) {
    // Mode Switcher Header (Chat Tutor vs Code Reviewer)
    Surface(
      color = BgDark,
      modifier = Modifier.fillMaxWidth(),
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { mentorMode = MentorMode.CHAT },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (mentorMode == MentorMode.CHAT) PurpleAI else BgCard,
            contentColor = if (mentorMode == MentorMode.CHAT) Color.White else TextSecondary
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
            Text("AI DSA Mentor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Button(
          onClick = { mentorMode = MentorMode.CODE_REVIEW },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (mentorMode == MentorMode.CODE_REVIEW) PurpleAI else BgCard,
            contentColor = if (mentorMode == MentorMode.CODE_REVIEW) Color.White else TextSecondary
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
            Text("AI Code Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (mentorMode == MentorMode.CHAT) {
      // 1. Quick Action Pills Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val quickActions = listOf(
          "Explain this",
          "Give me a hint",
          "Why does this work?",
          "Show dry run",
          "What's the complexity?",
          "Find common mistakes",
          "Optimize this"
        )
        quickActions.forEach { action ->
          Surface(
            color = BgCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.clickable {
              messages.add(ChatMessage("user", action))
              isThinking = true
              scope.launch {
                val reply = geminiService.askDsaMentor(action)
                messages.add(ChatMessage("mentor", reply))
                isThinking = false
                listState.animateScrollToItem(messages.size - 1)
              }
            }
          ) {
            Text(
              text = action,
              color = CyanBright,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }

      // 2. Chat Conversation LazyColumn
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
      ) {
        items(messages) { msg ->
          val isUser = msg.sender == "user"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
          ) {
            Surface(
              color = if (isUser) PurpleAI.copy(alpha = 0.35f) else BgCard,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) PurpleGlow else BorderSubtle),
              modifier = Modifier.widthIn(max = 300.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = if (isUser) "You" else "DSA Mentor",
                    color = if (isUser) PurpleGlow else CyanBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = msg.text,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }

        if (isThinking) {
          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(8.dp)
            ) {
              CircularProgressIndicator(color = PurpleGlow, modifier = Modifier.size(16.dp))
              Text("DSA Mentor is thinking...", color = TextMuted, fontSize = 12.sp)
            }
          }
        }
      }

      // 3. User Input Row
      Surface(
        color = BgDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 60.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TextField(
            value = userPrompt,
            onValueChange = { userPrompt = it },
            placeholder = { Text("Ask DSA Mentor a question...", color = TextMuted, fontSize = 13.sp) },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = BgInput,
              unfocusedContainerColor = BgInput,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .testTag("mentor_prompt_input")
          )

          IconButton(
            onClick = {
              if (userPrompt.isNotBlank()) {
                val q = userPrompt
                userPrompt = ""
                messages.add(ChatMessage("user", q))
                isThinking = true
                scope.launch {
                  val reply = geminiService.askDsaMentor(q)
                  messages.add(ChatMessage("mentor", reply))
                  isThinking = false
                  listState.animateScrollToItem(messages.size - 1)
                }
              }
            },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(PurpleAI)
              .testTag("mentor_send_button")
          ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
      }
    } else {
      // CODE REVIEW MODE
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp)
          .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Text(
            text = "Paste Solution for AI Review",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Gemini analyzes correctness, Time/Space complexity, edge cases, and provides optimal code.",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        item {
          TextField(
            value = pastedCode,
            onValueChange = { pastedCode = it },
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
              fontSize = 12.sp
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(8.dp))
              .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
          )
        }

        item {
          Button(
            onClick = {
              isReviewing = true
              scope.launch {
                codeReviewResult = geminiService.reviewCode(pastedCode, "Custom Problem")
                isReviewing = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PurpleAI, contentColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            if (isReviewing) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
            } else {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Analyze Code with AI", fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        if (codeReviewResult != null) {
          val res = codeReviewResult!!
          item {
            Surface(
              color = BgCard,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAI),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                      imageVector = if (res.isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                      contentDescription = null,
                      tint = if (res.isCorrect) SuccessGreen else WarningOrange
                    )
                    Text(
                      text = if (res.isCorrect) "Logic Correct" else "Potential Logic Issue",
                      color = if (res.isCorrect) SuccessGreen else WarningOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    color = BgInput,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                      Text("Time Complexity", color = TextMuted, fontSize = 10.sp)
                      Text(res.timeComplexity, color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                  }

                  Surface(
                    color = BgInput,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                      Text("Space Complexity", color = TextMuted, fontSize = 10.sp)
                      Text(res.spaceComplexity, color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                  }
                }

                Text("Potential Edge Cases & Issues", color = WarningOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                res.potentialIssues.forEach { issue ->
                  Text("• $issue", color = TextSecondary, fontSize = 11.sp)
                }

                Text("Optimizations", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                res.optimizations.forEach { opt ->
                  Text("• $opt", color = TextSecondary, fontSize = 11.sp)
                }

                Text("Recommended Code", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Surface(
                  color = BgInput,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = res.improvedCode,
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
    }
  }
}
