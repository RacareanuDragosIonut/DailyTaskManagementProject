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
import androidx.compose.foundation.layout.Row
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


    private lateinit var refreshTasks: () -> Unit

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


                            refreshTasks = {
                                FirebaseUtils().getTasksBySharedUsers(username.orEmpty()) { tasks ->
                                    tasksState = tasks
                                }
                            }


                            refreshTasks.invoke()

                            TaskCategoryPageContent(
                                title = "Shared Tasks with me",
                                tasksState,
                                username,
                                taskType
                            )
                        }

                        "work", "gym", "reading", "self learning", "other tasks" -> {
                            var tasksState by remember { mutableStateOf<List<Task>>(emptyList()) }


                            refreshTasks = {
                                FirebaseUtils().getTasksByTypeAndOwner(taskType.orEmpty(), username.orEmpty()) { tasks ->
                                    tasksState = tasks
                                }
                            }


                            refreshTasks.invoke()

                            TaskCategoryPageContent(
                                title = "Your $taskType Tasks",
                                tasksState,
                                username,
                                taskType
                            )
                        }

                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskCategoryPageContent(
        title: String,
        tasks: List<Task>,
        username: String?,
        taskType: String?
    ) {


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
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Yellow)
            )



                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(task = task) {


                        }
                    }
                }

        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskItem(task: Task, onClick: () -> Unit) {
        var showDeleteDialog = remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        if (showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = {

                    showDeleteDialog.value = false
                },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete the task ${task?.name}?") },
                confirmButton = {
                    Button(
                        onClick = {

                            FirebaseUtils().deleteTask(task) { success ->
                                if (success) {

                                    Log.d("TaskCategoryPage", "Task deleted successfully")
                                    refreshTasks.invoke()
                                } else {

                                    Log.e("TaskCategoryPage", "Failed to delete task")
                                }
                            }


                            showDeleteDialog.value = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {

                            showDeleteDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            ),
            onClick = {
                expanded = !expanded
                onClick()
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Task Name: ${task.name}", fontWeight = FontWeight.Bold)
                Text(text = "Priority: ${task.priority}")

                if (expanded) {
                    Text(text = "Due Date: ${task.dueDate}")
                    Text(text = "Status: ${task.status}")
                    Text(text = "Description: ${task.description}")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Button(onClick = {
                            val intent = Intent(
                            this@TaskCategoryPageActivity,
                            EditTaskFormActivity::class.java
                        )
                            intent.putExtra("task", task)
                            intent.putExtra("username", task.owner)
                            intent.putExtra("taskType", task.type)
                            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
                        }) {
                            Text(text = "Edit")
                        }
                        Button(onClick = {
                            showDeleteDialog.value = true
                        }
                        )
                        {
                            Text(text = "Delete")
                        }
                        Button(onClick = {  }) {
                            Text(text = "Share")
                        }


                        Button(onClick = { expanded = false }) {
                            Text(text = "Close")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun showDeleteTaskDialog(task: Task?) {
        val showDialog = remember { mutableStateOf(true) }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog
                    showDialog.value = false
                },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete the task ${task?.name}?") },
                confirmButton = {
                    Button(
                        onClick = {

                            FirebaseUtils().deleteTask(task) { success ->
                                if (success) {

                                    Log.d("TaskCategoryPage", "Task deleted successfully")
                                } else {

                                    Log.e("TaskCategoryPage", "Failed to delete task")
                                }
                            }


                            showDialog.value = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {

                            showDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Log.d("TaskCategoryPage", "Task added/updated successfully")


            refreshTasks.invoke()
        }
    }
}
