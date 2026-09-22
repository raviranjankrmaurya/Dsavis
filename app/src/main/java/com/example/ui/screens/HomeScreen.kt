package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Algorithm
import com.example.data.model.Problem
import com.example.data.model.UserProfile
import com.example.ui.components.NavDestination
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  onNavigateToVisualizer: (String?) -> Unit,
  onNavigateToPractice: (String?) -> Unit,
  onNavigateToDestination: (NavDestination) -> Unit,
  onOpenRoadmap: () -> Unit,
  onOpenQuiz: () -> Unit,
  onOpenInterview: () -> Unit,
  onOpenCareer: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
  ) {
    // 1. Hero Section
    item {
      Surface(
        color = BgDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.radialGradient(
                colors = listOf(CyanBright.copy(alpha = 0.12f), Color.Transparent),
                radius = 500f
              )
            )
            .padding(18.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(CyanBright)
              )
              Text(
                text = "INTERACTIVE LEARNING ENGINE",
                color = CyanBright,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Master DSA visually.",
              color = TextPrimary,
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Understand algorithms by watching them work step-by-step.",
              color = TextSecondary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    // 2. Personal Stats Dashboard Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Streak Card
        StatCard(
          icon = "🔥",
          value = "${userProfile.streakDays} Day",
          label = "Current Streak",
          color = WarningOrange,
          modifier = Modifier.weight(1f)
        )
        // XP Card
        StatCard(
          icon = "⚡",
          value = "${userProfile.totalXp} XP",
          label = "Level ${userProfile.level} Adept",
          color = CyanBright,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Solved Card
        StatCard(
          icon = "✓",
          value = "${userProfile.solvedProblemIds.size + 83}",
          label = "Problems Solved",
          color = SuccessGreen,
          modifier = Modifier.weight(1f)
        )
        // Progress Card
        StatCard(
          icon = "🎯",
          value = "68%",
          label = "DSA Mastery",
          color = PurpleGlow,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Continue Learning Card
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderAccent),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "CONTINUE LEARNING",
                color = CyanBright,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Binary Search & Pointers",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "65%",
              color = CyanBright,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Progress Bar
          LinearProgressIndicator(
            progress = { 0.65f },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = CyanBright,
            trackColor = BgInput
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { onNavigateToVisualizer("binary-search") },
            colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("continue_learning_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
              Text("Continue Visualizing", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 4. Today's Challenge Card
    item {
      Surface(
        color = BgCardElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAI.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(text = "⭐", fontSize = 16.sp)
              Text(
                text = "TODAY'S DSA CHALLENGE",
                color = PurpleGlow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "+25 XP • 🔥 +1 Streak",
              color = WarningOrange,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Climbing Stairs (Dynamic Programming)",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "Difficulty: Easy • 15 min estimated • Tabulation & Fibonacci approach",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
          )

          Button(
            onClick = { onNavigateToPractice("climbing-stairs") },
            colors = ButtonDefaults.buttonColors(containerColor = PurpleAI, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("start_daily_challenge_button")
          ) {
            Text("Start Challenge", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 5. Explore DSA Hub Grid Shortcuts
    item {
      Text(
        text = "EXPLORE DSA PLATFORM",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 4.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        HubButton(
          title = "Roadmap",
          subtitle = "Visual Path",
          icon = "🗺️",
          onClick = onOpenRoadmap,
          modifier = Modifier.weight(1f)
        )
        HubButton(
          title = "Quizzes",
          subtitle = "Test Intuition",
          icon = "❓",
          onClick = onOpenQuiz,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        HubButton(
          title = "Interview Arena",
          subtitle = "Timed Mocks",
          icon = "🎯",
          onClick = onOpenInterview,
          modifier = Modifier.weight(1f)
        )
        HubButton(
          title = "Career Prep",
          subtitle = "Job Ready",
          icon = "💼",
          onClick = onOpenCareer,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 6. Recommended For You Carousel
    item {
      Text(
        text = "RECOMMENDED ALGORITHMS",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          RecommendationCard(
            title = "Quick Sort Partitioning",
            category = "Sorting",
            time = "O(n log n)",
            tag = "Core Pattern",
            color = CyanBright,
            onClick = { onNavigateToVisualizer("quick-sort") }
          )
        }
        item {
          RecommendationCard(
            title = "Breadth First Search",
            category = "Graph Traversal",
            time = "O(V + E)",
            tag = "Shortest Path",
            color = SuccessGreen,
            onClick = { onNavigateToVisualizer("graph-bfs") }
          )
        }
        item {
          RecommendationCard(
            title = "Fibonacci DP Tabulation",
            category = "Dynamic Prog",
            time = "O(n)",
            tag = "Weak Topic (32%)",
            color = WarningOrange,
            onClick = { onNavigateToVisualizer("fibonacci-dp") }
          )
        }
      }
    }
  }
}

@Composable
fun StatCard(
  icon: String,
  value: String,
  label: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    color = BgCard,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(text = icon, fontSize = 20.sp)
      Column {
        Text(
          text = value,
          color = color,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = label,
          color = TextSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun HubButton(
  title: String,
  subtitle: String,
  icon: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = BgCard,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(text = icon, fontSize = 20.sp)
      Column {
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = subtitle,
          color = TextMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun RecommendationCard(
  title: String,
  category: String,
  time: String,
  tag: String,
  color: Color,
  onClick: () -> Unit
) {
  Surface(
    color = BgCard,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = Modifier
      .width(220.dp)
      .clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = category,
          color = color,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = time,
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(color.copy(alpha = 0.15f))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      ) {
        Text(
          text = tag,
          color = color,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
