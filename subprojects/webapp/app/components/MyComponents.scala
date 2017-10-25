package components

import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.filters.gzip.GzipFilter
import router.Routes

class MyComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with controllers.AssetsComponents {

  lazy val luceneController = new controllers.LuceneController(controllerComponents)

  lazy val homeController = new controllers.HomeController(controllerComponents)

  override lazy val router = new Routes(httpErrorHandler, homeController, luceneController, assets)

  val gzipFilter = new GzipFilter(shouldGzip =
    (_, response) => {
      val contentType = response.header.headers.get("Content-Type")
      contentType.exists(_.startsWith("text/html"))
    })

  override lazy val httpFilters = Seq(gzipFilter)
}