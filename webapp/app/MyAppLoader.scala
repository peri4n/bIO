import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.gzip.GzipFilter
import router.Routes

class MyAppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach(_.configure(context.environment))
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context)
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
