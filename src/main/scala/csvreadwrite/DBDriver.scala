package csvreadwrite
import org.mongodb.scala.{MongoClient, MongoCollection, Observable}
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

object DBDriver  {

    // Mongo scala boilerplate for localhost:
    // include classOf[T] for all of your classes
    val codecRegistry = fromRegistries(fromProviders(classOf[Contact]), MongoClient.DEFAULT_CODEC_REGISTRY)
    val client = MongoClient()
    val db = client.getDatabase("contactsdb").withCodecRegistry(codecRegistry)
    val collection : MongoCollection[Contact] = db.getCollection("contacts")


    // Helper functions for access and printing, to get us started + skip the observable data type
    def getResults[T](obs: Observable[T]): Seq[T] = {
        Await.result(obs.toFuture(), Duration(10, SECONDS))
    }

    def printResults[T](obs: Observable[T]): Unit = {
        getResults(obs).foreach(println(_))

    }

}
