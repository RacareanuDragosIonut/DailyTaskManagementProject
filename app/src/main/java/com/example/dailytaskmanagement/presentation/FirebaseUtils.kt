import android.util.Log
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseUtils {

    private val database = FirebaseDatabase.getInstance("https://dailytaskmanagement-a3cfa-default-rtdb.europe-west1.firebasedatabase.app")
    private val tasksReference = database.getReference("tasks")

    fun getTasksSharedWithUser(username: String, onComplete: (List<Task>) -> Unit) {
        tasksReference.orderByChild("owner").startAt("").endAt(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = mutableListOf<Task>()

                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let {
                            if (it.sharedUsers is List<*> && username in (it.sharedUsers as List<*>)) {
                                tasks.add(it)
                            }
                        }
                    }

                    onComplete(tasks)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }




    fun getTasksByTypeAndOwner(type: String, owner: String, onComplete: (List<Task>) -> Unit) {
        tasksReference.orderByChild("type").equalTo(type)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = mutableListOf<Task>()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task?.owner == owner) {
                            task?.let { tasks.add(it) }
                        }
                    }
                    onComplete(tasks)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun addTask(
        owner: String,
        sharedUsers: List<String>,
        status: String,
        type: String?,
        name: String,
        dueDate: String,
        priority: String,
        description: String,
        completedDate: String
    ) {

        val taskId = tasksReference.push().key


        val taskData = hashMapOf(
            "taskId" to taskId,
            "owner" to owner,
            "sharedUsers" to sharedUsers,
            "status" to status,
            "type" to type,
            "name" to name,
            "dueDate" to dueDate,
            "priority" to priority,
            "description" to description,
            "completedDate" to completedDate
        )
        Log.d("FirebaseUtils", "Task Data: $taskData")

        tasksReference.child(taskId.orEmpty()).setValue(taskData)
            .addOnSuccessListener {

                println("Task added successfully")
            }
            .addOnFailureListener { e ->

                println("Error adding task: $e")
            }
    }

    fun updateTask(updatedTask: Task?) {
        val taskId = updatedTask?.taskId
        if (taskId != null) {

            tasksReference.child(taskId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val originalStatus = snapshot.child("status").getValue(String::class.java)

                    val updatedTaskData = mutableMapOf(
                        "name" to updatedTask.name,
                        "dueDate" to updatedTask.dueDate,
                        "priority" to updatedTask.priority,
                        "status" to updatedTask.status,
                        "description" to updatedTask.description,
                        "sharedUsers" to updatedTask.sharedUsers,
                        "completedDate" to updatedTask.completedDate
                    )

                    if (updatedTask.status == "completed" && originalStatus != "completed") {
                        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val formattedDate = formatDatabaseDate(currentDate)
                        updatedTaskData["completedDate"] = formattedDate
                    }

                    if(updatedTask.status != "completed" && originalStatus == "completed"){
                        updatedTaskData["completedDate"] = ""
                    }

                    tasksReference.child(taskId).updateChildren(updatedTaskData)
                        .addOnSuccessListener {
                            println("Task updated successfully")
                        }
                        .addOnFailureListener { e ->
                            println("Error updating task: $e")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error retrieving original task status: $error")
                }
            })
        } else {
            println("Invalid task ID for update")
        }
    }

    fun formatDatabaseDate(dateString: String): String {
        val parts = dateString.split("/")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull()?.toString() ?: parts[0]
            val month = parts[1].toIntOrNull()?.toString() ?: parts[1]
            val year = parts[2]
            return "$day/$month/$year"
        }
        return dateString
    }

    fun deleteTask(task: Task?, onComplete: (Boolean) -> Unit) {
        tasksReference.child(task?.taskId.orEmpty())
            .removeValue()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun getTaskCountsForCurrentMonthAnalytics(username: String, onComplete: (List<Int>) -> Unit) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentMonth = currentDate.split("/")[1].toIntOrNull() ?: 1

        tasksReference.orderByChild("owner").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val completedBeforeDueDate = mutableListOf<Task>()
                    val completedAfterDueDate = mutableListOf<Task>()
                    val inProgress = mutableListOf<Task>()
                    val notStarted = mutableListOf<Task>()

                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let {


                            val dueDateParts = it.dueDate?.split("/") ?: emptyList()

                            val completedDateParts = it.completedDate?.split("/") ?: emptyList()

                            val dueDateMonth = dueDateParts[1].toIntOrNull() ?: 1
                            val completedDateDay = completedDateParts[0].toIntOrNull() ?: 1

                            when {
                                it.status == "completed" && completedDateDay <= dueDateParts[0].toIntOrNull()!! && currentMonth == dueDateMonth -> {
                                    completedBeforeDueDate.add(it)
                                }
                                it.status == "completed" && completedDateDay > dueDateParts[0].toIntOrNull()!! && currentMonth == dueDateMonth -> {
                                    completedAfterDueDate.add(it)
                                }
                                it.status == "in progress" && currentMonth == dueDateMonth-> {
                                    inProgress.add(it)
                                }
                                it.status == "not started" && currentMonth == dueDateMonth-> {
                                    notStarted.add(it)
                                }

                                else -> {}
                            }


                        }
                    }

                    val totalTasks = completedBeforeDueDate.size + completedAfterDueDate.size + inProgress.size + notStarted.size
                    val counts = listOf(
                        completedBeforeDueDate.size,
                        completedAfterDueDate.size,
                        inProgress.size,
                        notStarted.size,
                        totalTasks
                    )

                    onComplete(counts)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }






}
