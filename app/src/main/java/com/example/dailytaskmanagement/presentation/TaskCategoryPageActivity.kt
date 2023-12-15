package com.example.dailytaskmanagement.presentation
import FirebaseUtils
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import Task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.items

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

class TaskCategoryPageActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskManagementTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    val taskType = intent.getStringExtra("taskType")
                    val username = intent.getStringExtra("username")


                    when (taskType) {
                        "shared tasks with me" -> {
                            var tasksState by remember { mutableStateOf<List<Task>>(emptyList()) }

                            FirebaseUtils().getTasksBySharedUsers(username.orEmpty()) { tasks ->
                                tasksState = tasks
                            }

                            TaskCategoryPageContent(title = "Shared Tasks with me", tasksState)
                        }

                        "work", "gym", "reading", "self learning", "other tasks" -> {
                            var tasksState by remember { mutableStateOf<List<Task>>(emptyList()) }

                            FirebaseUtils().getTasksByTypeAndOwner(taskType.orEmpty(), username.orEmpty()) { tasks ->
                                tasksState = tasks
                            }

                            TaskCategoryPageContent(title = "Your $taskType Tasks", tasksState)
                        }

                    }
                }
            }
        }
    }


    @Composable
    fun TaskCategoryPageContent(title: String, tasks: List<Task>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task = task)
                }
            }
        }
    }

    @Composable
    fun TaskItem(task: Task) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Green,
                contentColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Task Name: ${task.name}", fontWeight = FontWeight.Bold)
                Text(text = "Priority: ${task.priority}")
            }
        }
    }

}
