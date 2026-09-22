package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoadmapStage
import com.example.ui.theme.*

@Composable
fun RoadmapScreen(
  stages: List<RoadmapStage>,
  onBack: () -> Unit,
  onSelectAlgorithm: (String) -> Unit,
  onSelectProblem: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Column {
          Text(
            text = "DSA Learning Roadmap",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Structured visual curriculum from basics to advanced DP.",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }
    }

    // Stages
    items(stages) { stage ->
      Surface(
        color = BgCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(stage.iconEmoji, fontSize = 22.sp)
              Column {
                Text(
                  text = "Stage ${stage.order}: ${stage.title}",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Text(
                  text = stage.subtitle,
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Topics
          Text("Key Topics", color = IndigoAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            stage.topics.take(3).forEach { topic ->
              Surface(
                color = BgInput,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
              ) {
                Text(
                  text = topic,
                  color = TextSecondary,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Visualizer & Practice Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            stage.algorithmIds.firstOrNull()?.let { algoId ->
              Button(
                onClick = { onSelectAlgorithm(algoId) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = BgDeepDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                  Text("Visualize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            stage.problemIds.firstOrNull()?.let { probId ->
              OutlinedButton(
                onClick = { onSelectProblem(probId) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderAccent)
              ) {
                Text("Practice Problem", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
