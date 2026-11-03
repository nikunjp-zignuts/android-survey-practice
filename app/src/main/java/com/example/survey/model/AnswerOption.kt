package com.example.survey.model

data class AnswerOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val imageUrl: String? = null,
    val emoji: String? = null
)
