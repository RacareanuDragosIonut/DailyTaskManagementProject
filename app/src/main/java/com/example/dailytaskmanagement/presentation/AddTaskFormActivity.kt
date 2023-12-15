package com.example.dailytaskmanagement.presentation

import FirebaseUtils
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

class AddTaskFormActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskManagementTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val username = intent.getStringExtra("username")
                    val taskType = intent.getStringExtra("taskType")

                    AddTaskForm(onSubmit = { taskName, dueDate, priority, description ->

                        addTaskToFirebase(username, taskName, dueDate, priority, description, taskType)
                        setResult(Activity.RESULT_OK)

                        finish()
                    }, onClose = {

                        val intent = Intent(this@AddTaskFormActivity, TaskCategoryPageActivity::class.java)
                        intent.putExtra("username", username)
                        intent.putExtra("taskType", taskType)
                        startActivity(intent)
                        finish()
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskForm(onSubmit: (String, String, String, String) -> Unit, onClose: () -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    onClose()
                },
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        // Task Name
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Due Date
        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Due Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Priority
        OutlinedTextField(
            value = priority,
            onValueChange = { priority = it },
            label = { Text("Priority") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {

                    onSubmit(taskName, dueDate, priority, description)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
                Text("Submit")
            }
        }
    }
}


private fun addTaskToFirebase(username: String?, taskName: String, dueDate: String, priority: String, description: String, type: String?) {
    if (username != null) {

        FirebaseUtils().addTask(
            owner = username,
            sharedUsers = emptyList(),
            status = "not started",
            name = taskName,
            dueDate = dueDate,
            priority = priority,
            description = description,
            type = type

        )
    }
}
