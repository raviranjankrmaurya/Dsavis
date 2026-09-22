package com.example.data.repository

import com.example.data.model.*

object DSADataRepository {

  val algorithms: List<Algorithm> = listOf(
    Algorithm(
      id = "binary-search",
      name = "Binary Search",
      category = AlgorithmCategory.ARRAYS,
      visualType = VisualizationType.ARRAY_POINTERS,
      timeComplexityBest = "O(1)",
      timeComplexityAvg = "O(log n)",
      timeComplexityWorst = "O(log n)",
      spaceComplexity = "O(1)",
      summary = "Efficiently locates target in a sorted collection by repeatedly halving search interval.",
      pseudocode = listOf(
        "function binarySearch(arr, target):",
        "  low = 0, high = arr.length - 1",
        "  while low <= high:",
        "    mid = floor((low + high) / 2)",
        "    if arr[mid] == target: return mid",
        "    else if arr[mid] < target: low = mid + 1",
        "    else: high = mid - 1",
        "  return -1"
      ),
      codeImplementations = mapOf(
        "Python" to """def binary_search(arr: list[int], target: int) -> int:
    low, high = 0, len(arr) - 1
    while low <= high:
        mid = (low + high) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            low = mid + 1
        else:
            high = mid - 1
    return -1""",
        "Java" to """public class Solution {
    public int binarySearch(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }
}""",
        "C++" to """int binarySearch(const std::vector<int>& arr, int target) {
    int low = 0, high = arr.size() - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) low = mid + 1;
        else high = mid - 1;
    }
    return -1;
}""",
        "TypeScript" to """function binarySearch(arr: number[], target: number): number {
  let low = 0, high = arr.length - 1;
  while (low <= high) {
    const mid = Math.floor((low + high) / 2);
    if (arr[mid] === target) return mid;
    if (arr[mid] < target) low = mid + 1;
    else high = mid - 1;
  }
  return -1;
}""",
        "JavaScript" to """function binarySearch(arr, target) {
  let low = 0, high = arr.length - 1;
  while (low <= high) {
    const mid = Math.floor((low + high) / 2);
    if (arr[mid] === target) return mid;
    if (arr[mid] < target) low = mid + 1;
    else high = mid - 1;
  }
  return -1;
}"""
      ),
      explanation = "Binary Search operates on a pre-sorted array. At each step, it compares the middle element to the target. If matched, it returns the index. Otherwise, half the array is pruned.",
      commonMistakes = listOf(
        "Integer overflow in (low + high) / 2; use low + (high - low) / 2 instead.",
        "Using '<' instead of '<=' causing off-by-one errors for single-element arrays.",
        "Forgetting that the input array MUST be sorted before executing."
      ),
      defaultInput = "2, 5, 8, 12, 16, 23, 38, 56, 72, 91; 23",
      stepGenerator = { input -> AlgorithmStepGenerators.generateBinarySearch(input) }
    ),

    Algorithm(
      id = "linear-search",
      name = "Linear Search",
      category = AlgorithmCategory.ARRAYS,
      visualType = VisualizationType.ARRAY_BARS,
      timeComplexityBest = "O(1)",
      timeComplexityAvg = "O(n)",
      timeComplexityWorst = "O(n)",
      spaceComplexity = "O(1)",
      summary = "Sequentially examines each element in the collection until the target is found.",
      pseudocode = listOf(
        "function linearSearch(arr, target):",
        "  for i from 0 to arr.length - 1:",
        "    if arr[i] == target: return i",
        "  return -1"
      ),
      codeImplementations = mapOf(
        "Python" to """def linear_search(arr: list[int], target: int) -> int:
    for i in range(len(arr)):
        if arr[i] == target:
            return i
    return -1""",
        "Java" to """public int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}""",
        "TypeScript" to """function linearSearch(arr: number[], target: number): number {
  for (let i = 0; i < arr.length; i++) {
    if (arr[i] === target) return i;
  }
  return -1;
}"""
      ),
      explanation = "The simplest search algorithm. Works on unsorted collections by checking every slot until finding target or hitting the end.",
      commonMistakes = listOf(
        "Looping beyond the bounds of the array.",
        "Not returning -1 when target is not found."
      ),
      defaultInput = "4, 2, 7, 1, 9, 3; 7",
      stepGenerator = { input -> AlgorithmStepGenerators.generateLinearSearch(input) }
    ),

    Algorithm(
      id = "bubble-sort",
      name = "Bubble Sort",
      category = AlgorithmCategory.SORTING,
      visualType = VisualizationType.ARRAY_BARS,
      timeComplexityBest = "O(n)",
      timeComplexityAvg = "O(n²)",
      timeComplexityWorst = "O(n²)",
      spaceComplexity = "O(1)",
      summary = "Repeatedly steps through list, compares adjacent elements and swaps them if out of order.",
      pseudocode = listOf(
        "function bubbleSort(arr):",
        "  for i from 0 to n - 1:",
        "    for j from 0 to n - i - 2:",
        "      if arr[j] > arr[j + 1]:",
        "        swap(arr[j], arr[j + 1])",
        "  return arr"
      ),
      codeImplementations = mapOf(
        "Python" to """def bubble_sort(arr: list[int]) -> list[int]:
    n = len(arr)
    for i in range(n):
        swapped = False
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:
            break
    return arr""",
        "Java" to """public void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break;
    }
}""",
        "TypeScript" to """function bubbleSort(arr: number[]): number[] {
  const n = arr.length;
  for (let i = 0; i < n; i++) {
    let swapped = false;
    for (let j = 0; j < n - i - 1; j++) {
      if (arr[j] > arr[j + 1]) {
        [arr[j], arr[j + 1]] = [arr[j + 1], arr[j]];
        swapped = true;
      }
    }
    if (!swapped) break;
  }
  return arr;
}"""
      ),
      explanation = "Bubble sort repeatedly bubbles the largest unsorted element to the right end of the array. An optimization flag allows early exit if no swaps occur.",
      commonMistakes = listOf(
        "Running the inner loop over the already-sorted tail elements (n - i - 1).",
        "Omitting the early termination flag when the array is already sorted."
      ),
      defaultInput = "64, 34, 25, 12, 22, 11, 90",
      stepGenerator = { input -> AlgorithmStepGenerators.generateBubbleSort(input) }
    ),

    Algorithm(
      id = "selection-sort",
      name = "Selection Sort",
      category = AlgorithmCategory.SORTING,
      visualType = VisualizationType.ARRAY_BARS,
      timeComplexityBest = "O(n²)",
      timeComplexityAvg = "O(n²)",
      timeComplexityWorst = "O(n²)",
      spaceComplexity = "O(1)",
      summary = "Divides the list into sorted and unsorted, continually selecting the minimum from unsorted.",
      pseudocode = listOf(
        "function selectionSort(arr):",
        "  for i from 0 to n - 2:",
        "    minIdx = i",
        "    for j from i + 1 to n - 1:",
        "      if arr[j] < arr[minIdx]: minIdx = j",
        "    swap(arr[i], arr[minIdx])",
        "  return arr"
      ),
      codeImplementations = mapOf(
        "Python" to """def selection_sort(arr: list[int]) -> list[int]:
    n = len(arr)
    for i in range(n):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]:
                min_idx = j
        arr[i], arr[min_idx] = arr[min_idx], arr[i]
    return arr"""
      ),
      explanation = "Maintains two subarrays: sorted (left) and unsorted (right). Finds the minimum in unsorted portion and swaps with current index.",
      commonMistakes = listOf(
        "Selection Sort is not a stable sort in basic array implementation.",
        "Always takes O(n²) comparisons even on already sorted input."
      ),
      defaultInput = "29, 10, 14, 37, 13",
      stepGenerator = { input -> AlgorithmStepGenerators.generateSelectionSort(input) }
    ),

    Algorithm(
      id = "insertion-sort",
      name = "Insertion Sort",
      category = AlgorithmCategory.SORTING,
      visualType = VisualizationType.ARRAY_BARS,
      timeComplexityBest = "O(n)",
      timeComplexityAvg = "O(n²)",
      timeComplexityWorst = "O(n²)",
      spaceComplexity = "O(1)",
      summary = "Builds sorted array one element at a time, inserting each item into its correct slot.",
      pseudocode = listOf(
        "function insertionSort(arr):",
        "  for i from 1 to n - 1:",
        "    key = arr[i], j = i - 1",
        "    while j >= 0 and arr[j] > key:",
        "      arr[j + 1] = arr[j]; j--",
        "    arr[j + 1] = key"
      ),
      codeImplementations = mapOf(
        "Python" to """def insertion_sort(arr: list[int]) -> list[int]:
    for i in range(1, len(arr)):
        key = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > key:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key
    return arr"""
      ),
      explanation = "Analogous to sorting playing cards in hand. Very efficient for small or nearly sorted lists.",
      commonMistakes = listOf(
        "Off-by-one in the while loop condition (j >= 0).",
        "Forgetting to place 'key' at j + 1 after exiting the loop."
      ),
      defaultInput = "12, 11, 13, 5, 6",
      stepGenerator = { input -> AlgorithmStepGenerators.generateInsertionSort(input) }
    ),

    Algorithm(
      id = "quick-sort",
      name = "Quick Sort",
      category = AlgorithmCategory.SORTING,
      visualType = VisualizationType.ARRAY_BARS,
      timeComplexityBest = "O(n log n)",
      timeComplexityAvg = "O(n log n)",
      timeComplexityWorst = "O(n²)",
      spaceComplexity = "O(log n)",
      summary = "Divide-and-conquer algorithm picking a pivot and partitioning elements smaller and greater.",
      pseudocode = listOf(
        "function quickSort(arr, low, high):",
        "  if low < high:",
        "    pivot = partition(arr, low, high)",
        "    quickSort(arr, low, pivot - 1)",
        "    quickSort(arr, pivot + 1, high)"
      ),
      codeImplementations = mapOf(
        "Python" to """def quick_sort(arr: list[int], low: int, high: int):
    if low < high:
        pi = partition(arr, low, high)
        quick_sort(arr, low, pi - 1)
        quick_sort(arr, pi + 1, high)

def partition(arr, low, high):
    pivot = arr[high]
    i = low - 1
    for j in range(low, high):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    return i + 1"""
      ),
      explanation = "Picks a pivot, places all smaller items left and greater items right, then recursively sorts.",
      commonMistakes = listOf(
        "Worst-case O(n²) behavior if bad pivot chosen on already-sorted arrays.",
        "Incorrect partitioning indices causing infinite recursion."
      ),
      defaultInput = "38, 27, 43, 3, 9, 82, 10",
      stepGenerator = { input -> AlgorithmStepGenerators.generateQuickSort(input) }
    ),

    Algorithm(
      id = "stack-operations",
      name = "Stack Operations (LIFO)",
      category = AlgorithmCategory.STACK,
      visualType = VisualizationType.STACK_VIEW,
      timeComplexityBest = "O(1)",
      timeComplexityAvg = "O(1)",
      timeComplexityWorst = "O(1)",
      spaceComplexity = "O(n)",
      summary = "Last-In First-Out linear container supporting Push, Pop, and Peek.",
      pseudocode = listOf(
        "class Stack:",
        "  push(item): items.append(item)",
        "  pop(): return items.removeLast()",
        "  peek(): return items.last()"
      ),
      codeImplementations = mapOf(
        "Python" to """class Stack:
    def __init__(self):
        self.items = []
    def push(self, item): self.items.append(item)
    def pop(self): return self.items.pop() if self.items else None
    def peek(self): return self.items[-1] if self.items else None
    def is_empty(self): return len(self.items) == 0"""
      ),
      explanation = "A stack operates on Last-In First-Out semantics. Elements are inserted and removed from the same top end in constant O(1) time.",
      commonMistakes = listOf(
        "Calling pop() on an empty stack causing an Underflow exception.",
        "Confusing Stack (LIFO) with Queue (FIFO)."
      ),
      defaultInput = "",
      stepGenerator = { input -> AlgorithmStepGenerators.generateStackOperations(input) }
    ),

    Algorithm(
      id = "queue-operations",
      name = "Queue Operations (FIFO)",
      category = AlgorithmCategory.QUEUE,
      visualType = VisualizationType.QUEUE_VIEW,
      timeComplexityBest = "O(1)",
      timeComplexityAvg = "O(1)",
      timeComplexityWorst = "O(1)",
      spaceComplexity = "O(n)",
      summary = "First-In First-Out collection where elements are added at rear and removed from front.",
      pseudocode = listOf(
        "class Queue:",
        "  enqueue(item): add item at tail",
        "  dequeue(): remove item from head"
      ),
      codeImplementations = mapOf(
        "Python" to """from collections import deque

class Queue:
    def __init__(self):
        self.q = deque()
    def enqueue(self, val):
        self.q.append(val)
    def dequeue(self):
        return self.q.popleft() if self.q else None"""
      ),
      explanation = "Queues preserve order of arrival. Standard in BFS traversals and task scheduling.",
      commonMistakes = listOf(
        "Using regular arrays in Python/JS where pop(0) takes O(n) instead of O(1).",
        "Dequeuing on empty queue without validation."
      ),
      defaultInput = "",
      stepGenerator = { input -> AlgorithmStepGenerators.generateQueueOperations(input) }
    ),

    Algorithm(
      id = "binary-search-tree",
      name = "Binary Search Tree",
      category = AlgorithmCategory.TREE,
      visualType = VisualizationType.TREE_VIEW,
      timeComplexityBest = "O(log n)",
      timeComplexityAvg = "O(log n)",
      timeComplexityWorst = "O(n)",
      spaceComplexity = "O(h)",
      summary = "Hierarchical binary tree where left child < root < right child for every node.",
      pseudocode = listOf(
        "function searchBST(root, val):",
        "  if root is null or root.val == val: return root",
        "  if val < root.val: return searchBST(root.left, val)",
        "  else: return searchBST(root.right, val)"
      ),
      codeImplementations = mapOf(
        "Python" to """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def search_bst(root, val):
    curr = root
    while curr and curr.val != val:
        curr = curr.left if val < curr.val else curr.right
    return curr"""
      ),
      explanation = "Maintains sorted property allowing logarithmic search, insertion, and deletion when balanced.",
      commonMistakes = listOf(
        "Assuming a BST remains balanced without self-balancing (AVL/Red-Black).",
        "Checking only direct parent-child instead of all subtree ancestors."
      ),
      defaultInput = "40",
      stepGenerator = { input -> AlgorithmStepGenerators.generateBSTTraversal(input) }
    ),

    Algorithm(
      id = "graph-bfs",
      name = "Breadth-First Search (BFS)",
      category = AlgorithmCategory.GRAPH,
      visualType = VisualizationType.GRAPH_VIEW,
      timeComplexityBest = "O(V + E)",
      timeComplexityAvg = "O(V + E)",
      timeComplexityWorst = "O(V + E)",
      spaceComplexity = "O(V)",
      summary = "Level-by-level graph traversal finding shortest paths in unweighted graphs.",
      pseudocode = listOf(
        "function bfs(graph, start):",
        "  queue = [start], visited = {start}",
        "  while queue is not empty:",
        "    node = queue.pop(0)",
        "    for neighbor in graph[node]:",
        "      if neighbor not in visited: queue.append(neighbor)"
      ),
      codeImplementations = mapOf(
        "Python" to """from collections import deque

def bfs(graph, start):
    visited = {start}
    queue = deque([start])
    while queue:
        node = queue.popleft()
        for neighbor in graph[node]:
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)"""
      ),
      explanation = "Explores neighbor nodes first before moving to next depth level. Optimal for unweighted shortest paths.",
      commonMistakes = listOf(
        "Forgetting to mark nodes visited when queuing, resulting in duplicate processing.",
        "Using DFS when shortest path in unweighted graph is requested."
      ),
      defaultInput = "A",
      stepGenerator = { input -> AlgorithmStepGenerators.generateGraphBFS(input) }
    ),

    Algorithm(
      id = "fibonacci-dp",
      name = "Fibonacci (DP Tabulation)",
      category = AlgorithmCategory.DYNAMIC_PROGRAMMING,
      visualType = VisualizationType.DP_GRID,
      timeComplexityBest = "O(n)",
      timeComplexityAvg = "O(n)",
      timeComplexityWorst = "O(n)",
      spaceComplexity = "O(n)",
      summary = "Bottom-up dynamic programming solving subproblems F(i) = F(i-1) + F(i-2).",
      pseudocode = listOf(
        "function fib(n):",
        "  dp = array of size n + 1",
        "  dp[0] = 0, dp[1] = 1",
        "  for i from 2 to n:",
        "    dp[i] = dp[i-1] + dp[i-2]",
        "  return dp[n]"
      ),
      codeImplementations = mapOf(
        "Python" to """def fib(n: int) -> int:
    if n <= 1: return n
    dp = [0] * (n + 1)
    dp[1] = 1
    for i in range(2, n + 1):
        dp[i] = dp[i - 1] + dp[i - 2]
    return dp[n]"""
      ),
      explanation = "Demonstrates memoization/tabulation overcoming naive 2^n exponential recursion.",
      commonMistakes = listOf(
        "Using naive recursion causing stack overflow on n > 40.",
        "Forgetting base cases F(0) and F(1)."
      ),
      defaultInput = "6",
      stepGenerator = { input -> AlgorithmStepGenerators.generateFibonacciDP(input) }
    )
  )

