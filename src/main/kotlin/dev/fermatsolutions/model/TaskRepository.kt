package dev.fermatsolutions.model

object TaskRepository {
    private val tasks = mutableListOf(
        Task(name = "cleaning", priority = Priority.Low, description = "Clean the house"),
        Task(name = "gardening", priority = Priority.Medium, description = "Mow the lawn")
    )

    fun allTasks(): List<Task> = tasks

    fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }
}
