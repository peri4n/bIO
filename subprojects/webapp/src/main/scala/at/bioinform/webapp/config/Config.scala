package at.bioinform.webapp.config

import at.bioinform.webapp.Env
import cats.data.Reader
import org.slf4j.LoggerFactory

object Config {

  val Logger = LoggerFactory.getLogger(Config.getClass.getName)

  def printConfiguration(): Reader[Env, Unit] = Reader { env =>
    Logger.info("Database settings:")
    Logger.info("  Database url: {}", env.config.getString("database.test.url"))

    Logger.info("Sequence indexing options:")
    Logger.info("  Kmer size for DNA indexing is set to: {}", env.config.getInt("bio.indexing.dna.kmer-size"))
    Logger.info("  Kmer overlap for DNA indexing is set to: {}", env.config.getInt("bio.indexing.dna.kmer-overlap"))
  }

}
