package Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 3/8/17.
 */
trait IDistinguishingWordsGenerator extends java.io.Serializable {
  def generateMostDistinguishingWords(tweets: RDD[Tweet]): Array[String]
}
