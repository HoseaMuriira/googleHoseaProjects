package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonPlanEditorScreen
import com.example.ui.screens.SchemeEditorScreen
import com.example.ui.screens.SyllabusGuideScreen
import com.example.ui.screens.WordDocViewerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CurrentDocView
import com.example.viewmodel.SchemlyViewModel

sealed interface AppScreen {
    data object Home : AppScreen
    data class SchemeEditor(val scheme: SchemeOfWork) : AppScreen
    data class LessonPlanEditor(val plan: LessonPlan) : AppScreen
    data class WordDocViewer(val docView: CurrentDocView) : AppScreen
    data object SyllabusGuide : AppScreen
}

class MainActivity : ComponentActivity() {

    private val viewModel: SchemlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SchemlyApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SchemlyApp(viewModel: SchemlyViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val schemes by viewModel.schemes.collectAsState()
    val lessonPlans by viewModel.lessonPlans.collectAsState()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    // Keep active scheme/lesson plan synchronized when updated in DB
    val activeScheme = uiState.activeScheme ?: (currentScreen as? AppScreen.SchemeEditor)?.scheme
    val activeLessonPlan = uiState.activeLessonPlan ?: (currentScreen as? AppScreen.LessonPlanEditor)?.plan

    BackHandler(enabled = currentScreen !is AppScreen.Home) {
        currentScreen = AppScreen.Home
    }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                viewModel = viewModel,
                uiState = uiState,
                schemes = schemes,
                lessonPlans = lessonPlans,
                onOpenScheme = { scheme ->
                    viewModel.selectScheme(scheme)
                    currentScreen = AppScreen.SchemeEditor(scheme)
                },
                onOpenLessonPlan = { plan ->
                    viewModel.selectLessonPlan(plan)
                    currentScreen = AppScreen.LessonPlanEditor(plan)
                },
                onNavigateSyllabus = {
                    currentScreen = AppScreen.SyllabusGuide
                },
                onOpenWordViewer = { scheme ->
                    currentScreen = AppScreen.WordDocViewer(CurrentDocView.SchemeDoc(scheme))
                }
            )
        }

        is AppScreen.SchemeEditor -> {
            val schemeToEdit = schemes.find { it.id == screen.scheme.id } ?: screen.scheme
            SchemeEditorScreen(
                scheme = schemeToEdit,
                viewModel = viewModel,
                uiState = uiState,
                onNavigateHome = { currentScreen = AppScreen.Home },
                onOpenWordViewer = { scheme ->
                    currentScreen = AppScreen.WordDocViewer(CurrentDocView.SchemeDoc(scheme))
                },
                onOpenLessonPlan = { plan ->
                    viewModel.selectLessonPlan(plan)
                    currentScreen = AppScreen.LessonPlanEditor(plan)
                }
            )
        }

        is AppScreen.LessonPlanEditor -> {
            val planToEdit = lessonPlans.find { it.id == screen.plan.id } ?: screen.plan
            LessonPlanEditorScreen(
                plan = planToEdit,
                viewModel = viewModel,
                uiState = uiState,
                onNavigateHome = { currentScreen = AppScreen.Home },
                onOpenWordViewer = { plan ->
                    currentScreen = AppScreen.WordDocViewer(CurrentDocView.LessonDoc(plan))
                }
            )
        }

        is AppScreen.WordDocViewer -> {
            WordDocViewerScreen(
                docView = screen.docView,
                onNavigateBack = {
                    when (screen.docView) {
                        is CurrentDocView.SchemeDoc -> {
                            currentScreen = AppScreen.SchemeEditor(screen.docView.scheme)
                        }
                        is CurrentDocView.LessonDoc -> {
                            currentScreen = AppScreen.LessonPlanEditor(screen.docView.plan)
                        }
                    }
                }
            )
        }

        is AppScreen.SyllabusGuide -> {
            SyllabusGuideScreen(
                viewModel = viewModel,
                onNavigateHome = { currentScreen = AppScreen.Home },
                onGenerateFromSyllabus = { grade, subject ->
                    viewModel.createNewScheme(grade, subject, "Term 1", "JUNIOR SECONDARY SCHOOL", "")
                    val newScheme = uiState.activeScheme
                    if (newScheme != null) {
                        currentScreen = AppScreen.SchemeEditor(newScheme)
                    } else {
                        currentScreen = AppScreen.Home
                    }
                }
            )
        }
    }
}
