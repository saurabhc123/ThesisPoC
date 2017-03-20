package Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/19/17.
 */
trait IMissingWordFeatureProcessor  extends java.io.Serializable{

	def replaceMissingWords(tweets:RDD[Tweet]): RDD[Tweet]
	def replaceMissingWords(tweet:Tweet): Tweet

}
