package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavDestination(val label: String, val icon: ImageVector, val tag: String) {
  HOME("Home", Icons.Default.Home, "nav_home"),
  VISUALIZE("Visualize", Icons.Default.PlayCircle, "nav_visualize"),
  PRACTICE("Practice", Icons.Default.Code, "nav_practice"),
  AI_MENTOR("AI Mentor", Icons.Default.AutoAwesome, "nav_ai_mentor"),
  PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

@Composable
fun DSAFlowBottomNav(
  currentDestination: NavDestination,
  onSelectDestination: (NavDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = BgDark,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle),
    tonalElevation = 8.dp
  ) {
    NavigationBar(
      containerColor = BgDark,
      contentColor = TextPrimary,
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .height(64.dp)
    ) {
      NavDestination.values().forEach { destination ->
        val isSelected = currentDestination == destination
        NavigationBarItem(
          selected = isSelected,
          onClick = { onSelectDestination(destination) },
          icon = {
            Icon(
              imageVector = destination.icon,
              contentDescription = destination.label,
              tint = if (isSelected) {
                if (destination == NavDestination.AI_MENTOR) PurpleGlow else CyanBright
              } else TextMuted
            )
          },
          label = {
            Text(
              text = destination.label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) {
                if (destination == NavDestination.AI_MENTOR) PurpleGlow else CyanBright
              } else TextMuted
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyanBright,
            unselectedIconColor = TextMuted,
            indicatorColor = if (destination == NavDestination.AI_MENTOR) {
              PurpleAI.copy(alpha = 0.2f)
            } else {
              CyanBright.copy(alpha = 0.15f)
            }
          ),
          modifier = Modifier.testTag(destination.tag)
        )
      }
    }
  }
}
