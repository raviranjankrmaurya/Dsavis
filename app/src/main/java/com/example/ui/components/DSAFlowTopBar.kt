package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
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
import com.example.ui.theme.*

@Composable
fun DSAFlowTopBar(
  streakDays: Int,
  totalXp: Int,
  onSearchClick: () -> Unit,
  onCommandPaletteClick: () -> Unit,
  currentUser: com.example.service.AuthUser? = null,
  onAuthClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Surface(
    color = BgDark,
    modifier = modifier.fillMaxWidth(),
    border = WindowInsets(0).let { null }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Logo and Branding
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
              Brush.linearGradient(
                listOf(CyanBright, PurpleAI)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "</>",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "DSA",
              color = CyanBright,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Flow",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          }
          Text(
            text = "Master DSA Visually",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Actions & Badges
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Streak Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WarningBg.copy(alpha = 0.6f))
            .border(1.dp, WarningOrange.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Streak",
            tint = WarningOrange,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "$streakDays d",
            color = WarningOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }

        // XP Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1B4B))
            .border(1.dp, CyanBright.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = "XP",
            tint = CyanBright,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "$totalXp",
            color = CyanBright,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }

        // Search Action
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(BgCard)
            .testTag("top_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search algorithms and problems",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Command Palette Action
        IconButton(
          onClick = onCommandPaletteClick,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(BgCard)
            .testTag("command_palette_button")
        ) {
          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = "Command Palette",
            tint = IndigoAccent,
            modifier = Modifier.size(18.dp)
          )
        }

        // Auth Account Action
        IconButton(
          onClick = onAuthClick,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (currentUser != null && !currentUser.isAnonymous) PurpleAI else BgCard)
            .border(1.dp, if (currentUser != null && !currentUser.isAnonymous) PurpleGlow else BorderSubtle, CircleShape)
            .testTag("top_auth_button")
        ) {
          if (currentUser != null && !currentUser.isAnonymous) {
            Text(
              text = currentUser.displayName?.take(1)?.uppercase() ?: "U",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          } else {
            Text(
              text = "👤",
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }
}
