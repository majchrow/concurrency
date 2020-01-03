package webcrawler

import java.net.URL

import org.htmlcleaner.HtmlCleaner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object WebCrawler extends App {

  def parseUrl(url: String, depth: Int): Future[Any] = {
    try {
      val src = scala.io.Source.fromURL(url)
      val out = new java.io.FileWriter("htmls/" + url.replaceAll("/", "") + ".html") // remove "/" from url before saving
      out.write(src.mkString)
      out.close()
    } catch {
      case e: java.io.IOException => Future.failed(e)
    }
    if (depth == 0) {
      Future.successful()
    } else {
      try {
        val cleaner = new HtmlCleaner
        val rootNode = cleaner.clean(new URL(url))
        val elements = rootNode.getElementsByName("a", true)
        val futures = elements.map(elem => {
          val hrefs = elem.getAttributeByName("href")
          val future = parseUrl(hrefs.toString, depth - 1) recover {case _ => ()}
          future
        }).toSeq
        Future.sequence(futures) recover {case _ => ()}
      } catch {
        case e: Throwable => Future.failed(e)
      }
    }
  }

  val parsed = parseUrl("http://google.com/", 2) recover {case _ => ()} // Recovers to avoid exceptions
  Await.result(parsed, Duration.Inf)
  println("Success")

}

