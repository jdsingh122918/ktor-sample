package dev.fermatsolutions.plugins

import dev.fermatsolutions.model.Priority
import dev.fermatsolutions.model.Task
import dev.fermatsolutions.model.TaskRepository
import dev.fermatsolutions.model.tasksAsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    routing {
        staticResources("/content", "myContent")

        get("/") {
            call.respondText("Hello World!")
        }

        get("/test1") {
            val text = "<h1>Hello from Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }

        get("/tasks") {
            val tasks = TaskRepository.allTasks()
            call.respond(tasks)
        }

        get("/tasks/byPriority/{priority}") {
            val priorityAsText = call.parameters["priority"]
            if (priorityAsText == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val priority = Priority.valueOf(priorityAsText)
                val tasks = TaskRepository.tasksByPriority(priority)

                if (tasks.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = tasks.tasksAsTable()
                )
            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/tasks") {
            try {
                val task = call.receive<Task>()
                TaskRepository.addTask(task)
                call.respond(HttpStatusCode.NoContent)
            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete("/tasks/{taskName}") {
            val taskName = call.parameters["taskName"]
            if (taskName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            if (TaskRepository.deleteTask(taskName)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
