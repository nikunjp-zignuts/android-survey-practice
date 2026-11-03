package com.example.survey.data

import android.content.Context
import com.example.survey.model.AnswerOption
import com.example.survey.model.Question
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SurveyRepository(private val context: Context? = null) {

    private val localFile: File?
        get() = context?.let { File(it.filesDir, "questions_store.json") }

    fun getQuestions(): List<Question> {
        val jsonString = loadJsonString() ?: return emptyList()
        return parseQuestionsJson(jsonString)
    }

    fun saveQuestion(newQuestion: Question) {
        val currentQuestions = getQuestions().toMutableList()
        currentQuestions.add(newQuestion)
        saveQuestionsToFile(currentQuestions)
    }

    private fun saveQuestionsToFile(questions: List<Question>) {
        val file = localFile ?: return
        try {
            val jsonArray = JSONArray()
            for (q in questions) {
                val qObj = JSONObject().apply {
                    put("id", q.id)
                    put("question", q.text)
                    put("isRequired", q.isRequired)

                    val optionsArray = JSONArray()
                    for (opt in q.options) {
                        val optObj = JSONObject().apply {
                            put("id", opt.id)
                            put("text", opt.text)
                            put("isCorrect", opt.isCorrect)
                            opt.emoji?.let { put("emoji", it) }
                            opt.imageUrl?.let { put("imageUrl", it) }
                        }
                        optionsArray.put(optObj)
                    }
                    put("options", optionsArray)
                }
                jsonArray.put(qObj)
            }
            file.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadJsonString(): String? {
        val file = localFile
        if (file != null && file.exists()) {
            try {
                val content = file.readText()
                if (content.isNotBlank()) return content
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val defaultContent = try {
            if (context != null) {
                context.assets.open("questions.json").bufferedReader().use { it.readText() }
            } else {
                DEFAULT_JSON
            }
        } catch (_: Exception) {
            DEFAULT_JSON
        }

        if (file != null) {
            try {
                file.writeText(defaultContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return defaultContent
    }

    private fun parseQuestionsJson(jsonString: String): List<Question> {
        val questions = mutableListOf<Question>()
        try {
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val qObject = jsonArray.getJSONObject(i)
                val id = qObject.optInt("id", i + 1)
                val questionText = qObject.getString("question")
                val isRequired = qObject.optBoolean("isRequired", true)

                val optionsArray = qObject.getJSONArray("options")
                val options = mutableListOf<AnswerOption>()

                for (j in 0 until optionsArray.length()) {
                    val optObject = optionsArray.getJSONObject(j)
                    val optId = optObject.optString("id", (j + 1).toString())
                    val optText = optObject.getString("text")
                    val isCorrect = optObject.optBoolean("isCorrect", false)
                    val emoji = if (optObject.has("emoji") && !optObject.isNull("emoji")) {
                        optObject.getString("emoji")
                    } else null
                    val imageUrl = if (optObject.has("imageUrl") && !optObject.isNull("imageUrl")) {
                        optObject.getString("imageUrl")
                    } else null

                    options.add(
                        AnswerOption(
                            id = optId,
                            text = optText,
                            isCorrect = isCorrect,
                            imageUrl = imageUrl,
                            emoji = emoji
                        )
                    )
                }

                questions.add(
                    Question(
                        id = id,
                        text = questionText,
                        isRequired = isRequired,
                        options = options
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return questions
    }

    companion object {
        private const val DEFAULT_JSON = """
        [
          {
            "id": 1,
            "question": "What is the official programming language for Android development recommended by Google?",
            "isRequired": true,
            "options": [
              { "id": "a", "text": "Kotlin", "isCorrect": true, "emoji": "🎯" },
              { "id": "b", "text": "Swift", "isCorrect": false, "emoji": "⚡" },
              { "id": "c", "text": "Ruby", "isCorrect": false, "emoji": "💎" },
              { "id": "d", "text": "C#", "isCorrect": false, "emoji": "💻" }
            ]
          },
          {
            "id": 2,
            "question": "Which toolkit is used for building native UI in Android with declarative Kotlin code?",
            "isRequired": true,
            "options": [
              { "id": "a", "text": "Flutter", "isCorrect": false, "emoji": "💙" },
              { "id": "b", "text": "Jetpack Compose", "isCorrect": true, "emoji": "🚀" },
              { "id": "c", "text": "React Native", "isCorrect": false, "emoji": "⚛️" },
              { "id": "d", "text": "Xamarin", "isCorrect": false, "emoji": "📱" }
            ]
          },
          {
            "id": 3,
            "question": "What is your feedback on this survey application?",
            "isRequired": false,
            "options": [
              { "id": "a", "text": "Excellent", "isCorrect": true, "emoji": "😍" },
              { "id": "b", "text": "Good", "isCorrect": true, "emoji": "👍" },
              { "id": "c", "text": "Average", "isCorrect": false, "emoji": "😐" },
              { "id": "d", "text": "Needs Improvement", "isCorrect": false, "emoji": "👎" }
            ]
          }
        ]
        """
    }
}
