package controllers.jumpstart.day2

import com.greenfossil.data.mapping.Mapping
import com.greenfossil.jumpstart.day2.TaskList
import com.greenfossil.data.mapping.Mapping.*
import com.linecorp.armeria.server.annotation.*
import com.greenfossil.thorium.{*, given}
import com.linecorp.armeria.common.sse.ServerSentEvent
import com.linecorp.armeria.server.streaming.ServerSentEvents
import reactor.core.publisher.{Flux, Sinks}
import views.jumpstart.day2.IndexPage

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime}

object HomeController:

  private val addTaskMapping: Mapping[String] = nonEmptyText.name("task")

  @Get("/")
  def index =
    val tasks = TaskList.findTasks(_ => true)
    Ok(IndexPage(tasks, addTaskMapping).render)

  @Post("/task/create")
  def createTask = Action{ implicit request =>
    val tasks = TaskList.findTasks(_ => true)
    addTaskMapping.bindFromRequest().fold(
      errorForm => BadRequest(IndexPage(tasks, errorForm).render),
      newTask =>
        TaskList.addTasks(newTask)
        Redirect(HomeController.index)
    )
  }

  /**
   * Implement controller action that deletes the task with ID, and redirect to the index page.
   * Expectation: The matching task should no longer be in the list
   */
  def deleteTask(taskId: Long) = ???

  /**
   * Implement controller action that completes the task with ID, and redirect to the index page.
   * Expectation: The matching task should be marked as completed
   */
  def completeTask(taskId: Long) = ???

  @Get("/api/getTime")
  def apiGetTime = Action { request =>
    request.requestContext.clearRequestTimeout()
    ServerSentEvents.fromPublisher(
      Flux.interval(Duration.ofSeconds(1))
        .map:seq =>
          val now = LocalDateTime.now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
          ServerSentEvent.ofData(now)
    )
  }

