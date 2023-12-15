
package com.example.dailytaskmanagement.presentation
import FirebaseUtils
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import Task
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

class TaskCategoryPageActivity : ComponentActivity() {
    companion object {
        const val ADD_TASK_REQUEST_CODE = 123
    }
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

                            TaskCategoryPageContent(title = "Shared Tasks with me", tasksState, username, taskType)
                        }

                        "work", "gym", "reading", "self learning", "other tasks" -> {
                            var tasksState by remember { mutableStateOf<List<Task>>(emptyList()) }

                            FirebaseUtils().getTasksByTypeAndOwner(taskType.orEmpty(), username.orEmpty()) { tasks ->
                                tasksState = tasks
                            }

                            TaskCategoryPageContent(title = "Your $taskType Tasks", tasksState, username, taskType)
                        }

                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskCategoryPageContent(title: String, tasks: List<Task>, username: String?, taskType: String?) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                },
                actions = {
                    if (taskType != "shared tasks with me") {
                    IconButton(
                        onClick = {
                            val intent = Intent(
                                this@TaskCategoryPageActivity,
                                AddTaskFormActivity::class.java
                            )
                            intent.putExtra("username", username)
                            intent.putExtra("taskType", taskType)
                            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Yellow) // Set the background color to blue
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Log.d("TaskCategoryPage", "Task added successfully")


        }
    }
}
