package main.scala


import java.net.URLEncoder

import DataTypes.WordVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._


object WebServiceClient {



  def main(args: Array[String]): Unit = {

	  implicit val formats = DefaultFormats
	  val sentence = URLEncoder.encode("Hello World", "utf-8").replaceAll("\\+", "%20");
	  val url = s"http://localhost:5000/getvector/$sentence"
	  //val formattedUrl = String.format(url,sentence)
      val result = scala.io.Source.fromURL(url).mkString
	  val p = parse(result).extract[WordVector]
	  //val p = result.toVector
	  val vector = new LabeledPoint(0.0, Vectors.dense(p.vector))
	  println(result)

  }

}