  val problems: List<Problem> = listOf(
    Problem(
      id = "two-sum",
      title = "Two Sum",
      category = "Arrays",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "54.2%",
      estimatedMinutes = 15,
      description = "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume each input has exactly one solution, and you may not use the same element twice.",
      examples = listOf(
        ProblemExample("nums = [2,7,11,15], target = 9", "[0,1]", "Because nums[0] + nums[1] == 9, we return [0, 1]."),
        ProblemExample("nums = [3,2,4], target = 6", "[1,2]", "")
      ),
      constraints = listOf(
        "2 <= nums.length <= 10^4",
        "-10^9 <= nums[i] <= 10^9",
        "Only one valid answer exists."
      ),
      starterCode = mapOf(
        "Python" to """def two_sum(nums: list[int], target: int) -> list[int]:
    # Write your solution here
    pass""",
        "Java" to """public int[] twoSum(int[] nums, int target) {
    // Write your solution here
    return new int[]{};
}""",
        "TypeScript" to """function twoSum(nums: number[], target: number): number[] {
  // Write your solution here
  return [];
}"""
      ),
      testCases = listOf(
        TestCase("[2,7,11,15], target = 9", "[0, 1]"),
        TestCase("[3,2,4], target = 6", "[1, 2]"),
        TestCase("[3,3], target = 6", "[0, 1]")
      ),
      hints = listOf(
        "Hint 1 (Concept): A naive double loop checks all pairs in O(n²). Can we check if complement exists in O(1)?",
        "Hint 2 (Approach): Use a Hash Map to store previously seen numbers and their indices.",
        "Hint 3 (Pseudocode): For each number x, complement = target - x. Check if complement in map.",
        "Hint 4 (Full): Map stores {num: index}. If (target - num) exists, return [map[target - num], current_index]."
      ),
      solutionExplanation = "By maintaining a hash map from value to index, we can query complement existence in O(1) time per item, achieving total O(n) time and O(n) space.",
      timeComplexity = "O(n)",
      spaceComplexity = "O(n)",
      optimalCode = mapOf(
        "Python" to """def two_sum(nums: list[int], target: int) -> list[int]:
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []"""
      )
    ),

    Problem(
      id = "valid-parentheses",
      title = "Valid Parentheses",
      category = "Stack",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "41.0%",
      estimatedMinutes = 15,
      description = "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. Open brackets must be closed by the same type of brackets in the correct order.",
      examples = listOf(
        ProblemExample("s = \"()[]{}\"", "true"),
        ProblemExample("s = \"(]\"", "false"),
        ProblemExample("s = \"([])\"", "true")
      ),
      constraints = listOf("1 <= s.length <= 10^4", "s consists of parentheses only '()[]{}'"),
      starterCode = mapOf(
        "Python" to """def is_valid(s: str) -> bool:
    # Use stack
    pass"""
      ),
      testCases = listOf(
        TestCase("\"()\"", "true"),
        TestCase("\"()[]{}\"", "true"),
        TestCase("\"(]\"", "false")
      ),
      hints = listOf(
        "Hint 1: Last opened bracket must be the first closed bracket.",
        "Hint 2: A Stack (LIFO) matches this behavior naturally.",
        "Hint 3: Push opening brackets onto stack, on closing bracket check if top matches."
      ),
      solutionExplanation = "Push opening brackets into stack. When closing bracket encountered, pop top and ensure matching pair.",
      timeComplexity = "O(n)",
      spaceComplexity = "O(n)",
      optimalCode = mapOf(
        "Python" to """def is_valid(s: str) -> bool:
    stack = []
    mapping = {')': '(', '}': '{', ']': '['}
    for char in s:
        if char in mapping:
            top = stack.pop() if stack else '#'
            if mapping[char] != top:
                return False
        else:
            stack.append(char)
    return not stack"""
      )
    ),

    Problem(
      id = "reverse-linked-list",
      title = "Reverse Linked List",
      category = "Linked List",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "76.5%",
      estimatedMinutes = 20,
      description = "Given the head of a singly linked list, reverse the list, and return the reversed list.",
      examples = listOf(
        ProblemExample("head = [1,2,3,4,5]", "[5,4,3,2,1]"),
        ProblemExample("head = [1,2]", "[2,1]")
      ),
      constraints = listOf("Number of nodes in range [0, 5000]", "-5000 <= Node.val <= 5000"),
      starterCode = mapOf(
        "Python" to """def reverse_list(head):
    # Iterative 3-pointer approach
    pass"""
      ),
      testCases = listOf(
        TestCase("[1,2,3,4,5]", "[5,4,3,2,1]"),
        TestCase("[1,2]", "[2,1]")
      ),
      hints = listOf(
        "Hint 1: Maintain three pointers: prev, curr, next_temp.",
        "Hint 2: Point curr.next to prev, then advance prev and curr."
      ),
      solutionExplanation = "Iterative reversal modifies pointers in-place with O(1) additional memory.",
      timeComplexity = "O(n)",
      spaceComplexity = "O(1)",
      optimalCode = mapOf(
        "Python" to """def reverse_list(head):
    prev = None
    curr = head
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt
    return prev"""
      )
    ),

    Problem(
      id = "climbing-stairs",
      title = "Climbing Stairs",
      category = "Dynamic Programming",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "52.8%",
      estimatedMinutes = 15,
      description = "You are climbing a staircase. It takes n steps to reach the top. Each time you can climb 1 or 2 steps. In how many distinct ways can you climb to the top?",
      examples = listOf(
        ProblemExample("n = 2", "2", "1 step + 1 step, or 2 steps."),
        ProblemExample("n = 3", "3", "1+1+1, 1+2, 2+1.")
      ),
      constraints = listOf("1 <= n <= 45"),
      starterCode = mapOf(
        "Python" to """def climb_stairs(n: int) -> int:
    pass"""
      ),
      testCases = listOf(
        TestCase("n = 2", "2"),
        TestCase("n = 3", "3"),
        TestCase("n = 4", "5")
      ),
      hints = listOf(
        "Hint 1: To reach step n, you must arrive from step n-1 or n-2.",
        "Hint 2: Ways(n) = Ways(n-1) + Ways(n-2) — identical to Fibonacci!"
      ),
      solutionExplanation = "Subproblems overlap. Base cases ways(1)=1, ways(2)=2. Dynamic programming computes in O(n) time.",
      timeComplexity = "O(n)",
      spaceComplexity = "O(1)",
      optimalCode = mapOf(
        "Python" to """def climb_stairs(n: int) -> int:
    if n <= 2: return n
    first, second = 1, 2
    for _ in range(3, n + 1):
        first, second = second, first + second
    return second"""
      )
    ),

    Problem(
      id = "binary-search-problem",
      title = "Binary Search",
      category = "Searching",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "58.1%",
      estimatedMinutes = 15,
      description = "Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1.",
      examples = listOf(
        ProblemExample("nums = [-1,0,3,5,9,12], target = 9", "4"),
        ProblemExample("nums = [-1,0,3,5,9,12], target = 2", "-1")
      ),
      constraints = listOf("1 <= nums.length <= 10^4", "All integers in nums are unique"),
      starterCode = mapOf("Python" to "def search(nums: list[int], target: int) -> int:\n    pass"),
      testCases = listOf(TestCase("nums = [-1,0,3,5,9,12], target = 9", "4")),
      hints = listOf("Hint: Cut search range in half each comparison."),
      solutionExplanation = "Classic binary search in logarithmic time.",
      timeComplexity = "O(log n)",
      spaceComplexity = "O(1)",
      optimalCode = mapOf("Python" to "def search(nums, target):\n    l, r = 0, len(nums)-1\n    while l <= r:\n        m = (l+r)//2\n        if nums[m] == target: return m\n        elif nums[m] < target: l = m+1\n        else: r = m-1\n    return -1")
    ),

    Problem(
      id = "coin-change",
      title = "Coin Change",
      category = "Dynamic Programming",
      difficulty = ProblemDifficulty.MEDIUM,
      acceptanceRate = "43.7%",
      estimatedMinutes = 25,
      description = "You are given an integer array coins representing coins of different denominations and an integer amount. Return the fewest number of coins that you need to make up that amount. If cannot be made up, return -1.",
      examples = listOf(
        ProblemExample("coins = [1,2,5], amount = 11", "3", "11 = 5 + 5 + 1"),
        ProblemExample("coins = [2], amount = 3", "-1")
      ),
      constraints = listOf("1 <= coins.length <= 12", "0 <= amount <= 10^4"),
      starterCode = mapOf("Python" to "def coin_change(coins: list[int], amount: int) -> int:\n    pass"),
      testCases = listOf(TestCase("[1,2,5], 11", "3"), TestCase("[2], 3", "-1")),
      hints = listOf("Hint 1: Bottom-up DP where dp[i] = min coins to form value i."),
      solutionExplanation = "dp[i] = min(dp[i], dp[i - coin] + 1) for coin in coins.",
      timeComplexity = "O(amount * len(coins))",
      spaceComplexity = "O(amount)",
      optimalCode = mapOf("Python" to "def coin_change(coins, amount):\n    dp = [float('inf')] * (amount + 1)\n    dp[0] = 0\n    for i in range(1, amount + 1):\n        for c in coins:\n            if i - c >= 0:\n                dp[i] = min(dp[i], dp[i - c] + 1)\n    return dp[amount] if dp[amount] != float('inf') else -1")
    ),

    Problem(
      id = "invert-binary-tree",
      title = "Invert Binary Tree",
      category = "Binary Tree",
      difficulty = ProblemDifficulty.EASY,
      acceptanceRate = "77.9%",
      estimatedMinutes = 15,
      description = "Given the root of a binary tree, invert the tree (swap left and right subtrees recursively), and return its root.",
      examples = listOf(ProblemExample("root = [4,2,7,1,3,6,9]", "[4,7,2,9,6,3,1]")),
      constraints = listOf("0 <= Number of nodes <= 100"),
      starterCode = mapOf("Python" to "def invert_tree(root):\n    pass"),
      testCases = listOf(TestCase("root = [4,2,7]", "[4,7,2]")),
      hints = listOf("Hint: Recursively swap root.left and root.right."),
      solutionExplanation = "Post-order or pre-order traversal swapping children.",
      timeComplexity = "O(n)",
      spaceComplexity = "O(h)",
      optimalCode = mapOf("Python" to "def invert_tree(root):\n    if not root: return None\n    root.left, root.right = invert_tree(root.right), invert_tree(root.left)\n    return root")
    )
  )

