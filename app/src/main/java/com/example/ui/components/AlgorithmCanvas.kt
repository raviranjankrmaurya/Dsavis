package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AlgorithmCanvas(
  visualType: VisualizationType,
  step: VisualizerStep,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(260.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(BgInput)
      .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    when (visualType) {
      VisualizationType.ARRAY_BARS,
      VisualizationType.ARRAY_POINTERS -> {
        ArrayVisualizer(
          data = step.arrayData,
          highlights = step.highlights,
          pointers = step.pointers
        )
      }
      VisualizationType.STACK_VIEW -> {
        StackVisualizer(stackData = step.stackData)
      }
      VisualizationType.QUEUE_VIEW -> {
        QueueVisualizer(queueData = step.queueData)
      }
      VisualizationType.TREE_VIEW -> {
        TreeVisualizer(nodes = step.treeNodes)
      }
      VisualizationType.GRAPH_VIEW -> {
        GraphVisualizer(nodes = step.graphNodes, edges = step.graphEdges)
      }
      VisualizationType.LINKED_LIST_VIEW -> {
        LinkedListVisualizer(
          data = step.arrayData,
          pointers = step.pointers,
          highlights = step.highlights
        )
      }
      VisualizationType.DP_GRID -> {
        DpGridVisualizer(
          rowHeaders = step.dpRowHeaders,
          colHeaders = step.dpColHeaders,
          table = step.dpTable,
          activeCell = step.activeTableCell
        )
      }
    }
  }
}

