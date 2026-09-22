package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.data.model.UserProfile
import com.example.data.repository.DSADataRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  userPrefsRepo: UserPreferencesRepository,
  onOpenCareer: () -> Unit,
  currentUser: com.example.service.AuthUser? = null,
  isFirebaseActive: Boolean = false,
  onOpenAuth: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val badges = DSADataRepository.badges

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
  ) {
    // 1. Profile Header
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(CyanBright, PurpleAI))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text("⚡", fontSize = 28.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = userProfile.name,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Level ${userProfile.level} Algorithmic Master",
            color = CyanBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Stats horizontal row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${userProfile.streakDays} Days", color = WarningOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Streak", color = TextMuted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${userProfile.totalXp}", color = CyanBright, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Total XP", color = TextMuted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${userProfile.solvedProblemIds.size + 83}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Solved", color = TextMuted, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 2. Skill Mastery Breakdown
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("DSA Topic Mastery Breakdown", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

          TopicProgressRow("Arrays & Two Pointers", 0.92f, "92%", SuccessGreen)
          TopicProgressRow("Sorting Algorithms", 0.80f, "80%", CyanBright)
          TopicProgressRow("Stacks & Queues", 0.75f, "75%", CyanBright)
          TopicProgressRow("Binary Trees & BST", 0.60f, "60%", WarningOrange)
          TopicProgressRow("Graph Traversals (BFS/DFS)", 0.45f, "45%", WarningOrange)
          TopicProgressRow("Dynamic Programming", 0.32f, "32%", PurpleGlow)
        }
      }
    }

    // 3. Badges & Achievements Gallery
    item {
      Text("Badges & Achievements", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        badges.forEach { badge ->
          val isUnlocked = userProfile.unlockedBadgeIds.contains(badge.id) || badge.isUnlocked
          Surface(
            color = if (isUnlocked) BgCard else BgInput,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) CyanBright.copy(alpha = 0.4f) else BorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(badge.iconEmoji, fontSize = 24.sp)
              Column(modifier = Modifier.weight(1f)) {
                Text(badge.title, color = if (isUnlocked) TextPrimary else TextMuted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(badge.description, color = TextSecondary, fontSize = 11.sp)
              }
              if (isUnlocked) {
                Text("UNLOCKED", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              } else {
                Text("${badge.currentCount}/${badge.requiredCount}", color = TextMuted, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 4. Firebase Authentication & Personalized Progress Card
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (currentUser != null && !currentUser.isAnonymous) PurpleAI else BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = CyanBright,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Firebase Authentication & Sync",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Surface(
              color = if (isFirebaseActive) SuccessBg else BgInput,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isFirebaseActive) SuccessGreen else BorderSubtle)
            ) {
              Text(
                text = if (isFirebaseActive) "LIVE" else "SANDBOX",
                color = if (isFirebaseActive) SuccessGreen else CyanBright,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          if (currentUser != null && !currentUser.isAnonymous) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(PurpleAI),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = currentUser.displayName?.take(1)?.uppercase() ?: "U",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = currentUser.displayName ?: "Authenticated Developer",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = currentUser.email ?: "Secured Account",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("User UID:", color = TextMuted, fontSize = 11.sp)
              Text(
                text = currentUser.uid.take(16) + "...",
                color = CyanBright,
                fontSize = 11.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
              )
            }

            Button(
              onClick = onOpenAuth,
              colors = ButtonDefaults.buttonColors(containerColor = BgInput, contentColor = TextPrimary),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Manage Firebase Account", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          } else {
            Text(
              text = "Sign in or create an account to securely sync your DSA progress, XP, solved challenges, and streak across sessions.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )

            Button(
              onClick = onOpenAuth,
              colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Sign In / Create Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // 5. Learning Preferences
    item {
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Preferences", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

          // Preferred Language
          Text("Preferred Programming Language:", color = TextSecondary, fontSize = 12.sp)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Python", "Java", "C++", "TypeScript").forEach { lang ->
              val isSelected = userProfile.preferredLanguage == lang
              FilterChip(
                selected = isSelected,
                onClick = { userPrefsRepo.updateLanguage(lang) },
                label = { Text(lang, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = CyanBright,
                  selectedLabelColor = BgDeepDark,
                  containerColor = BgInput,
                  labelColor = TextSecondary
                )
              )
            }
          }

          Divider(color = BorderSubtle)

          // Career checklist navigation
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenCareer() }
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(Icons.Default.Work, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
              Text("Software Engineer Career Checklist", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
          }

          Divider(color = BorderSubtle)

          // Reset Data Button
          TextButton(
            onClick = {
              userPrefsRepo.resetData()
              Toast.makeText(context, "Progress reset to baseline", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
          ) {
            Text("Reset Learning Data", fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@Composable
fun TopicProgressRow(topic: String, progress: Float, percentText: String, color: Color) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(topic, color = TextSecondary, fontSize = 11.sp)
      Text(percentText, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(5.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = color,
      trackColor = BgInput
    )
  }
}
