package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Algorithm
import com.example.data.model.Problem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  algorithms: List<Algorithm>,
  problems: List<Problem>,
  onSelectAlgorithm: (Algorithm) -> Unit,
  onSelectProblem: (Problem) -> Unit,
  onSelectDestination: (NavDestination) -> Unit
) {
  if (!isOpen) return

  var query by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = BgDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderAccent),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 500.dp)
        .testTag("global_search_dialog")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Search Input Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BgInput)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = CyanBright,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search algorithms, problems, topics...", color = TextMuted, fontSize = 14.sp) },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("search_text_input")
          )
          if (query.isNotEmpty()) {
            IconButton(onClick = { query = "" }, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Command Shortcuts if query is blank
        if (query.isBlank()) {
          Text(
            text = "QUICK NAVIGATION",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectDestination(NavDestination.VISUALIZE)
                onDismiss()
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Open Algorithm Visualizer", color = TextPrimary, fontSize = 13.sp)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectDestination(NavDestination.PRACTICE)
                onDismiss()
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Code, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Go to Practice Arena", color = TextPrimary, fontSize = 13.sp)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectDestination(NavDestination.AI_MENTOR)
                onDismiss()
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleGlow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Chat with DSA Mentor (AI)", color = TextPrimary, fontSize = 13.sp)
          }
        }

        // Filtered Results
        val filteredAlgos = algorithms.filter {
          it.name.contains(query, ignoreCase = true) || it.category.displayName.contains(query, ignoreCase = true)
        }
        val filteredProblems = problems.filter {
          it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }

        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (filteredAlgos.isNotEmpty()) {
            item {
              Text(
                text = "ALGORITHMS (${filteredAlgos.size})",
                color = CyanBright,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
              )
            }
            items(filteredAlgos) { algo ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    onSelectAlgorithm(algo)
                    onDismiss()
                  }
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyanBright, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(algo.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Text(
                  text = algo.category.displayName,
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          }

          if (filteredProblems.isNotEmpty()) {
            item {
              Text(
                text = "PRACTICE PROBLEMS (${filteredProblems.size})",
                color = SuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
              )
            }
            items(filteredProblems) { prob ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    onSelectProblem(prob)
                    onDismiss()
                  }
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Code, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(prob.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Text(
                  text = prob.difficulty.displayName,
                  color = if (prob.difficulty.name == "EASY") SuccessGreen else WarningOrange,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
