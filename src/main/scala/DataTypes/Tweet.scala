package main.DataTypes


/**
  * This class will serve as the main way that data is transferred between each stage
  * Created by Eric on 2/1/2017.
  */
case class Tweet(identifier: String, tweetText:String,
                 var label: Double)
{

	/*def getFeatures(text: String, featureGenerator: IFeatureGenerator): Vector = {
		featureGenerator.generateFeatures()
	}

	def transformToLabeledPoint() : LabeledPoint =
	{
		new LabeledPoint(this.label.get, getFeatures(this.tweetText))
	}*/

}
