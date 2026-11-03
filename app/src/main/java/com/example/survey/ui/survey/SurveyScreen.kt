package com.example.survey.ui.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.survey.model.AnswerOption
import com.example.survey.model.Question
import com.example.survey.ui.survey.components.AnswerTile
import com.example.survey.ui.survey.components.QuestionTile
import com.example.survey.ui.theme.SurveyTheme

@Composable
fun SurveyScreen(
    modifier: Modifier = Modifier,
    viewModel: SurveyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (!uiState.isTakingSurvey) {
                NavigationBar {
                    NavigationBarItem(
                        selected = uiState.activeTab == ActiveTab.HOME,
                        onClick = { viewModel.selectTab(ActiveTab.HOME) },
                        icon = { Icon(BottomNavItem.Home.icon, contentDescription = BottomNavItem.Home.title) },
                        label = { Text(BottomNavItem.Home.title) }
                    )
                    NavigationBarItem(
                        selected = uiState.activeTab == ActiveTab.CREATE,
                        onClick = { viewModel.selectTab(ActiveTab.CREATE) },
                        icon = { Icon(BottomNavItem.Create.icon, contentDescription = BottomNavItem.Create.title) },
                        label = { Text(BottomNavItem.Create.title) }
                    )
                    NavigationBarItem(
                        selected = uiState.activeTab == ActiveTab.HISTORY,
                        onClick = { viewModel.selectTab(ActiveTab.HISTORY) },
                        icon = { Icon(BottomNavItem.History.icon, contentDescription = BottomNavItem.History.title) },
                        label = { Text(BottomNavItem.History.title) }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                uiState.isTakingSurvey -> {
                    if (uiState.isSubmitted) {
                        SurveyResultScreen(
                            score = uiState.score,
                            totalQuestions = uiState.questions.size,
                            onRestart = viewModel::restartSurvey,
                            onExit = viewModel::exitSurvey
                        )
                    } else {
                        SurveyContent(
                            uiState = uiState,
                            onSelectAnswer = viewModel::selectAnswer,
                            onPreviousQuestion = viewModel::previousQuestion,
                            onNextQuestion = viewModel::nextQuestion,
                            onSubmit = viewModel::submitSurvey,
                            onExit = viewModel::exitSurvey
                        )
                    }
                }

                uiState.activeTab == ActiveTab.CREATE -> {
                    CreateQuestionScreen(
                        onSaveQuestion = viewModel::addQuestion,
                        onCancel = { viewModel.selectTab(ActiveTab.HOME) }
                    )
                }

                uiState.activeTab == ActiveTab.HISTORY -> {
                    SurveyHistoryScreen(
                        questions = uiState.questions,
                        onStartSurvey = { index -> viewModel.startSurvey(index) }
                    )
                }

                else -> {
                    SurveyListScreen(
                        questions = uiState.questions,
                        onStartSurvey = { index -> viewModel.startSurvey(index) },
                        onCreateSurveyClicked = { viewModel.selectTab(ActiveTab.CREATE) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyContent(
    uiState: SurveyUiState,
    onSelectAnswer: (Int) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onSubmit: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.currentQuestion

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taking Survey") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.Close, contentDescription = "Exit Survey")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            currentQuestion?.let { question ->
                QuestionTile(
                    name = question.text,
                    isRequired = question.isRequired,
                    questionNumber = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.questions.size
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(question.options.size) { index ->
                        val option = question.options[index]
                        AnswerTile(
                            name = option.text,
                            imageUrl = option.imageUrl,
                            emoji = option.emoji,
                            selected = uiState.selectedAnswerForCurrentQuestion == index,
                            onSelect = { onSelectAnswer(index) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPreviousQuestion,
                    enabled = uiState.isBackEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                if (uiState.isLastQuestion) {
                    Button(
                        onClick = onSubmit,
                        enabled = uiState.canSubmit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Submit")
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        enabled = uiState.isNextEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
fun SurveyResultScreen(
    score: Int,
    totalQuestions: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Survey Completed!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Score: $score / $totalQuestions",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onExit) {
                    Text("Home")
                }

                Button(onClick = onRestart) {
                    Text("Retake Survey")
                }
            }
        }
    }
}

@Composable
fun SurveyHistoryScreen(
    questions: List<Question>,
    onStartSurvey: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Survey Overview",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Questions: ${questions.size}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onStartSurvey(0) }) {
                Text("Start Full Survey (${questions.size} Questions)")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyContentPreview() {
    val sampleQuestion = Question(
        id = 1,
        text = "What is the official programming language for Android development recommended by Google?",
        isRequired = true,
        options = listOf(
            AnswerOption(id = "a", text = "Kotlin", isCorrect = true, emoji = "🎯"),
            AnswerOption(id = "b", text = "Swift", isCorrect = false, emoji = "⚡"),
            AnswerOption(id = "c", text = "Ruby", isCorrect = false, emoji = "💎"),
            AnswerOption(id = "d", text = "C#", isCorrect = false, emoji = "💻")
        )
    )

    val sampleUiState = SurveyUiState(
        questions = listOf(sampleQuestion),
        currentQuestionIndex = 0,
        selectedAnswers = mapOf(0 to 0)
    )

    SurveyTheme {
        SurveyContent(
            uiState = sampleUiState,
            onSelectAnswer = {},
            onPreviousQuestion = {},
            onNextQuestion = {},
            onSubmit = {},
            onExit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyResultPreview() {
    SurveyTheme {
        SurveyResultScreen(
            score = 3,
            totalQuestions = 4,
            onRestart = {},
            onExit = {}
        )
    }
}
