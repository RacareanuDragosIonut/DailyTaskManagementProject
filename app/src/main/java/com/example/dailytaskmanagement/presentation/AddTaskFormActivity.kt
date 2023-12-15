package com.example.dailytaskmanagement.presentation

import FirebaseUtils
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme

import java.util.Calendar
import java.util.Date


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
    var priority by remember { mutableStateOf("Low") }
    var description by remember { mutableStateOf("") }
    val priorityOptions = listOf("Low", "Medium", "High")
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


        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        )


        showDatePicker(
            context=LocalContext.current,
            selectedDate = dueDate,
            onDateSelected = { selectedDate ->
                dueDate = selectedDate
            }
        )



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        ) {
            var expanded by remember { mutableStateOf(false) }
            var selectedIndex by remember { mutableStateOf(0) }

            Text(
                text = "Priority: ${priorityOptions[selectedIndex]}",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                priorityOptions.forEachIndexed { index, priorityOption ->
                    DropdownMenuItem(text={Text(text = priorityOption)},onClick = {
                        selectedIndex = index
                        priority = priorityOption
                        expanded = false
                  })

                }

            }
        }
        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        )


        Spacer(modifier = Modifier.weight(1f))

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

@Composable
fun showDatePicker(context: Context, selectedDate: String, onDateSelected: (String) -> Unit) {
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date = remember { mutableStateOf("") }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.value = "$dayOfMonth/$month/$year"
            onDateSelected(date.value)
        }, year, month, day
    )
    date.value = selectedDate
    Text(text = "Selected Date: ${date.value}")
    Spacer(modifier = Modifier.size(16.dp))
    Button(onClick = {
        datePickerDialog.show()
    }) {
        Text(text = "Open Date Picker")
    }
}


private fun addTaskToFirebase(username: String?, taskName: String, dueDate: String, priority: String, description: String, type: String?) {
    if (username != null) {
        FirebaseUtils().addTask(
            owner = username,
            sharedUsers = listOf(""),
            status = "not started",
            name = taskName,
            dueDate = dueDate,
            priority = priority,
            description = description,
            type = type

        )
    }
}
