package com.example.survey.ui.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.survey.model.AnswerOption
import com.example.survey.model.Question
import com.example.survey.ui.theme.SurveyTheme

data class DraftOption(
    var text: String = "",
    var emoji: String = "😀",
    var isCorrect: Boolean = false
)

private val presetEmojis = listOf(
    "😀", "👍", "👎", "❤️", "🎉", "🚀", "🔥", "⭐", "💡", "👏",
    "✅", "❌", "🎯", "😃", "🤔", "💯", "📱", "💻", "🎨", "🏆"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionScreen(
    onSaveQuestion: (Question) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var questionText by remember { mutableStateOf("") }
    var isRequired by remember { mutableStateOf(true) }

    val options = remember {
        mutableStateListOf(
            DraftOption(text = "Option 1", emoji = "👍", isCorrect = true),
            DraftOption(text = "Option 2", emoji = "👎", isCorrect = false)
        )
    }

    var selectedCorrectIndex by remember { mutableStateOf(0) }

    val canSave = questionText.isNotBlank() && options.all { it.text.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Question") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Text Input
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question Text") },
                placeholder = { Text("e.g. What is your favorite Android feature?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )

            // Is Required Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Required Question", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Users must answer before proceeding",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isRequired,
                    onCheckedChange = { isRequired = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Answer Options Section
            Text(
                "Answer Options & Emojis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            options.forEachIndexed { index, draftOption ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.5f
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCorrectIndex == index,
                                onClick = {
                                    selectedCorrectIndex = index
                                }
                            )

                            OutlinedTextField(
                                value = draftOption.text,
                                onValueChange = { newText ->
                                    options[index] = draftOption.copy(text = newText)
                                },
                                label = { Text("Answer ${index + 1}") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            if (options.size > 2) {
                                IconButton(
                                    onClick = {
                                        options.removeAt(index)
                                        if (selectedCorrectIndex >= options.size) {
                                            selectedCorrectIndex = 0
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Option",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Emoji: ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    draftOption.emoji,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = draftOption.emoji,
                                onValueChange = { newEmoji ->
                                    options[index] = draftOption.copy(emoji = newEmoji)
                                },
                                label = { Text("Custom Emoji") },
                                modifier = Modifier.width(130.dp),
                                singleLine = true
                            )
                        }

                        Text(
                            "Select Emoji:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(presetEmojis) { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (draftOption.emoji == emoji) MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.2f
                                            ) else Color.Transparent
                                        )
                                        .border(
                                            width = if (draftOption.emoji == emoji) 2.dp else 1.dp,
                                            color = if (draftOption.emoji == emoji) MaterialTheme.colorScheme.primary else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            options[index] = draftOption.copy(emoji = emoji)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    val nextEmoji = presetEmojis.getOrElse(options.size) { "⭐" }
                    options.add(DraftOption(text = "Option ${options.size + 1}", emoji = nextEmoji))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Option")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (canSave) {
                        val finalOptions = options.mapIndexed { idx, draft ->
                            AnswerOption(
                                id = (idx + 1).toString(),
                                text = draft.text,
                                isCorrect = idx == selectedCorrectIndex,
                                emoji = draft.emoji
                            )
                        }

                        val newQuestion = Question(
                            id = System.currentTimeMillis().toInt(),
                            text = questionText,
                            isRequired = isRequired,
                            options = finalOptions
                        )

                        onSaveQuestion(newQuestion)
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Question")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateQuestionScreenPreview() {
    SurveyTheme {
        CreateQuestionScreen(
            onSaveQuestion = {},
            onCancel = {}
        )
    }
}
