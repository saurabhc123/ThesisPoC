package main.scala
object WebServiceClient {


  def main(args: Array[String]): Unit = {
    val url = "http://localhost:5000/getvector/hello"
    val result = scala.io.Source.fromURL(url).mkString
    println(result)

  }

}
