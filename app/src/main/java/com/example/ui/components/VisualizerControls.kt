package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VisualizerStep
import com.example.ui.theme.*

@Composable
fun VisualizerControls(
  step: VisualizerStep,
  isPlaying: Boolean,
  speed: Float,
  onPlayPauseToggle: () -> Unit,
  onNextStep: () -> Unit,
  onPrevStep: () -> Unit,
  onRestart: () -> Unit,
  onSpeedChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Current Step Status & Operation Description
    Surface(
      color = BgCard,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isPlaying) SuccessGreen else CyanBright)
            )
            Text(
              text = if (step.totalSteps > 0) "Step ${step.stepNumber} of ${step.totalSteps}" else "Step ${step.stepNumber}",
              color = CyanBright,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }

          if (step.activePseudocodeLine >= 0) {
            Text(
              text = "Line: ${step.activePseudocodeLine + 1}",
              color = IndigoAccent,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = step.description,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          lineHeight = 18.sp
        )
      }
    }

    // Playback Controls Row
    Surface(
      color = BgCardElevated,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Restart Button
        IconButton(
          onClick = onRestart,
          modifier = Modifier.testTag("vis_restart_button")
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = "Restart",
            tint = TextSecondary,
            modifier = Modifier.size(22.dp)
          )
        }

        // Previous Step
        IconButton(
          onClick = onPrevStep,
          enabled = step.stepNumber > 1,
          modifier = Modifier.testTag("vis_prev_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Previous Step",
            tint = if (step.stepNumber > 1) TextPrimary else TextMuted,
            modifier = Modifier.size(24.dp)
          )
        }

        // Play / Pause Primary Button
        FloatingActionButton(
          onClick = onPlayPauseToggle,
          containerColor = CyanBright,
          contentColor = BgDeepDark,
          modifier = Modifier
            .size(46.dp)
            .testTag("vis_play_pause_button"),
          shape = CircleShape
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier.size(26.dp)
          )
        }

        // Next Step
        IconButton(
          onClick = onNextStep,
          enabled = step.stepNumber < step.totalSteps,
          modifier = Modifier.testTag("vis_next_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Next Step",
            tint = if (step.stepNumber < step.totalSteps) TextPrimary else TextMuted,
            modifier = Modifier.size(24.dp)
          )
        }

        // Speed Selector
        TextButton(
          onClick = {
            val nextSpeed = when (speed) {
              0.5f -> 1.0f
              1.0f -> 2.0f
              else -> 0.5f
            }
            onSpeedChange(nextSpeed)
          },
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .testTag("vis_speed_toggle")
        ) {
          Text(
            text = "${speed}x",
            color = CyanBright,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
