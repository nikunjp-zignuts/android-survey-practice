package com.example.survey.ui.survey

import com.example.survey.model.Question

enum class ActiveTab {
    HOME,
    CREATE,
    HISTORY
}

data class SurveyUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val isSubmitted: Boolean = false,
    val isTakingSurvey: Boolean = false,
    val activeTab: ActiveTab = ActiveTab.HOME
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)
    val selectedAnswerForCurrentQuestion: Int? get() = selectedAnswers[currentQuestionIndex]

    val isBackEnabled: Boolean get() = currentQuestionIndex > 0
    val isNextEnabled: Boolean
        get() {
            val question = currentQuestion ?: return false
            return currentQuestionIndex < questions.lastIndex && (!question.isRequired || selectedAnswerForCurrentQuestion != null)
        }

    val isLastQuestion: Boolean get() = currentQuestionIndex == questions.lastIndex && questions.isNotEmpty()

    val canSubmit: Boolean
        get() {
            if (!isLastQuestion) return false
            val question = currentQuestion ?: return false
            return !question.isRequired || selectedAnswerForCurrentQuestion != null
        }

    val score: Int
        get() {
            return questions.indices.count { index ->
                val selectedOptionIndex = selectedAnswers[index]
                selectedOptionIndex != null && questions[index].options.getOrNull(
                    selectedOptionIndex
                )?.isCorrect == true
            }
        }
}
