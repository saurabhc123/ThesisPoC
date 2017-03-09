package main.Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 2/3/2017.
  */
trait IAuxiliaryDataRetriever extends java.io.Serializable {
  def retrieveAuxiliaryData(distinguishingWords:Array[String]): RDD[Tweet]
}
