package dev.fermatsolutions

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testNewEndpoint() = testApplication {
        application {
            module()
        }
        val response = client.get("/test1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
    }

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/byPriority/Medium")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun unusedPriorityReturns404() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/byPriority/High")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun newTaskCanBeAdded() = testApplication {
        application {
            module()
        }

        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(
                listOf(
                    "name" to "Test Task",
                    "description" to "This is a test task",
                    "priority" to "Low"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.NoContent, response.status)

        val tasksListResponse = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, tasksListResponse.status)
        val tasksListBody = tasksListResponse.bodyAsText()

        assertContains(tasksListBody, "Test Task")
    }
}