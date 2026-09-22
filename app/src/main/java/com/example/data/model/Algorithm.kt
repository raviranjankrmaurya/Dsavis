package com.example.data.model

enum class AlgorithmCategory(val displayName: String) {
  ARRAYS("Arrays"),
  SORTING("Sorting"),
  LINKED_LIST("Linked List"),
  STACK("Stack"),
  QUEUE("Queue"),
  TREE("Tree"),
  GRAPH("Graph"),
  HASHING("Hashing"),
  DYNAMIC_PROGRAMMING("Dynamic Programming")
}

enum class HighlightType {
  DEFAULT,
  COMPARING,
  SWAPPING,
  SORTED,
  PIVOT,
  VISITED,
  CURRENT
}

enum class VisualizationType {
  ARRAY_BARS,
  ARRAY_POINTERS,
  STACK_VIEW,
  QUEUE_VIEW,
  LINKED_LIST_VIEW,
  TREE_VIEW,
  GRAPH_VIEW,
  DP_GRID
}

data class TreeNodeVisual(
  val id: Int,
  val value: Int,
  val leftId: Int? = null,
  val rightId: Int? = null,
  val x: Float, // relative 0.0 .. 1.0
  val y: Float, // relative 0.0 .. 1.0
  val state: HighlightType = HighlightType.DEFAULT
)

data class GraphNodeVisual(
  val id: String,
  val label: String,
  val x: Float,
  val y: Float,
  val state: HighlightType = HighlightType.DEFAULT,
  val distance: String = "∞"
)

data class GraphEdgeVisual(
  val from: String,
  val to: String,
  val weight: Int? = null,
  val isHighlighted: Boolean = false
)

data class VisualizerStep(
  val stepNumber: Int,
  val totalSteps: Int,
  val description: String,
  val activePseudocodeLine: Int = -1,
  val arrayData: List<Int> = emptyList(),
  val highlights: Map<Int, HighlightType> = emptyMap(),
  val pointers: Map<String, Int> = emptyMap(),
  val stackData: List<String> = emptyList(),
  val queueData: List<String> = emptyList(),
  val treeNodes: List<TreeNodeVisual> = emptyList(),
  val graphNodes: List<GraphNodeVisual> = emptyList(),
  val graphEdges: List<GraphEdgeVisual> = emptyList(),
  val dpTable: List<List<String>> = emptyList(),
  val dpRowHeaders: List<String> = emptyList(),
  val dpColHeaders: List<String> = emptyList(),
  val activeTableCell: Pair<Int, Int>? = null
)

data class Algorithm(
  val id: String,
  val name: String,
  val category: AlgorithmCategory,
  val visualType: VisualizationType,
  val timeComplexityBest: String,
  val timeComplexityAvg: String,
  val timeComplexityWorst: String,
  val spaceComplexity: String,
  val summary: String,
  val pseudocode: List<String>,
  val codeImplementations: Map<String, String>, // "Java", "Python", "C++", "JavaScript", "TypeScript"
  val explanation: String,
  val commonMistakes: List<String>,
  val defaultInput: String,
  val stepGenerator: (String) -> List<VisualizerStep>
)
