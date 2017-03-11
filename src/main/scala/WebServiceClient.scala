package main.scala


import java.net.URLEncoder

import DataTypes.WordVector
import Utilities.CosineSimilarity
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._


object WebServiceClient {



  def main(args: Array[String]): Unit = {

	  implicit val formats = DefaultFormats
	  val firstSentence = URLEncoder.encode("Hello World", "utf-8").replaceAll("\\+", "%20");
	  val secondSentence = URLEncoder.encode("Hello World There", "utf-8").replaceAll("\\+", "%20");
	  val firstUrl = s"http://localhost:5000/getvector/$firstSentence"
	  val secondUrl = s"http://localhost:5000/getvector/$secondSentence"
	  //val formattedUrl = String.format(url,sentence)
      var result = scala.io.Source.fromURL(firstUrl).mkString
	  val firstVector = parse(result).extract[WordVector]
	  result = scala.io.Source.fromURL(secondUrl).mkString
	  val secondVector = parse(result).extract[WordVector]

	  val cosineSimilarity = CosineSimilarity.cosineSimilarity(firstVector.vector, secondVector.vector)
	  //val p = result.toVector
	  val vector = new LabeledPoint(0.0, Vectors.dense(firstVector.vector))
	  println(cosineSimilarity)

  }

}
