package Implementations.AuxiliaryDataRetrievers

import java.net.URLEncoder

import DataTypes.WmdMetric
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
 * Created by ericrw96 on 3/17/17.
 */
class Wmdistance{
	val url = "http://localhost:5000/file_model/distance"

	def getSimilarities(first: String, second: String): Double = {
		implicit val formats = DefaultFormats

		val encodedFirst = URLEncoder.encode(first, "utf-8").replaceAll("\\+", "%20");
		val encodedSecond = URLEncoder.encode(second, "utf-8").replaceAll("\\+", "%20");
		val uri = s"?document1=$encodedFirst&document2=$encodedSecond"
		//uri = uri.replaceAll("%3F", "?");
		val result = scala.io.Source.fromURL(url+uri).mkString
		val wmdMetric = parse(result).extract[WmdMetric]
		wmdMetric.wmdDistance
	}
}
