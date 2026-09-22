package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.repository.DSADataRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.service.GeminiApiService
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.BgDeepDark
import com.example.ui.theme.MyApplicationTheme

enum class SecondaryScreen {
  NONE,
  ROADMAP,
  QUIZ,
  INTERVIEW,
  CAREER
}

class MainActivity : ComponentActivity() {
  private lateinit var userPrefsRepo: UserPreferencesRepository
  private lateinit var authService: com.example.service.FirebaseAuthService
  private val geminiService = GeminiApiService()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    userPrefsRepo = UserPreferencesRepository(applicationContext)
    authService = com.example.service.FirebaseAuthService(applicationContext)

    setContent {
      MyApplicationTheme {
        DSAFlowApp(
          userPrefsRepo = userPrefsRepo,
          authService = authService,
          geminiService = geminiService
        )
      }
    }
  }
}

@Composable
fun DSAFlowApp(
  userPrefsRepo: UserPreferencesRepository,
  authService: com.example.service.FirebaseAuthService,
  geminiService: GeminiApiService
) {
  val userProfile by userPrefsRepo.userProfile.collectAsState()
  val currentUser by authService.currentUser.collectAsState()
  val isFirebaseActive by authService.isFirebaseActive.collectAsState()
  var isAuthDialogOpen by remember { mutableStateOf(false) }

  // Sync user profile personalization whenever auth user switches
  LaunchedEffect(currentUser) {
    userPrefsRepo.switchUser(currentUser?.uid, currentUser?.displayName)
  }

  var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
  var secondaryScreen by remember { mutableStateOf(SecondaryScreen.NONE) }

  // Target algorithm/problem to open directly from Home or Search
  var activeVisualizerAlgorithmId by remember { mutableStateOf<String?>(null) }
  var activePracticeProblemId by remember { mutableStateOf<String?>(null) }

  // Global search dialog state
  var isSearchOpen by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BgDeepDark)
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = BgDeepDark,
      topBar = {
        if (secondaryScreen == SecondaryScreen.NONE) {
          DSAFlowTopBar(
            streakDays = userProfile.streakDays,
            totalXp = userProfile.totalXp,
            onSearchClick = { isSearchOpen = true },
            onCommandPaletteClick = { isSearchOpen = true },
            currentUser = currentUser,
            onAuthClick = { isAuthDialogOpen = true }
          )
        }
      },
      bottomBar = {
        if (secondaryScreen == SecondaryScreen.NONE) {
          DSAFlowBottomNav(
            currentDestination = currentDestination,
            onSelectDestination = { dest ->
              currentDestination = dest
              secondaryScreen = SecondaryScreen.NONE
            }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (secondaryScreen) {
          SecondaryScreen.ROADMAP -> {
            RoadmapScreen(
              stages = DSADataRepository.roadmapStages,
              onBack = { secondaryScreen = SecondaryScreen.NONE },
              onSelectAlgorithm = { algoId ->
                activeVisualizerAlgorithmId = algoId
                currentDestination = NavDestination.VISUALIZE
                secondaryScreen = SecondaryScreen.NONE
              },
              onSelectProblem = { probId ->
                activePracticeProblemId = probId
                currentDestination = NavDestination.PRACTICE
                secondaryScreen = SecondaryScreen.NONE
              }
            )
          }
          SecondaryScreen.QUIZ -> {
            QuizScreen(
              quizzes = DSADataRepository.quizzes,
              onBack = { secondaryScreen = SecondaryScreen.NONE },
              onAwardXp = { earned -> userPrefsRepo.addXp(earned) }
            )
          }
          SecondaryScreen.INTERVIEW -> {
            InterviewArenaScreen(
              onBack = { secondaryScreen = SecondaryScreen.NONE },
              geminiService = geminiService
            )
          }
          SecondaryScreen.CAREER -> {
            CareerChecklistScreen(
              onBack = { secondaryScreen = SecondaryScreen.NONE }
            )
          }
          SecondaryScreen.NONE -> {
            when (currentDestination) {
              NavDestination.HOME -> {
                HomeScreen(
                  userProfile = userProfile,
                  onNavigateToVisualizer = { algoId ->
                    activeVisualizerAlgorithmId = algoId
                    currentDestination = NavDestination.VISUALIZE
                  },
                  onNavigateToPractice = { probId ->
                    activePracticeProblemId = probId
                    currentDestination = NavDestination.PRACTICE
                  },
                  onNavigateToDestination = { dest -> currentDestination = dest },
                  onOpenRoadmap = { secondaryScreen = SecondaryScreen.ROADMAP },
                  onOpenQuiz = { secondaryScreen = SecondaryScreen.QUIZ },
                  onOpenInterview = { secondaryScreen = SecondaryScreen.INTERVIEW },
                  onOpenCareer = { secondaryScreen = SecondaryScreen.CAREER }
                )
              }
              NavDestination.VISUALIZE -> {
                VisualizerScreen(
                  algorithms = DSADataRepository.algorithms,
                  initialAlgorithmId = activeVisualizerAlgorithmId,
                  geminiService = geminiService,
                  onAlgorithmCompleted = { algoId ->
                    userPrefsRepo.markAlgorithmCompleted(algoId)
                    userPrefsRepo.addXp(15)
                  }
                )
              }
              NavDestination.PRACTICE -> {
                PracticeScreen(
                  problems = DSADataRepository.problems,
                  initialProblemId = activePracticeProblemId,
                  solvedProblemIds = userProfile.solvedProblemIds,
                  onSolveProblem = { probId ->
                    userPrefsRepo.markProblemSolved(probId, xpAward = 25)
                  },
                  geminiService = geminiService
                )
              }
              NavDestination.AI_MENTOR -> {
                AIMentorScreen(geminiService = geminiService)
              }
              NavDestination.PROFILE -> {
                ProfileScreen(
                  userProfile = userProfile,
                  userPrefsRepo = userPrefsRepo,
                  onOpenCareer = { secondaryScreen = SecondaryScreen.CAREER },
                  currentUser = currentUser,
                  isFirebaseActive = isFirebaseActive,
                  onOpenAuth = { isAuthDialogOpen = true }
                )
              }
            }
          }
        }
      }
    }

    // Floating AI Assistant available on all screens
    if (currentDestination != NavDestination.AI_MENTOR && secondaryScreen == SecondaryScreen.NONE) {
      val activeContext = when (currentDestination) {
        NavDestination.VISUALIZE -> activeVisualizerAlgorithmId ?: "Algorithm Visualization"
        NavDestination.PRACTICE -> activePracticeProblemId ?: "Coding Practice Problem"
        else -> "Data Structures & Algorithms"
      }
      FloatingAIAssistant(
        currentContext = activeContext,
        geminiService = geminiService
      )
    }

    // Firebase Authentication Dialog Modal
    AuthDialog(
      isOpen = isAuthDialogOpen,
      onDismiss = { isAuthDialogOpen = false },
      authService = authService,
      currentUser = currentUser,
      onUserChanged = { user ->
        userPrefsRepo.switchUser(user?.uid, user?.displayName)
      }
    )

    // Global Search & Command Palette Modal
    GlobalSearchDialog(
      isOpen = isSearchOpen,
      onDismiss = { isSearchOpen = false },
      algorithms = DSADataRepository.algorithms,
      problems = DSADataRepository.problems,
      onSelectAlgorithm = { algo ->
        activeVisualizerAlgorithmId = algo.id
        currentDestination = NavDestination.VISUALIZE
        secondaryScreen = SecondaryScreen.NONE
      },
      onSelectProblem = { prob ->
        activePracticeProblemId = prob.id
        currentDestination = NavDestination.PRACTICE
        secondaryScreen = SecondaryScreen.NONE
      },
      onSelectDestination = { dest ->
        currentDestination = dest
        secondaryScreen = SecondaryScreen.NONE
      }
    )
  }
}
