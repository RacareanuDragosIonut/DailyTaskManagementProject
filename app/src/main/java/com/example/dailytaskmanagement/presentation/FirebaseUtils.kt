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
                    // Handle error
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


}
