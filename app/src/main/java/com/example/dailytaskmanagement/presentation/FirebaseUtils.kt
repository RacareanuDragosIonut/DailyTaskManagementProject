import com.google.firebase.database.*

class FirebaseUtils {

    private val database = FirebaseDatabase.getInstance("https://dailytaskmanagement-a3cfa-default-rtdb.europe-west1.firebasedatabase.app")
    private val tasksReference = database.getReference("tasks")

    fun getTasksBySharedUsers(username: String, onComplete: (List<Task>) -> Unit) {
        tasksReference.orderByChild("shared_users").startAt(username).endAt(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = mutableListOf<Task>()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let { tasks.add(it) }
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
            "shared_users" to sharedUsers,
            "status" to status,
            "type" to type,
            "name" to name,
            "dueDate" to dueDate,
            "priority" to priority,
            "description" to description

        )


        tasksReference.child(taskId.orEmpty()).setValue(taskData)
            .addOnSuccessListener {

                println("Task added successfully")
            }
            .addOnFailureListener { e ->

                println("Error adding task: $e")
            }
    }


}
