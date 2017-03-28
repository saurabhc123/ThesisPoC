import Implementations.AuxiliaryDataRetrievers.FileBasedAuxiliaryDataRetriever
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.Word2Vec

/**
 * Created by saur6410 on 3/27/17.
 */
object word2VecLocalExp extends App {

	override def main(args: Array[String]): Unit = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)
		Logger.getLogger("logreg").setLevel(Level.OFF)

		val filename = "data/final/1k_results_lem.txt"
		FileBasedAuxiliaryDataRetriever._auxiliaryFileName = filename
		val tweets = FileBasedAuxiliaryDataRetriever.readTweetsFromAuxiliaryFile()
		val tweetSentences = tweets.map(t => t.tweetText.split(" ").toIterable)



		//Load up the word vectors from the different files
		println("Start Training Word2Vec --->")
		val word2vecModel = new Word2Vec().fit(tweetSentences)

		println("Finished Training")
		println(word2vecModel.findSynonyms("obesity",10).deep.mkString(" "))
		println(word2vecModel.findSynonyms("disease",10).deep.mkString(" "))
		println(word2vecModel.findSynonyms("fukushima",10).deep.mkString(" "))
		println(word2vecModel.findSynonyms("japan", 10).deep.mkString(" "))

		//Figure out what you expect as similarities.

		//Compare with the similarities in python. Find out where they are so different.

	}

}