  val badges: List<Badge> = listOf(
    Badge("first-step", "First Algorithm", "Ran your first visualizer algorithm", "🚀", "Progress", 1, 1, true),
    Badge("streak-7", "7 Day Streak", "Learned DSA consistently for 7 straight days", "🔥", "Streak", 7, 12, true),
    Badge("streak-30", "30 Day Streak", "Maintain a 30-day algorithmic momentum", "⚡", "Streak", 30, 12, false),
    Badge("sorting-starter", "Sorting Apprentice", "Visualized Bubble, Selection, and Quick Sort", "📊", "Sorting", 3, 3, true),
    Badge("graph-explorer", "Graph Explorer", "Explored BFS and Dijkstra traversals", "🌐", "Graphs", 2, 1, false),
    Badge("dp-conqueror", "DP Conqueror", "Solved 5 Dynamic Programming challenges", "🧩", "DP", 5, 2, false),
    Badge("century-solver", "100 Problems", "Solved 100 DSA coding problems", "🏆", "Mastery", 100, 87, false),
    Badge("interview-ready", "Interview Ready", "Completed full technical mock simulation", "🎯", "Career", 1, 0, false)
  )

  val roadmapStages: List<RoadmapStage> = listOf(
    RoadmapStage(
      id = "stage-1",
      title = "Programming & Arrays",
      subtitle = "Foundational memory layouts, pointers, searching & sliding window",
      order = 1,
      iconEmoji = "📦",
      topics = listOf("Array Traversal", "Two Pointers", "Sliding Window", "Binary Search"),
      algorithmIds = listOf("linear-search", "binary-search"),
      problemIds = listOf("two-sum", "binary-search-problem"),
      quizId = "quiz-arrays"
    ),
    RoadmapStage(
      id = "stage-2",
      title = "Sorting & Complexities",
      subtitle = "Divide-and-conquer, partition mechanisms, O(n log n) efficiency",
      order = 2,
      iconEmoji = "⚡",
      topics = listOf("Bubble Sort", "Insertion Sort", "Selection Sort", "Quick Sort", "Merge Sort"),
      algorithmIds = listOf("bubble-sort", "selection-sort", "insertion-sort", "quick-sort"),
      problemIds = listOf("two-sum"),
      quizId = "quiz-sorting"
    ),
    RoadmapStage(
      id = "stage-3",
      title = "Linear Structures",
      subtitle = "Linked Lists, LIFO Stacks & FIFO Queues",
      order = 3,
      iconEmoji = "🔗",
      topics = listOf("Singly Linked List", "Stack LIFO", "Queue FIFO", "Balanced Parentheses"),
      algorithmIds = listOf("stack-operations", "queue-operations"),
      problemIds = listOf("reverse-linked-list", "valid-parentheses"),
      quizId = "quiz-stacks"
    ),
    RoadmapStage(
      id = "stage-4",
      title = "Trees & Graphs",
      subtitle = "Hierarchical structures, BFS, DFS, and topological orderings",
      order = 4,
      iconEmoji = "🌲",
      topics = listOf("Binary Search Tree", "Tree Traversals", "BFS Traversal", "DFS Traversal"),
      algorithmIds = listOf("binary-search-tree", "graph-bfs"),
      problemIds = listOf("invert-binary-tree"),
      quizId = "quiz-graphs"
    ),
    RoadmapStage(
      id = "stage-5",
      title = "Dynamic Programming",
      subtitle = "Optimal substructure, overlapping subproblems, memoization & tabulation",
      order = 5,
      iconEmoji = "🧩",
      topics = listOf("Fibonacci", "Climbing Stairs", "Coin Change", "0/1 Knapsack"),
      algorithmIds = listOf("fibonacci-dp"),
      problemIds = listOf("climbing-stairs", "coin-change"),
      quizId = "quiz-dp"
    )
  )

