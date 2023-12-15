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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytaskmanagement.R
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
                                FirebaseUtils().getTasksSharedWithUser(username.orEmpty()) { tasks ->
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

        var selectedTask by remember { mutableStateOf<Task?>(null) }
        var isAscendingOrder by remember { mutableStateOf(true) }

        val sortedTasks = tasks.sortedWith(compareBy<Task> {
            when (it.priority) {
                "High" -> 3
                "Medium" -> 2
                "Low" -> 1
                else -> 0
            }
        }.let {
            if (isAscendingOrder) it else it.reversed()
        })

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
                    Row(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sort Tasks by Priority",
                                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                            IconButton(
                                onClick = {
                                    isAscendingOrder = !isAscendingOrder
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isAscendingOrder) R.drawable.baseline_arrow_upward_24 else R.drawable.baseline_arrow_downward_24),
                                    contentDescription = if (isAscendingOrder) "Sort Ascending" else "Sort Descending"
                                )
                            }
                        }
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
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Yellow)
            )


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedTasks) { task ->
                    TaskItem(
                        task = task, taskType = taskType
                    )
                }
            }
        }
    }









    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskItem(task: Task, taskType: String?) {
        var showDeleteDialog by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        var showShareDialog by remember { mutableStateOf(false) }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
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
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        if(showShareDialog){
            var sharedUserInput by remember { mutableStateOf("") }
            var sharedUsers by remember { mutableStateOf(task.sharedUsers ?: emptyList()) }

            AlertDialog(
                onDismissRequest = { refreshTasks.invoke() },
                title = { Text("Share Task") },
                text = {
                    Column {
                        Text("Share this task with other users:")
                        TextField(
                            value = sharedUserInput,
                            onValueChange = { sharedUserInput = it },
                            label = { Text("Username") }
                        )
                        if (sharedUsers != listOf("")) {
                            Text("Shared with: ${sharedUsers.filter { it != "" }.joinToString(", ")}")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sharedUsers = sharedUsers + sharedUserInput
                            sharedUserInput = ""
                            task.sharedUsers = sharedUsers
                            FirebaseUtils().updateTask(task)
                            showShareDialog = false
                        }
                    ) {
                        Text("Share Task With User")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showShareDialog = false
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
                    if(taskType != "shared tasks with me"){
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
                                showDeleteDialog = true
                            }) {
                                Text(text = "Delete")
                            }
                            Button(onClick = {
                                showShareDialog = true
                            }) {
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
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShareTaskPage(
        task: Task,
        onShare: (List<String>) -> Unit,
        onClose: () -> Unit
    ) {
        var sharedUserInput by remember { mutableStateOf("") }
        var sharedUsers by remember { mutableStateOf(task.sharedUsers ?: emptyList()) }

        AlertDialog(
            onDismissRequest = { onClose() },
            title = { Text("Share Task") },
            text = {
                Column {
                    Text("Share this task with other users:")
                    TextField(
                        value = sharedUserInput,
                        onValueChange = { sharedUserInput = it },
                        label = { Text("Username") }
                    )
                    if (sharedUsers.isNotEmpty()) {
                        Text("Shared with: ${sharedUsers.joinToString(", ")}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sharedUsers = sharedUsers + sharedUserInput
                        sharedUserInput = ""
                    }
                ) {
                    Text("Add User")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onClose()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("TaskCategoryPage", "Task added/updated successfully")
            refreshTasks.invoke()
        }
    }
}
