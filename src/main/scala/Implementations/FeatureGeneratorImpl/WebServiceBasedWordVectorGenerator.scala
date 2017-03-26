package Implementations.FeatureGeneratorImpl

import java.net.URLEncoder

import DataTypes.WordVector
import main.DataTypes.Tweet
import main.Interfaces.DataType.DataType
import main.Interfaces.IFeatureGenerator
import main.scala.Implementations.AuxiliaryDataBasedExperiment
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
 * Created by saur6410 on 3/11/17.
 */
class WebServiceBasedWordVectorGenerator extends IFeatureGenerator{



	override def generateFeatures(tweets: RDD[Tweet], dataType: DataType): RDD[LabeledPoint] = {

		val missingWordVectorProcessor = new MissingWordFeatureProcessor
		val processedTweets =  tweets.map(tweet => missingWordVectorProcessor.replaceMissingWords(tweet))
		processedTweets.map(tweet => this.getWordVectorForSentence(tweet.tweetText, tweet.label))

	}

	def getWordVectorForSentence(word:String, label:Double):LabeledPoint =	{

		var sentence = word
		if(sentence == null || sentence.length == 0 || sentence.trim.length == 0)
			{
				sentence = "UNKNOWN_TOKEN"
				//new LabeledPoint(label, Vectors.dense(0))
			}
		implicit val formats = DefaultFormats
		val sentence1 = URLEncoder.encode(sentence, "utf-8").replaceAll("\\+", "%20");
		val url = AuxiliaryDataBasedExperiment.webWord2VecBaseUri + s"$sentence1"
		try {
			val result = scala.io.Source.fromURL(url).mkString
			val wv = parse(result).extract[WordVector]
			new LabeledPoint(label, Vectors.dense(wv.vector))
		}
		catch {

			case _ => {
				println("Some issues with retrieving word vectors.")
				return new LabeledPoint(label, Vectors.dense(0))
			}

		}
	}

	override def generateFeature(tweet: Tweet): LabeledPoint = {
		this.getWordVectorForSentence(tweet.tweetText, tweet.label)
	}
}
