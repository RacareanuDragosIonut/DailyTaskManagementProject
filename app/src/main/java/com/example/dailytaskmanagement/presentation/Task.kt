data class Task(
    val name: String = "",
    val priority: String = "",
    val description: String = "",
    val owner: String = "",
    val status: String = "",
    val type: String = "",
    val dueDate: String = "",
    val sharedUsers: List<String> = emptyList()
)
