package com.example.dailytaskmanagement.presentation

import FirebaseUtils
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

class AnalyticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val username = intent.getStringExtra("username")
            var completedBeforeDueDateCount by remember{mutableStateOf(0)}
            var completedAfterDueDateCount by remember{mutableStateOf(0)}
            var inProgressCount by remember {mutableStateOf(0)}
            var notStartedCount by remember {mutableStateOf(0)}
            var totalCount by remember {mutableStateOf(0)}
            FirebaseUtils().getTaskCountsForCurrentMonthAnalytics(username.orEmpty()) { counts ->
                    completedBeforeDueDateCount = counts[0]
                    completedAfterDueDateCount = counts[1]
                    inProgressCount = counts[2]
                    notStartedCount = counts[3]
                    totalCount = counts[4]
            }
            DailyTaskManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    AnalyticsContent(completedBeforeDueDateCount, completedAfterDueDateCount, inProgressCount, notStartedCount, totalCount)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsContent(completedBeforeDueDateCount: Int, completedAfterDueDateCount: Int, inProgress: Int, notStarted: Int,
                             totalCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tasks To Be Done For This Month",
            style = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        AnalyticsLabel("Completed Before Due Date: $completedBeforeDueDateCount(${String.format("%.2f",completedBeforeDueDateCount.toFloat()/totalCount.toFloat() * 100.0)} %)")
        Spacer(modifier = Modifier.height(8.dp))
        AnalyticsLabel("Completed After Due Date: $completedAfterDueDateCount(${String.format("%.2f", completedAfterDueDateCount.toFloat()/totalCount.toFloat() * 100.0)} %)")
        Spacer(modifier = Modifier.height(8.dp))
        AnalyticsLabel("In Progress: $inProgress(${String.format("%.2f", inProgress.toFloat()/totalCount.toFloat() * 100.0)} %)")
        Spacer(modifier = Modifier.height(8.dp))
        AnalyticsLabel("Not Started: $notStarted(${String.format("%.2f", notStarted.toFloat()/totalCount.toFloat() * 100.0)} %)")
    }
}

@Composable
private fun AnalyticsLabel(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = text,
            color = Color.Black
        )
    }
}


