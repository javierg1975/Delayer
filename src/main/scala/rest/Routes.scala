package rest

import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.StatusCodes._
import spray.http.MediaTypes._
import spray.json._
import spray.httpx.SprayJsonSupport
import scala.concurrent.{Await, Future}
import concurrent.ExecutionContext.Implicits.global


trait Debugger {
  import org.slf4j._

  val log = LoggerFactory.getLogger("Debugger")
}

import akka.actor.{Props, Actor}
import akka.pattern.ask
import spray.routing.HttpService
import spray.routing.directives.CachingDirectives
import spray.http._




class DatalizeServiceActor extends Actor with DatalizeService{
  def actorRefFactory = context
  def receive = runRoute(departuresRoute)
}

trait DatalizeService extends HttpService  {

  implicit val timeout = Timeout(20 seconds)

  val departuresRoute = {
    pathPrefix(""){
      getFromDirectory("src/main/webapp/")
    }~
      path(""){
      respondWithMediaType(`text/html`){
        getFromFile("src/main/webapp/index.html")
      }
    }~
    pathPrefix("api"){
      delayer("")

    }/*~
      pathPrefix("scripts"){
        getFromDirectory("target/scala-2.9.2/resource_managed/main/js/")
      }~
      pathPrefix("stylesheets"){
        getFromDirectory("target/scala-2.9.2/resource_managed/main/webapp/css/")
      }*/
  }



  private def delayer(user: String) = {

    pathPrefix("delay"){
      get{
        path(""){
          val f = Future{Thread.sleep(30000); """{"name":"Delayer", "waitedFor":"30 Seconds"}"""}
          val result = Await.result(f, 30 seconds).asInstanceOf[String]
          complete(result)
        }
      }
    }
  }

}
