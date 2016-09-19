package akka.http.scaladsl.util

import akka.http.scaladsl.model.Multipart
import akka.http.impl.util.StringRendering
import org.scalatest.{ FlatSpec, Matchers }
import com.typesafe.config.ConfigFactory

class SwedishArmyKnifeSpec extends FlatSpec with Matchers {
  val config = ConfigFactory.parseString(
    """|swiss-army-knife-test.content-type-header.modify-regex {
       |  match = "^(.*;)( boundary=[^;]*;)( charset=.*)$"
       |  replace = "$1$3;$2"
       |}""".stripMargin)

  "SwedishArmyKnife.transformContentType" should "rewrite a multipart/form data with charset=utf-8" in {
    val multi = Multipart.FormData(Multipart.FormData.BodyPart.Strict("hi", "sup?"))
    val entity = multi.toEntity()
    val knife = SwedishArmyKnife.fromConfig(config.getConfig("swiss-army-knife-test"))
    val rendering = new StringRendering
    val transformed = knife.transformContentType(rendering, entity.contentType).get
    transformed should fullyMatch regex """(?sm)Content-Type: multipart/form-data; charset=UTF-8; boundary=[^;]*;.."""
  }
}
