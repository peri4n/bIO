package controllers

import javax.inject.Inject

import play.api.mvc.{BaseController, ControllerComponents}

class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}
