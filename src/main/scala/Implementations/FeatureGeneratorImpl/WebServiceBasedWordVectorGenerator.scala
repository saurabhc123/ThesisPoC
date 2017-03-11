package Implementations.FeatureGeneratorImpl

import java.net.URLEncoder

import DataTypes.WordVector
import org.apache.spark.mllib.linalg.Vectors
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

import main.DataTypes.Tweet
import main.Interfaces.DataType.DataType
import main.Interfaces.IFeatureGenerator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/11/17.
 */
class WebServiceBasedWordVectorGenerator extends IFeatureGenerator{
	val url = "http://localhost:5000/getvector/{0}"

	override def generateFeatures(tweets: RDD[Tweet], dataType: DataType): RDD[LabeledPoint] = {

		tweets.map(tweet => this.getWordVectorForSentence(tweet.tweetText))

	}

	def getWordVectorForSentence(sentence:String):LabeledPoint =	{

		implicit val formats = DefaultFormats
		val sentence = URLEncoder.encode("Hello World", "utf-8").replaceAll("\\+", "%20");
		val url = s"http://localhost:5000/getvector/$sentence"
		val result = scala.io.Source.fromURL(url).mkString
		val wv = parse(result).extract[WordVector]
		new LabeledPoint(0.0, Vectors.dense(wv.vector))
	}
}
