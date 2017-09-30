package controllers

import play.api.mvc.{BaseController, ControllerComponents}

class HomeController(val controllerComponents: ControllerComponents) extends BaseController {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
