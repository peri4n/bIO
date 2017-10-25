package controllers

import java.io.File

import components.MyComponents
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api
import play.api.{ApplicationLoader, Environment, Mode}
import play.api.test.{FakeRequest, Writeables}
import play.api.test.Helpers.{GET, route}

class LuceneControllerTest extends PlaySpec with OneAppPerSuiteWithComponents with Writeables {

  "A LuceneController" must {
    "validates requests" in {
      route(app, FakeRequest(GET, "/index/search"))
      components.luceneController.add
    }
  }

  override def components = new MyComponents(context)

}
