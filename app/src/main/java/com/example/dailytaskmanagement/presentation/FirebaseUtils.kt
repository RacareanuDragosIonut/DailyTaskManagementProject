import android.util.Log
import com.google.firebase.database.*

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
        description: String
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
            "description" to description

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

            val updatedTaskData = mapOf(
                "name" to updatedTask.name,
                "dueDate" to updatedTask.dueDate,
                "priority" to updatedTask.priority,
                "description" to updatedTask.description,
                "sharedUsers" to updatedTask.sharedUsers

            )


            tasksReference.child(taskId).updateChildren(updatedTaskData)
                .addOnSuccessListener {

                    println("Task updated successfully")
                }
                .addOnFailureListener { e ->

                    println("Error updating task: $e")
                }
        } else {

            println("Invalid task ID for update")
        }
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



}
