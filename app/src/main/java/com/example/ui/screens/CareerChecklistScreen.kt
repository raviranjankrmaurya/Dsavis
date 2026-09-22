package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DSADataRepository
import com.example.ui.theme.*

@Composable
fun CareerChecklistScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val initialItems = remember { DSADataRepository.careerChecklist }
  var checkedItemIds by remember {
    mutableStateOf(initialItems.filter { it.isCompleted }.map { it.id }.toSet())
  }

  val completedCount = checkedItemIds.size
  val totalCount = initialItems.size
  val readinessPercentage = ((completedCount.toFloat() / totalCount) * 100).toInt()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BgDeepDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
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
          Text("SWE Career Prep Checklist", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Job readiness from resume to system design.", color = TextMuted, fontSize = 11.sp)
        }
      }
    }

    // Overall Readiness Card
    item {
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
            Text("Career Readiness Score", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("$readinessPercentage%", color = CyanBright, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
          }

          LinearProgressIndicator(
            progress = { completedCount.toFloat() / totalCount },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (readinessPercentage > 70) SuccessGreen else CyanBright,
            trackColor = BgInput
          )

          Text(
            text = "$completedCount of $totalCount items completed",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }
    }

    // Checklist Items grouped by category
    val categories = initialItems.groupBy { it.category }
    categories.forEach { (cat, items) ->
      item {
        Text(
          text = cat.uppercase(),
          color = CyanBright,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          modifier = Modifier.padding(top = 6.dp)
        )
      }

      items(items) { item ->
        val isChecked = checkedItemIds.contains(item.id)
        Surface(
          color = BgCard,
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) SuccessGreen.copy(alpha = 0.4f) else BorderSubtle),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              checkedItemIds = if (isChecked) {
                checkedItemIds - item.id
              } else {
                checkedItemIds + item.id
              }
            }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Checkbox(
              checked = isChecked,
              onCheckedChange = { checked ->
                checkedItemIds = if (checked) checkedItemIds + item.id else checkedItemIds - item.id
              },
              colors = CheckboxDefaults.colors(
                checkedColor = SuccessGreen,
                uncheckedColor = TextMuted,
                checkmarkColor = Color.White
              )
            )

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.title,
                color = if (isChecked) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = item.detail,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }
  }
}
