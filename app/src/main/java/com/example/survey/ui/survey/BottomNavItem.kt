package com.example.survey.ui.survey

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Surveys", Icons.Default.Home)
    object Create : BottomNavItem("create", "Create", Icons.Default.Add)
    object History : BottomNavItem("history", "Completed", Icons.Default.CheckCircle)
}
