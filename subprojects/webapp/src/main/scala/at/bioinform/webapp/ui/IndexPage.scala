package at.bioinform.webapp.ui

import scalatags.Text.all._

object IndexPage {

  def apply() =
    html(
      head(
        script("some script")),
      body(
        h1("This is my title"),
        div(
          p("This is my first paragraph"),
          p("This is my second paragraph")
        )
      )
    ).toString()

}