  val quizzes: List<Quiz> = listOf(
    Quiz(
      id = "quiz-arrays",
      title = "Arrays & Binary Search Check",
      category = "Arrays",
      description = "Test your intuition on array complexities and search boundaries.",
      questions = listOf(
        QuizQuestion(
          id = "q1",
          topic = "Searching",
          questionText = "What is the worst-case time complexity of Binary Search on an array of length N?",
          options = listOf("O(1)", "O(log N)", "O(N)", "O(N log N)"),
          correctOptionIndex = 1,
          explanation = "Binary Search halves the remaining search interval at each step, taking at most log2(N) comparisons.",
          type = QuestionType.COMPLEXITY
        ),
        QuizQuestion(
          id = "q2",
          topic = "Arrays",
          questionText = "Which prerequisite must be satisfied before Binary Search can be applied?",
          options = listOf("Elements must all be positive", "Array must be sorted", "Array must have even length", "Array cannot contain duplicates"),
          correctOptionIndex = 1,
          explanation = "Binary Search relies on sorted order to deduce which half contains the target.",
          type = QuestionType.MCQ
        ),
        QuizQuestion(
          id = "q3",
          topic = "Complexities",
          questionText = "What is the time complexity to insert an element at index 0 of an ArrayList of size N?",
          options = listOf("O(1)", "O(log N)", "O(N)", "O(N²)"),
          correctOptionIndex = 2,
          explanation = "All subsequent N elements must be shifted one position to the right.",
          type = QuestionType.COMPLEXITY
        )
      )
    ),
    Quiz(
      id = "quiz-sorting",
      title = "Sorting Algorithms Mastery",
      category = "Sorting",
      description = "Examine stability, worst-case behaviors, and partitioning logic.",
      questions = listOf(
        QuizQuestion(
          id = "qs1",
          topic = "Quick Sort",
          questionText = "What is the worst-case time complexity of Quick Sort?",
          options = listOf("O(N log N)", "O(N)", "O(N²)", "O(2^N)"),
          correctOptionIndex = 2,
          explanation = "When pivot chosen is repeatedly the minimum or maximum element (e.g. sorted array without random pivot), Quick Sort degrades to O(N²).",
          type = QuestionType.COMPLEXITY
        ),
        QuizQuestion(
          id = "qs2",
          topic = "Stability",
          questionText = "Which sorting algorithm below is STABLE by default?",
          options = listOf("Selection Sort", "Merge Sort", "Heap Sort", "Quick Sort"),
          correctOptionIndex = 1,
          explanation = "Merge Sort preserves relative order of duplicate elements because it prioritizes the left subarray during merge.",
          type = QuestionType.MCQ
        )
      )
    )
  )

  val careerChecklist: List<CareerItem> = listOf(
    CareerItem("c1", "Resume", "1-page concise layout", "Single column, clean typography, PDF format with active hyperlinks.", true),
    CareerItem("c2", "Resume", "Action-Impact bullet points", "Formula: 'Accomplished [X] as measured by [Y], by doing [Z]'.", true),
    CareerItem("c3", "GitHub", "Pinned full-stack projects", "Include live demo link, architecture diagram, and clean README.", true),
    CareerItem("c4", "GitHub", "Consistent green contribution graph", "Daily algorithmic commits and open source contributions.", false),
    CareerItem("c5", "CS Fundamentals", "Operating Systems & Concurrency", "Processes vs Threads, Mutex/Semaphore, Deadlocks, Virtual Memory.", true),
    CareerItem("c6", "CS Fundamentals", "Database Indexing & ACID", "B-Tree vs Hash index, transactions, isolation levels.", false),
    CareerItem("c7", "Interview Prep", "Big-O Analysis fluency", "Can evaluate Time and Space complexity instantly without hesitating.", true),
    CareerItem("c8", "Interview Prep", "Mock Interview Simulation", "Practice speaking out loud while designing data structures.", false)
  )
}
