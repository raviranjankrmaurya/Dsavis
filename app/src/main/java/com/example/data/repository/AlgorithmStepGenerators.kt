package com.example.data.repository

import com.example.data.model.HighlightType
import com.example.data.model.VisualizerStep

object AlgorithmStepGenerators {

  // --- ARRAYS: Linear Search ---
  fun generateLinearSearch(inputStr: String): List<VisualizerStep> {
    val tokens = inputStr.split(";").map { it.trim() }
    val array = (tokens.getOrNull(0) ?: "4, 2, 7, 1, 9, 3")
      .split(",").mapNotNull { it.trim().toIntOrNull() }.ifEmpty { listOf(4, 2, 7, 1, 9, 3) }
    val target = tokens.getOrNull(1)?.toIntOrNull() ?: 7

    val steps = mutableListOf<VisualizerStep>()
    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = array.size + 1,
        description = "Starting Linear Search for target = $target in array.",
        activePseudocodeLine = 0,
        arrayData = array,
        pointers = mapOf("target" to target)
      )
    )

    var found = false
    for (i in array.indices) {
      val elem = array[i]
      if (elem == target) {
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Match found! array[$i] = $elem matches target $target.",
            activePseudocodeLine = 3,
            arrayData = array,
            highlights = mapOf(i to HighlightType.SORTED),
            pointers = mapOf("i" to i)
          )
        )
        found = true
        break
      } else {
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Checking index $i: $elem != $target. Move to next index.",
            activePseudocodeLine = 2,
            arrayData = array,
            highlights = mapOf(i to HighlightType.COMPARING),
            pointers = mapOf("i" to i)
          )
        )
      }
    }

    if (!found) {
      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Target $target not found in array. Return -1.",
          activePseudocodeLine = 5,
          arrayData = array
        )
      )
    }

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- ARRAYS: Binary Search ---
  fun generateBinarySearch(inputStr: String): List<VisualizerStep> {
    val tokens = inputStr.split(";").map { it.trim() }
    val rawArray = (tokens.getOrNull(0) ?: "2, 5, 8, 12, 16, 23, 38, 56, 72, 91")
      .split(",").mapNotNull { it.trim().toIntOrNull() }.sorted().ifEmpty { listOf(2, 5, 8, 12, 16, 23, 38, 56, 72, 91) }
    val target = tokens.getOrNull(1)?.toIntOrNull() ?: 23

    val steps = mutableListOf<VisualizerStep>()
    var low = 0
    var high = rawArray.size - 1

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = 1,
        description = "Array sorted. Search range: low = $low, high = $high for target = $target.",
        activePseudocodeLine = 1,
        arrayData = rawArray,
        pointers = mapOf("low" to low, "high" to high)
      )
    )

    var found = false
    while (low <= high) {
      val mid = (low + high) / 2
      val midVal = rawArray[mid]

      val inRangeHighlights = (low..high).associateWith {
        if (it == mid) HighlightType.COMPARING else HighlightType.DEFAULT
      }

      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Calculated mid = $mid (value: $midVal). Comparing with target $target.",
          activePseudocodeLine = 3,
          arrayData = rawArray,
          highlights = inRangeHighlights,
          pointers = mapOf("low" to low, "mid" to mid, "high" to high)
        )
      )

      if (midVal == target) {
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Success! Target $target found at index $mid.",
            activePseudocodeLine = 4,
            arrayData = rawArray,
            highlights = mapOf(mid to HighlightType.SORTED),
            pointers = mapOf("mid" to mid)
          )
        )
        found = true
        break
      } else if (midVal < target) {
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "$midVal < $target: Discard left half. Move low to ${mid + 1}.",
            activePseudocodeLine = 5,
            arrayData = rawArray,
            highlights = (0..mid).associateWith { HighlightType.VISITED },
            pointers = mapOf("low" to mid + 1, "high" to high)
          )
        )
        low = mid + 1
      } else {
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "$midVal > $target: Discard right half. Move high to ${mid - 1}.",
            activePseudocodeLine = 6,
            arrayData = rawArray,
            highlights = (mid..rawArray.size - 1).associateWith { HighlightType.VISITED },
            pointers = mapOf("low" to low, "high" to mid - 1)
          )
        )
        high = mid - 1
      }
    }

    if (!found) {
      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Target $target not found. Search interval exhausted.",
          activePseudocodeLine = 7,
          arrayData = rawArray
        )
      )
    }

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- SORTING: Bubble Sort ---
  fun generateBubbleSort(inputStr: String): List<VisualizerStep> {
    val arr = inputStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableList()
      .ifEmpty { mutableListOf(64, 34, 25, 12, 22, 11, 90) }

    val steps = mutableListOf<VisualizerStep>()
    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = 1,
        description = "Starting Bubble Sort on initial array.",
        activePseudocodeLine = 0,
        arrayData = arr.toList()
      )
    )

    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()

    for (i in 0 until n - 1) {
      var swapped = false
      for (j in 0 until n - i - 1) {
        val hl = sortedIndices.associateWith { HighlightType.SORTED }.toMutableMap()
        hl[j] = HighlightType.COMPARING
        hl[j + 1] = HighlightType.COMPARING

        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Comparing arr[$j]=${arr[j]} and arr[${j + 1}]=${arr[j + 1]}.",
            activePseudocodeLine = 2,
            arrayData = arr.toList(),
            highlights = hl,
            pointers = mapOf("j" to j, "j+1" to j + 1)
          )
        )

        if (arr[j] > arr[j + 1]) {
          val temp = arr[j]
          arr[j] = arr[j + 1]
          arr[j + 1] = temp
          swapped = true

          val swapHl = sortedIndices.associateWith { HighlightType.SORTED }.toMutableMap()
          swapHl[j] = HighlightType.SWAPPING
          swapHl[j + 1] = HighlightType.SWAPPING

          steps.add(
            VisualizerStep(
              stepNumber = steps.size + 1,
              totalSteps = 0,
              description = "Since ${arr[j + 1]} > ${arr[j]}, swapped elements at $j and ${j + 1}.",
              activePseudocodeLine = 3,
              arrayData = arr.toList(),
              highlights = swapHl,
              pointers = mapOf("j" to j, "j+1" to j + 1)
            )
          )
        }
      }
      sortedIndices.add(n - i - 1)
      if (!swapped) break
    }
    sortedIndices.addAll(arr.indices)
    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Array is fully sorted!",
        activePseudocodeLine = 5,
        arrayData = arr.toList(),
        highlights = sortedIndices.associateWith { HighlightType.SORTED }
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- SORTING: Selection Sort ---
  fun generateSelectionSort(inputStr: String): List<VisualizerStep> {
    val arr = inputStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableList()
      .ifEmpty { mutableListOf(29, 10, 14, 37, 13) }
    val steps = mutableListOf<VisualizerStep>()
    val n = arr.size

    for (i in 0 until n - 1) {
      var minIdx = i
      for (j in i + 1 until n) {
        val hl = mutableMapOf<Int, HighlightType>()
        for (k in 0 until i) hl[k] = HighlightType.SORTED
        hl[minIdx] = HighlightType.PIVOT
        hl[j] = HighlightType.COMPARING

        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Comparing arr[$j]=${arr[j]} with current minimum arr[$minIdx]=${arr[minIdx]}.",
            activePseudocodeLine = 3,
            arrayData = arr.toList(),
            highlights = hl,
            pointers = mapOf("min" to minIdx, "j" to j)
          )
        )

        if (arr[j] < arr[minIdx]) {
          minIdx = j
        }
      }

      if (minIdx != i) {
        val temp = arr[i]
        arr[i] = arr[minIdx]
        arr[minIdx] = temp

        val hl = mutableMapOf<Int, HighlightType>()
        for (k in 0 until i) hl[k] = HighlightType.SORTED
        hl[i] = HighlightType.SWAPPING
        hl[minIdx] = HighlightType.SWAPPING

        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Swapped new minimum ${arr[i]} into sorted position $i.",
            activePseudocodeLine = 5,
            arrayData = arr.toList(),
            highlights = hl,
            pointers = mapOf("i" to i)
          )
        )
      }
    }

    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Selection sort complete. Array fully sorted.",
        activePseudocodeLine = 6,
        arrayData = arr.toList(),
        highlights = arr.indices.associateWith { HighlightType.SORTED }
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- SORTING: Insertion Sort ---
  fun generateInsertionSort(inputStr: String): List<VisualizerStep> {
    val arr = inputStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableList()
      .ifEmpty { mutableListOf(12, 11, 13, 5, 6) }
    val steps = mutableListOf<VisualizerStep>()

    for (i in 1 until arr.size) {
      val key = arr[i]
      var j = i - 1

      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Selecting key = $key at index $i to insert into sorted left portion.",
          activePseudocodeLine = 1,
          arrayData = arr.toList(),
          highlights = mapOf(i to HighlightType.PIVOT),
          pointers = mapOf("key" to i)
        )
      )

      while (j >= 0 && arr[j] > key) {
        arr[j + 1] = arr[j]
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Shifted ${arr[j]} right to index ${j + 1}.",
            activePseudocodeLine = 3,
            arrayData = arr.toList(),
            highlights = mapOf(j + 1 to HighlightType.SWAPPING),
            pointers = mapOf("j" to j)
          )
        )
        j--
      }
      arr[j + 1] = key
      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Inserted key $key at index ${j + 1}.",
          activePseudocodeLine = 4,
          arrayData = arr.toList(),
          highlights = (0..i).associateWith { HighlightType.SORTED }
        )
      )
    }

    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Insertion Sort complete. All elements sorted.",
        activePseudocodeLine = 5,
        arrayData = arr.toList(),
        highlights = arr.indices.associateWith { HighlightType.SORTED }
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- SORTING: Quick Sort ---
  fun generateQuickSort(inputStr: String): List<VisualizerStep> {
    val arr = inputStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableList()
      .ifEmpty { mutableListOf(38, 27, 43, 3, 9, 82, 10) }
    val steps = mutableListOf<VisualizerStep>()

    fun quickSort(low: Int, high: Int) {
      if (low < high) {
        val pivot = arr[high]
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Chosen pivot $pivot at index $high for sub-array [$low..$high].",
            activePseudocodeLine = 2,
            arrayData = arr.toList(),
            highlights = mapOf(high to HighlightType.PIVOT),
            pointers = mapOf("low" to low, "pivot" to high)
          )
        )

        var i = low - 1
        for (j in low until high) {
          if (arr[j] <= pivot) {
            i++
            val temp = arr[i]
            arr[i] = arr[j]
            arr[j] = temp
            steps.add(
              VisualizerStep(
                stepNumber = steps.size + 1,
                totalSteps = 0,
                description = "arr[$j]=${arr[i]} <= pivot $pivot. Swapped with index $i.",
                activePseudocodeLine = 4,
                arrayData = arr.toList(),
                highlights = mapOf(i to HighlightType.SWAPPING, j to HighlightType.SWAPPING, high to HighlightType.PIVOT)
              )
            )
          }
        }
        val temp = arr[i + 1]
        arr[i + 1] = arr[high]
        arr[high] = temp

        val pi = i + 1
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "Pivot $pivot placed in final position at index $pi.",
            activePseudocodeLine = 5,
            arrayData = arr.toList(),
            highlights = mapOf(pi to HighlightType.SORTED)
          )
        )

        quickSort(low, pi - 1)
        quickSort(pi + 1, high)
      }
    }

    quickSort(0, arr.size - 1)
    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Quick Sort complete! Entire array is partitioned and sorted.",
        activePseudocodeLine = 7,
        arrayData = arr.toList(),
        highlights = arr.indices.associateWith { HighlightType.SORTED }
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- STACK Operations ---
  fun generateStackOperations(inputStr: String): List<VisualizerStep> {
    val operations = listOf("PUSH(10)", "PUSH(20)", "PUSH(30)", "PEEK()", "POP()", "PUSH(40)", "POP()")
    val stack = mutableListOf<String>()
    val steps = mutableListOf<VisualizerStep>()

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = operations.size + 1,
        description = "Stack initialized (LIFO - Last In First Out). Empty stack.",
        activePseudocodeLine = 0,
        stackData = emptyList()
      )
    )

    for (op in operations) {
      if (op.startsWith("PUSH")) {
        val item = op.substringAfter("(").substringBefore(")")
        stack.add(item)
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "PUSH $item onto top of stack. Top is now at index ${stack.size - 1}.",
            activePseudocodeLine = 1,
            stackData = stack.toList()
          )
        )
      } else if (op.startsWith("POP")) {
        val removed = if (stack.isNotEmpty()) stack.removeAt(stack.size - 1) else "null"
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "POP executed: Removed item $removed from top of stack.",
            activePseudocodeLine = 2,
            stackData = stack.toList()
          )
        )
      } else if (op.startsWith("PEEK")) {
        val top = stack.lastOrNull() ?: "null"
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "PEEK executed: Top item is $top (without removing).",
            activePseudocodeLine = 3,
            stackData = stack.toList()
          )
        )
      }
    }

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- QUEUE Operations ---
  fun generateQueueOperations(inputStr: String): List<VisualizerStep> {
    val operations = listOf("ENQUEUE(A)", "ENQUEUE(B)", "ENQUEUE(C)", "DEQUEUE()", "ENQUEUE(D)", "DEQUEUE()")
    val queue = mutableListOf<String>()
    val steps = mutableListOf<VisualizerStep>()

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = operations.size + 1,
        description = "Queue initialized (FIFO - First In First Out). Empty queue.",
        activePseudocodeLine = 0,
        queueData = emptyList()
      )
    )

    for (op in operations) {
      if (op.startsWith("ENQUEUE")) {
        val item = op.substringAfter("(").substringBefore(")")
        queue.add(item)
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "ENQUEUE: Added $item to the rear of the queue.",
            activePseudocodeLine = 1,
            queueData = queue.toList()
          )
        )
      } else if (op.startsWith("DEQUEUE")) {
        val removed = if (queue.isNotEmpty()) queue.removeAt(0) else "null"
        steps.add(
          VisualizerStep(
            stepNumber = steps.size + 1,
            totalSteps = 0,
            description = "DEQUEUE: Removed $removed from the front of the queue.",
            activePseudocodeLine = 2,
            queueData = queue.toList()
          )
        )
      }
    }

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- DYNAMIC PROGRAMMING: Fibonacci ---
  fun generateFibonacciDP(inputStr: String): List<VisualizerStep> {
    val n = inputStr.toIntOrNull() ?: 6
    val steps = mutableListOf<VisualizerStep>()
    val dp = MutableList(n + 1) { 0 }

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = 0,
        description = "Initialize DP table of size ${n + 1} with base cases F(0)=0 and F(1)=1.",
        activePseudocodeLine = 1,
        dpRowHeaders = listOf("i", "F(i)"),
        dpColHeaders = (0..n).map { "$it" },
        dpTable = listOf((0..n).map { "$it" }, MutableList(n + 1) { "-" })
      )
    )

    dp[0] = 0
    if (n >= 1) dp[1] = 1

    val currentTable = MutableList(n + 1) { "-" }
    currentTable[0] = "0"
    if (n >= 1) currentTable[1] = "1"

    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Base cases stored: dp[0] = 0, dp[1] = 1.",
        activePseudocodeLine = 2,
        dpRowHeaders = listOf("i", "F(i)"),
        dpColHeaders = (0..n).map { "$it" },
        dpTable = listOf((0..n).map { "$it" }, currentTable.toList()),
        activeTableCell = Pair(1, 1)
      )
    )

    for (i in 2..n) {
      dp[i] = dp[i - 1] + dp[i - 2]
      currentTable[i] = "${dp[i]}"
      steps.add(
        VisualizerStep(
          stepNumber = steps.size + 1,
          totalSteps = 0,
          description = "Computing F($i) = F(${i - 1}) + F(${i - 2}) = ${dp[i - 1]} + ${dp[i - 2]} = ${dp[i]}.",
          activePseudocodeLine = 4,
          dpRowHeaders = listOf("i", "F(i)"),
          dpColHeaders = (0..n).map { "$it" },
          dpTable = listOf((0..n).map { "$it" }, currentTable.toList()),
          activeTableCell = Pair(1, i)
        )
      )
    }

    steps.add(
      VisualizerStep(
        stepNumber = steps.size + 1,
        totalSteps = 0,
        description = "Completed! The ${n}th Fibonacci number is ${dp[n]}.",
        activePseudocodeLine = 5,
        dpRowHeaders = listOf("i", "F(i)"),
        dpColHeaders = (0..n).map { "$it" },
        dpTable = listOf((0..n).map { "$it" }, currentTable.toList()),
        activeTableCell = Pair(1, n)
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- TREE: Binary Search Tree Insert & Search ---
  fun generateBSTTraversal(inputStr: String): List<VisualizerStep> {
    val steps = mutableListOf<VisualizerStep>()
    // Node coordinate map:
    // Root: 50, (0.5f, 0.15f)
    // Left: 30, (0.28f, 0.45f), Right: 70, (0.72f, 0.45f)
    // 20: (0.16f, 0.8f), 40: (0.40f, 0.8f), 60: (0.60f, 0.8f), 80: (0.84f, 0.8f)
    val baseNodes = listOf(
      com.example.data.model.TreeNodeVisual(1, 50, 2, 3, 0.5f, 0.15f),
      com.example.data.model.TreeNodeVisual(2, 30, 4, 5, 0.28f, 0.45f),
      com.example.data.model.TreeNodeVisual(3, 70, 6, 7, 0.72f, 0.45f),
      com.example.data.model.TreeNodeVisual(4, 20, null, null, 0.16f, 0.80f),
      com.example.data.model.TreeNodeVisual(5, 40, null, null, 0.40f, 0.80f),
      com.example.data.model.TreeNodeVisual(6, 60, null, null, 0.60f, 0.80f),
      com.example.data.model.TreeNodeVisual(7, 80, null, null, 0.84f, 0.80f)
    )

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = 0,
        description = "Binary Search Tree initialized. Target search value: 40.",
        activePseudocodeLine = 0,
        treeNodes = baseNodes
      )
    )

    // Step 1: Visit Root (50)
    steps.add(
      VisualizerStep(
        stepNumber = 2,
        totalSteps = 0,
        description = "At Root (50): 40 < 50, so move to LEFT child.",
        activePseudocodeLine = 2,
        treeNodes = baseNodes.map { if (it.id == 1) it.copy(state = HighlightType.COMPARING) else it }
      )
    )

    // Step 2: Visit Left Child (30)
    steps.add(
      VisualizerStep(
        stepNumber = 3,
        totalSteps = 0,
        description = "At Node (30): 40 > 30, so move to RIGHT child.",
        activePseudocodeLine = 3,
        treeNodes = baseNodes.map {
          when (it.id) {
            1 -> it.copy(state = HighlightType.VISITED)
            2 -> it.copy(state = HighlightType.COMPARING)
            else -> it
          }
        }
      )
    )

    // Step 3: Match Node (40)
    steps.add(
      VisualizerStep(
        stepNumber = 4,
        totalSteps = 0,
        description = "Found target 40 at Node 5! Search successful.",
        activePseudocodeLine = 4,
        treeNodes = baseNodes.map {
          when (it.id) {
            1, 2 -> it.copy(state = HighlightType.VISITED)
            5 -> it.copy(state = HighlightType.SORTED)
            else -> it
          }
        }
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }

  // --- GRAPH: Breadth First Search (BFS) ---
  fun generateGraphBFS(inputStr: String): List<VisualizerStep> {
    val steps = mutableListOf<VisualizerStep>()
    val baseNodes = listOf(
      com.example.data.model.GraphNodeVisual("A", "A (Start)", 0.2f, 0.3f),
      com.example.data.model.GraphNodeVisual("B", "B", 0.5f, 0.2f),
      com.example.data.model.GraphNodeVisual("C", "C", 0.5f, 0.65f),
      com.example.data.model.GraphNodeVisual("D", "D", 0.8f, 0.3f),
      com.example.data.model.GraphNodeVisual("E", "E", 0.8f, 0.75f)
    )
    val baseEdges = listOf(
      com.example.data.model.GraphEdgeVisual("A", "B"),
      com.example.data.model.GraphEdgeVisual("A", "C"),
      com.example.data.model.GraphEdgeVisual("B", "D"),
      com.example.data.model.GraphEdgeVisual("C", "E"),
      com.example.data.model.GraphEdgeVisual("D", "E")
    )

    steps.add(
      VisualizerStep(
        stepNumber = 1,
        totalSteps = 0,
        description = "Initialize BFS starting at Node A. Queue = ['A'].",
        activePseudocodeLine = 1,
        graphNodes = baseNodes.map { if (it.id == "A") it.copy(state = HighlightType.CURRENT, distance = "0") else it },
        graphEdges = baseEdges,
        queueData = listOf("A")
      )
    )

    // Dequeue A, Visit Neighbors B and C
    steps.add(
      VisualizerStep(
        stepNumber = 2,
        totalSteps = 0,
        description = "Dequeue A. Discover unvisited neighbors B and C. Add to Queue.",
        activePseudocodeLine = 3,
        graphNodes = baseNodes.map {
          when (it.id) {
            "A" -> it.copy(state = HighlightType.VISITED, distance = "0")
            "B" -> it.copy(state = HighlightType.COMPARING, distance = "1")
            "C" -> it.copy(state = HighlightType.COMPARING, distance = "1")
            else -> it
          }
        },
        graphEdges = baseEdges.map {
          if (it.from == "A" && (it.to == "B" || it.to == "C")) it.copy(isHighlighted = true) else it
        },
        queueData = listOf("B", "C")
      )
    )

    // Dequeue B, Discover D
    steps.add(
      VisualizerStep(
        stepNumber = 3,
        totalSteps = 0,
        description = "Dequeue B. Discover neighbor D. Distance = 2. Add D to Queue.",
        activePseudocodeLine = 3,
        graphNodes = baseNodes.map {
          when (it.id) {
            "A", "B" -> it.copy(state = HighlightType.VISITED, distance = if (it.id == "A") "0" else "1")
            "C" -> it.copy(state = HighlightType.COMPARING, distance = "1")
            "D" -> it.copy(state = HighlightType.CURRENT, distance = "2")
            else -> it
          }
        },
        graphEdges = baseEdges.map {
          if (it.from == "B" && it.to == "D") it.copy(isHighlighted = true) else it
        },
        queueData = listOf("C", "D")
      )
    )

    // Dequeue C, Discover E
    steps.add(
      VisualizerStep(
        stepNumber = 4,
        totalSteps = 0,
        description = "Dequeue C. Discover neighbor E. Distance = 2. Add E to Queue.",
        activePseudocodeLine = 3,
        graphNodes = baseNodes.map {
          when (it.id) {
            "A", "B", "C" -> it.copy(state = HighlightType.VISITED, distance = if (it.id == "A") "0" else "1")
            "D" -> it.copy(state = HighlightType.COMPARING, distance = "2")
            "E" -> it.copy(state = HighlightType.CURRENT, distance = "2")
            else -> it
          }
        },
        graphEdges = baseEdges.map {
          if (it.from == "C" && it.to == "E") it.copy(isHighlighted = true) else it
        },
        queueData = listOf("D", "E")
      )
    )

    // Finished
    steps.add(
      VisualizerStep(
        stepNumber = 5,
        totalSteps = 0,
        description = "BFS complete! All reachable nodes visited in level order.",
        activePseudocodeLine = 5,
        graphNodes = baseNodes.map { it.copy(state = HighlightType.SORTED, distance = if (it.id == "A") "0" else if (it.id in listOf("B", "C")) "1" else "2") },
        graphEdges = baseEdges.map { it.copy(isHighlighted = true) },
        queueData = emptyList()
      )
    )

    return steps.mapIndexed { idx, s -> s.copy(stepNumber = idx + 1, totalSteps = steps.size) }
  }
}