@Composable
fun ArrayVisualizer(
  data: List<Int>,
  highlights: Map<Int, HighlightType>,
  pointers: Map<String, Int>
) {
  if (data.isEmpty()) {
    Text(text = "Empty Array", color = TextMuted, fontSize = 14.sp)
    return
  }

  val maxVal = (data.maxOrNull() ?: 1).coerceAtLeast(1)

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Bottom,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Array Bars
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.Bottom
    ) {
      data.forEachIndexed { index, value ->
        val hlType = highlights[index] ?: HighlightType.DEFAULT
        val barColor by animateColorAsState(
          targetValue = when (hlType) {
            HighlightType.COMPARING -> VisComparing
            HighlightType.SWAPPING -> VisSwapping
            HighlightType.SORTED -> VisSorted
            HighlightType.PIVOT -> VisPivot
            HighlightType.VISITED -> VisVisited
            HighlightType.CURRENT -> VisCurrent
            HighlightType.DEFAULT -> VisDefault
          },
          animationSpec = tween(250), label = "barColor"
        )

        val heightRatio = (value.toFloat() / maxVal.toFloat()).coerceIn(0.15f, 1.0f)
        val targetHeight = (heightRatio * 150).dp
        val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = tween(250), label = "barHeight")

        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          verticalArrangement = Arrangement.Bottom,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Value text above bar
          Text(
            text = "$value",
            color = if (hlType != HighlightType.DEFAULT) Color.White else TextSecondary,
            fontSize = if (data.size > 8) 9.sp else 11.sp,
            fontWeight = if (hlType != HighlightType.DEFAULT) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(bottom = 2.dp)
          )

          // The Bar itself
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(animatedHeight)
              .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
              .background(
                Brush.verticalGradient(
                  listOf(barColor, barColor.copy(alpha = 0.6f))
                )
              )
              .border(
                1.dp,
                if (hlType != HighlightType.DEFAULT) Color.White.copy(alpha = 0.8f) else Color.Transparent,
                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
              )
          )

          // Index label beneath bar
          Text(
            text = "[$index]",
            color = TextMuted,
            fontSize = if (data.size > 8) 8.sp else 9.sp,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }

    // Pointer Tags (e.g. low, mid, high, i, j)
    if (pointers.isNotEmpty()) {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(BgCard)
          .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        pointers.forEach { (ptrName, ptrIdx) ->
          val badgeColor = when (ptrName.lowercase()) {
            "low", "i" -> CyanBright
            "mid", "pivot" -> PurpleGlow
            "high", "j", "j+1" -> WarningOrange
            "target" -> SuccessGreen
            else -> IndigoAccent
          }
          Text(
            text = "$ptrName: $ptrIdx   ",
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

@Composable
fun StackVisualizer(stackData: List<String>) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom
  ) {
    Text(
      text = "LIFO Container (Top ->)",
      color = TextMuted,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    Box(
      modifier = Modifier
        .width(170.dp)
        .weight(1f)
        .border(
          width = 2.dp,
          color = CyanBright.copy(alpha = 0.6f),
          shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
        )
        .padding(6.dp),
      contentAlignment = Alignment.BottomCenter
    ) {
      if (stackData.isEmpty()) {
        Text(text = "[ Empty Stack ]", color = TextMuted, fontSize = 12.sp)
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          stackData.asReversed().forEachIndexed { index, item ->
            val isTop = index == 0
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(if (isTop) PurpleAI else BgCardElevated)
                .border(1.dp, if (isTop) CyanBright else BorderSubtle, RoundedCornerShape(6.dp))
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = item,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              if (isTop) {
                Text(
                  text = "  ← TOP",
                  color = CyanBright,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun QueueVisualizer(queueData: List<String>) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "FIFO Queue Corridor",
      color = TextMuted,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 2.dp,
          color = IndigoAccent.copy(alpha = 0.5f),
          shape = RoundedCornerShape(8.dp)
        )
        .padding(horizontal = 8.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "OUT ◄",
        color = ErrorRed,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )

      if (queueData.isEmpty()) {
        Text(text = "[ Empty Queue ]", color = TextMuted, fontSize = 12.sp)
      } else {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          queueData.forEachIndexed { idx, item ->
            val isHead = idx == 0
            val isTail = idx == queueData.size - 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isHead) SuccessGreen.copy(alpha = 0.8f) else if (isTail) CyanBright.copy(alpha = 0.8f) else BgCardElevated)
                  .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = item,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Text(
                text = if (isHead) "FRONT" else if (isTail) "REAR" else "",
                color = if (isHead) SuccessGreen else CyanBright,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Text(
        text = "◄ IN",
        color = SuccessGreen,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun TreeVisualizer(nodes: List<TreeNodeVisual>) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    // Draw branch connecting lines
    nodes.forEach { node ->
      val start = Offset(node.x * width, node.y * height)
      node.leftId?.let { lId ->
        nodes.find { it.id == lId }?.let { lNode ->
          val end = Offset(lNode.x * width, lNode.y * height)
          drawLine(
            color = BorderAccent,
            start = start,
            end = end,
            strokeWidth = 3f
          )
        }
      }
      node.rightId?.let { rId ->
        nodes.find { it.id == rId }?.let { rNode ->
          val end = Offset(rNode.x * width, rNode.y * height)
          drawLine(
            color = BorderAccent,
            start = start,
            end = end,
            strokeWidth = 3f
          )
        }
      }
    }

    // Draw node circles
    nodes.forEach { node ->
      val center = Offset(node.x * width, node.y * height)
      val nodeColor = when (node.state) {
        HighlightType.COMPARING -> VisComparing
        HighlightType.SORTED -> VisSorted
        HighlightType.VISITED -> VisVisited
        HighlightType.CURRENT -> VisCurrent
        else -> CyanBright
      }

      drawCircle(
        color = BgCard,
        radius = 18.dp.toPx(),
        center = center
      )
      drawCircle(
        color = nodeColor,
        radius = 18.dp.toPx(),
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
      )
    }
  }

  // Draw node values using Compose Text overlays
  Box(modifier = Modifier.fillMaxSize()) {
    nodes.forEach { node ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(
            start = (node.x * 240).dp,
            top = (node.y * 180).dp
          )
      ) {
        Text(
          text = "${node.value}",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun GraphVisualizer(nodes: List<GraphNodeVisual>, edges: List<GraphEdgeVisual>) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    // Draw edges
    edges.forEach { edge ->
      val nFrom = nodes.find { it.id == edge.from }
      val nTo = nodes.find { it.id == edge.to }
      if (nFrom != null && nTo != null) {
        val start = Offset(nFrom.x * width, nFrom.y * height)
        val end = Offset(nTo.x * width, nTo.y * height)
        drawLine(
          color = if (edge.isHighlighted) CyanBright else BorderAccent,
          start = start,
          end = end,
          strokeWidth = if (edge.isHighlighted) 4.5f else 2.5f
        )
      }
    }

    // Draw nodes
    nodes.forEach { node ->
      val center = Offset(node.x * width, node.y * height)
      val color = when (node.state) {
        HighlightType.CURRENT -> VisCurrent
        HighlightType.COMPARING -> VisComparing
        HighlightType.VISITED -> VisVisited
        HighlightType.SORTED -> VisSorted
        else -> IndigoAccent
      }
      drawCircle(color = BgCardElevated, radius = 20.dp.toPx(), center = center)
      drawCircle(
        color = color,
        radius = 20.dp.toPx(),
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
      )
    }
  }

  // Node Labels overlay
  Box(modifier = Modifier.fillMaxSize()) {
    nodes.forEach { node ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(
            start = (node.x * 260).dp,
            top = (node.y * 170).dp
          )
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = node.id,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          if (node.distance != "∞") {
            Text(
              text = "d=${node.distance}",
              color = CyanBright,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun DpGridVisualizer(
  rowHeaders: List<String>,
  colHeaders: List<String>,
  table: List<List<String>>,
  activeCell: Pair<Int, Int>?
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "DP Memoization / Tabulation Grid",
      color = TextMuted,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // Grid Container
    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(BgCard)
        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
        .padding(6.dp)
    ) {
      table.forEachIndexed { rIdx, row ->
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          // Row header
          val rHeader = rowHeaders.getOrNull(rIdx) ?: "R$rIdx"
          Box(
            modifier = Modifier
              .width(38.dp)
              .height(30.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(BgCardElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = rHeader,
              color = IndigoAccent,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }

          row.forEachIndexed { cIdx, cellValue ->
            val isActive = activeCell?.first == rIdx && activeCell.second == cIdx
            val cellBg = if (isActive) PurpleAI else if (cellValue != "-") BgInput else Color.Transparent
            val cellBorder = if (isActive) CyanBright else BorderSubtle

            Box(
              modifier = Modifier
                .width(32.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(cellBg)
                .border(1.dp, cellBorder, RoundedCornerShape(4.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cellValue,
                color = if (isActive) Color.White else if (cellValue != "-") CyanBright else TextMuted,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
      }
    }
  }
}

@Composable
fun LinkedListVisualizer(
  data: List<Int>,
  pointers: Map<String, Int>,
  highlights: Map<Int, HighlightType>
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Singly Linked List Nodes & Pointers",
      color = TextMuted,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      data.forEachIndexed { index, value ->
        val hlType = highlights[index] ?: HighlightType.DEFAULT
        val nodeColor = when (hlType) {
          HighlightType.CURRENT -> VisCurrent
          HighlightType.COMPARING -> VisComparing
          HighlightType.SORTED -> VisSorted
          else -> CyanBright
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BgCardElevated)
                .border(2.dp, nodeColor, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$value",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            if (index < data.size - 1) {
              Text(
                text = " ──► ",
                color = IndigoAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            } else {
              Text(
                text = " ──► ∅",
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          val pointerLabel = pointers.entries.find { it.value == index }?.key
          if (pointerLabel != null) {
            Text(
              text = pointerLabel,
              color = PurpleGlow,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
