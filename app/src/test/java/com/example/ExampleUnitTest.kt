package com.example

import com.example.data.model.HighlightType
import com.example.data.repository.AlgorithmStepGenerators
import com.example.data.repository.DSADataRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBinarySearchStepsGeneration() {
    val steps = AlgorithmStepGenerators.generateBinarySearch("1, 3, 5, 7, 9; 5")
    assertTrue(steps.isNotEmpty())
    assertEquals(1, steps.first().stepNumber)
    assertEquals(steps.size, steps.last().stepNumber)
    // Should find 5 at index 2
    assertTrue(steps.any { it.description.contains("Target 5 found at index 2") })
  }

  @Test
  fun testBubbleSortStepsGeneration() {
    val steps = AlgorithmStepGenerators.generateBubbleSort("5, 1, 4, 2, 8")
    assertTrue(steps.size > 2)
    val lastStep = steps.last()
    assertEquals(listOf(1, 2, 4, 5, 8), lastStep.arrayData)
  }

  @Test
  fun testStackOperationsStepsGeneration() {
    val steps = AlgorithmStepGenerators.generateStackOperations("")
    assertTrue(steps.size >= 4)
  }

  @Test
  fun testDSADataRepositoryIntegrity() {
    assertTrue(DSADataRepository.algorithms.isNotEmpty())
    assertTrue(DSADataRepository.problems.isNotEmpty())
    assertTrue(DSADataRepository.quizzes.isNotEmpty())
    assertTrue(DSADataRepository.badges.isNotEmpty())
    assertTrue(DSADataRepository.roadmapStages.isNotEmpty())
  }

  @Test
  fun testAuthUserModel() {
    val user = com.example.service.AuthUser(
      uid = "usr_12345",
      email = "developer@dsaflow.io",
      displayName = "Master Coder",
      isAnonymous = false
    )
    assertEquals("usr_12345", user.uid)
    assertEquals("developer@dsaflow.io", user.email)
    assertEquals("Master Coder", user.displayName)
    assertFalse(user.isAnonymous)
  }

  @Test
  fun testAuthResultHierarchy() {
    val success = com.example.service.AuthResult.Success(
      com.example.service.AuthUser("uid_99", "test@test.com", "Tester")
    )
    assertTrue(success is com.example.service.AuthResult)
    val error = com.example.service.AuthResult.Error("Invalid credentials")
    assertEquals("Invalid credentials", error.message)
  }
}
