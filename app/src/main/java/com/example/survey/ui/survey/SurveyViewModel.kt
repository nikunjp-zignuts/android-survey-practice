package com.example.survey.ui.survey

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.survey.data.SurveyRepository
import com.example.survey.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SurveyViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = SurveyRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val questions = repository.getQuestions()
        _uiState.update { currentState ->
            currentState.copy(questions = questions)
        }
    }

    fun selectTab(tab: ActiveTab) {
        _uiState.update { currentState ->
            currentState.copy(
                activeTab = tab,
                isTakingSurvey = false
            )
        }
    }

    fun startSurvey(questionIndex: Int = 0) {
        _uiState.update { currentState ->
            currentState.copy(
                currentQuestionIndex = questionIndex,
                isTakingSurvey = true,
                isSubmitted = false,
                selectedAnswers = emptyMap()
            )
        }
    }

    fun exitSurvey() {
        _uiState.update { currentState ->
            currentState.copy(
                isTakingSurvey = false,
                isSubmitted = false
            )
        }
    }

    fun selectAnswer(answerIndex: Int) {
        _uiState.update { currentState ->
            val updatedAnswers = currentState.selectedAnswers.toMutableMap().apply {
                put(currentState.currentQuestionIndex, answerIndex)
            }
            currentState.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun nextQuestion() {
        _uiState.update { currentState ->
            if (currentState.isNextEnabled) {
                currentState.copy(currentQuestionIndex = currentState.currentQuestionIndex + 1)
            } else {
                currentState
            }
        }
    }

    fun previousQuestion() {
        _uiState.update { currentState ->
            if (currentState.isBackEnabled) {
                currentState.copy(currentQuestionIndex = currentState.currentQuestionIndex - 1)
            } else {
                currentState
            }
        }
    }

    fun submitSurvey() {
        _uiState.update { currentState ->
            if (currentState.canSubmit) {
                currentState.copy(isSubmitted = true)
            } else {
                currentState
            }
        }
    }

    fun restartSurvey() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswers = emptyMap(),
                isSubmitted = false,
                currentQuestionIndex = 0
            )
        }
    }

    fun addQuestion(question: Question) {
        repository.saveQuestion(question)
        val updatedQuestions = repository.getQuestions()
        _uiState.update { currentState ->
            currentState.copy(
                questions = updatedQuestions,
                activeTab = ActiveTab.HOME,
                isTakingSurvey = false
            )
        }
    }
}
