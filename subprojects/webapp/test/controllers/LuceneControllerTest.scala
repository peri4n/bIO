package controllers

import akka.stream.Materializer
import akka.util.ByteString
import components.MyComponents
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Writeables}

class LuceneControllerTest extends PlaySpec with OneAppPerSuiteWithComponents with Writeables {

  implicit lazy val materializer: Materializer = app.materializer

  "A LuceneController" must {
    "validates requests" in {
      val result = components.luceneController.add(FakeRequest(POST, "/index/add").withRawBody(ByteString("irsn")))
      val bodyText: String = contentAsString(result)
      println(bodyText)
    }
  }

  override def components = new MyComponents(context)

}
