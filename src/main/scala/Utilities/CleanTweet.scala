package Utilities

import java.io.{OutputStream, PrintStream}
import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations.{LemmaAnnotation, SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.util.CoreMap
import main.DataTypes.Tweet
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer
import scala.tools.nsc.interpreter.session.JIterator

//import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
/**
  * Created by Eric on 10/14/2016.
  * Will store the lemmatization a stopword removal code from the raw data
  */
object CleanTweet {


  def clean(tweets: RDD[Tweet],sc : SparkContext): RDD[Tweet] = {
    val tweetsRDD = tweets

    val values = tweetsRDD.map(tweet => tweet.tweetText)
    tweetsRDD.zip(getCleanedTweets(values,sc)).map(f = va => {
      val tw = va._1
      tw.copy(tweetText = va._2)
    })
  }
  def createNLPPipeline(): StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    new StanfordCoreNLP(props)
  }

  def plainTextToLemmas(text: String, stopWords: Set[String], pipeline: StanfordCoreNLP)
  : Seq[String] = {
    val t = text.replaceAll("[#]", "")
    val doc = new Annotation(t)
    pipeline.annotate(doc)
    val lemmas = new ArrayBuffer[String]()
    var s = new ListBuffer[CoreMap]()
    def addIt(x: CoreMap) : Unit = {
      s += x
    }
    def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
      override def hasNext = it.hasNext
      override def next() = it.next()
    }
    scalaIterator(doc.get(classOf[SentencesAnnotation]).iterator()).foreach(addIt)
    for (sentence <- s){
      val t = new ListBuffer[CoreLabel]() ;
      for (tw <- scalaIterator(sentence.get(classOf[TokensAnnotation]).iterator())){
        t += tw
      }
      for (token <- t){
        val lemma = token.get(classOf[LemmaAnnotation])
        if (lemma.length > 2 && !stopWords.contains(lemma) && isOnlyLetters(lemma)) {
          lemmas += lemma.toLowerCase
        }
      }
    }
    lemmas
  }

  def isOnlyLetters(str: String): Boolean = {
    // While loop for high performance
    var i = 0
    while (i < str.length) {
      if (!Character.isLetter(str.charAt(i))) {
        return false
      }
      i += 1
    }
    true
  }
  def getCleanedTweets(tweets: RDD[String], sc: SparkContext) : RDD[String] = {

    val stopWords = sc.broadcast(
                              scala.io.Source.fromFile("data/stopwords.txt").getLines().toSet).value
    val cleaned_text = tweets.mapPartitions(it => {
      val oldStderr = System.err
      System.setErr(new PrintStream(new OutputStream() {
        override def write(i: Int): Unit = {}
      }))
      val pipeline = createNLPPipeline()
      System.setErr(oldStderr)
      it.map(t => plainTextToLemmas(t,stopWords,pipeline).mkString(" "))
    })
    cleaned_text
  }
}

