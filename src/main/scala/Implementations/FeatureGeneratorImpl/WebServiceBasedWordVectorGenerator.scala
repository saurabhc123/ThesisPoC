package Implementations.FeatureGeneratorImpl

import java.net.URLEncoder

import DataTypes.WordVector
import main.DataTypes.Tweet
import main.Interfaces.DataType.DataType
import main.Interfaces.IFeatureGenerator
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

	def getWordVectorForSentence(sentence:String, label:Double):LabeledPoint =	{

		implicit val formats = DefaultFormats
		val sentence1 = URLEncoder.encode(sentence, "utf-8").replaceAll("\\+", "%20");
		val url = s"http://localhost:5000/getvector/$sentence1"
		val result = scala.io.Source.fromURL(url).mkString
		val wv = parse(result).extract[WordVector]
		new LabeledPoint(label, Vectors.dense(wv.vector))
	}

	override def generateFeature(tweet: Tweet): LabeledPoint = {
		this.getWordVectorForSentence(tweet.tweetText, tweet.label)
	}
}
