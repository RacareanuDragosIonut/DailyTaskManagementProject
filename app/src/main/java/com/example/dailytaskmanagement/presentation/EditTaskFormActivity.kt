package com.example.dailytaskmanagement.presentation

import FirebaseUtils
import Task
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
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanagement.ui.theme.DailyTaskManagementTheme
import java.util.*

class EditTaskFormActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskManagementTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    val task = intent.getParcelableExtra<Task>("task")
                    val username = intent.getStringExtra("username")
                    val taskType = intent.getStringExtra("taskType")


                    EditTaskForm(
                        task = task,
                        username = username,
                        taskType = taskType,
                        onUpdate = { task ->

                            updateTaskInFirebase(task)
                            setResult(Activity.RESULT_OK)

                            finish()
                        },
                        onClose = {

                            val intent = Intent(
                                this@EditTaskFormActivity,
                                TaskCategoryPageActivity::class.java
                            )
                            intent.putExtra("username", username)
                            intent.putExtra("taskType", taskType)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }


    private fun updateTaskInFirebase(task: Task?) {

        FirebaseUtils().updateTask(task)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskForm(
    task: Task?,
    username: String?,
    taskType: String?,
    onUpdate: (Task) -> Unit,
    onClose: () -> Unit
) {

    val priorityOptions = listOf("Low", "Medium", "High")

    var taskName by remember { mutableStateOf(task?.name.orEmpty()) }
    var dueDate by remember { mutableStateOf(task?.dueDate.orEmpty()) }
    var priority by remember {
        mutableStateOf(
            task?.priority
                ?.takeIf { priority -> priorityOptions.contains(priority) }
                ?: priorityOptions.first()
        )
    }
    var description by remember { mutableStateOf(task?.description.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        )


        showDatePickerEdit(
            context = LocalContext.current,
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
            var selectedIndex by remember {
                mutableStateOf(
                    priorityOptions.indexOf(priority)
                        .takeIf { it != -1 }
                        ?: 0
                )
            }

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
                    DropdownMenuItem(
                        text = { Text(text = priorityOption) },
                        onClick = {
                            selectedIndex = index
                            priority = priorityOption
                            expanded = false
                        }
                    )
                }
            }
        }


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

                    val updatedTask = Task(
                        taskId = task?.taskId,
                        name = taskName,
                        dueDate = dueDate,
                        priority = priority,
                        description = description,
                        owner = username.orEmpty(),
                        type = taskType.orEmpty(),

                    )

                    onUpdate(updatedTask)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Update")
            }


            Button(
                onClick = {

                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Close")
            }
        }
    }
}


@Composable
fun showDatePickerEdit(context: Context, selectedDate: String, onDateSelected: (String) -> Unit) {
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
