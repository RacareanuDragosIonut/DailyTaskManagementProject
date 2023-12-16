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
import java.text.SimpleDateFormat

import java.util.Locale

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

    @Composable
    fun TaskCategoryPageContent(
        title: String,
        tasks: List<Task>,
        username: String?,
        taskType: String?
    ) {
        var isAscendingOrder by remember { mutableStateOf(true) }
        var sortByPriority by remember { mutableStateOf(false) }
        var sortByDueDate by remember { mutableStateOf(false) }
        var sortedTasks = tasks;
        if(sortByPriority) {
             sortedTasks = tasks.sortedWith(compareBy<Task> {
                when (it.priority) {
                    "High" -> 3
                    "Medium" -> 2
                    "Low" -> 1
                    else -> 0
                }
            }.let {
                if (isAscendingOrder) it else it.reversed()
            })
        }
        else{
            if(sortByDueDate){
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sortedTasks = tasks.sortedBy {
                    dateFormat.parse(it.dueDate)
                }.let {
                    if (isAscendingOrder) it else it.reversed()
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow)
            ) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (taskType != "shared tasks with me") {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
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
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task"
                            )
                        }
                    }
                }


                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sort Tasks by Priority",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    )
                    Checkbox(
                        checked = sortByPriority,
                        onCheckedChange = {
                            if (it) {

                                sortByPriority = it
                                sortByDueDate = false
                            } else {

                                sortByPriority = it
                                sortByDueDate = it
                            }
                            isAscendingOrder = sortByPriority || sortByDueDate
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }


                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sort Tasks by Due Date",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    )
                    Checkbox(
                        checked = sortByDueDate,
                        onCheckedChange = {
                            if (it) {

                                sortByDueDate = it
                                sortByPriority = false
                            } else {

                                sortByDueDate = it
                                sortByPriority = it
                            }
                            isAscendingOrder = sortByPriority || sortByDueDate
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }


                if (sortByPriority || sortByDueDate) {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sort Order",
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
                }
            }


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
        var selectedUsers by remember { mutableStateOf<List<String>>(emptyList()) }
        var showUnshareDialog by remember { mutableStateOf(false) }

        if (showUnshareDialog) {
            AlertDialog(
                onDismissRequest = {
                    showUnshareDialog = false
                    selectedUsers = emptyList()
                },
                title = { Text("Unshare Task") },
                text = {
                    Column {
                        Text("Select users to unshare from:")
                        for (user in task.sharedUsers.orEmpty()) {
                            if (user != "") {
                                Checkbox(
                                    checked = selectedUsers.contains(user),
                                    onCheckedChange = {
                                        selectedUsers = if (it) {
                                            selectedUsers + user
                                        } else {
                                            selectedUsers - user
                                        }
                                    },
                                    modifier = Modifier.padding(4.dp)
                                )
                                Text(text = user)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            task.sharedUsers = task.sharedUsers?.filterNot { it in selectedUsers }
                            FirebaseUtils().updateTask(task)
                            showUnshareDialog = false
                            refreshTasks.invoke()
                        }
                    ) {
                        Text("Unshare")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showUnshareDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

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

        if (showShareDialog) {
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
                    if (taskType != "shared tasks with me") {
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
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { showUnshareDialog = true },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(text = "Unshare")
                            }
                            Button(
                                onClick = { expanded = false },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = "Close")
                            }
                        }
                    }
                }
            }
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
