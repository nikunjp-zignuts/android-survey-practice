package com.example.survey.model

data class Question(
    val id: Int,
    val text: String,
    val isRequired: Boolean = true,
    val options: List<AnswerOption>
)
