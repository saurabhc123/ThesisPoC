package main.DataTypes

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint


/**
  * This class will serve as the main way that data is transferred between each stage
  * Created by Eric on 2/1/2017.
  */
case class Tweet(identifier: String, tweetText:String,
                 label: Option[Double])
{

	def getWordVectors(text: String): Vector = ???

	def transformToLabeledPoint() : LabeledPoint =
	{
		new LabeledPoint(this.label.get, getWordVectors(this.tweetText))
	}

}
