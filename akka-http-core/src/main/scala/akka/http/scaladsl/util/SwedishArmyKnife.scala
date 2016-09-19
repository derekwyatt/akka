package akka.http.scaladsl.util

import akka.http.scaladsl.model.ContentType
import akka.http.impl.util.Rendering
import akka.http.impl.util.Rendering.CrLf
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.HttpHeader
import akka.util.ByteString
import akka.http.impl.util.StringRendering
import com.typesafe.config.Config

trait SwedishArmyKnife {
  def transformContentType[R <: Rendering](r: R, header: ContentType): r.type
  def transformContentType[R <: Rendering](r: R, bytes: Array[Byte]): r.type
  def transformHeader[R <: Rendering](r: R, header: HttpHeader): r.type
}

object SwedishArmyKnife {
  type SwissRendering <: Rendering
  def fromConfig(config: Config): SwedishArmyKnife = {
    val matcher = config.getString("content-type-header.modify-regex.match")
    val replacer = config.getString("content-type-header.modify-regex.replace")

    new SwedishArmyKnife {
      def transformContentType[R <: Rendering](r: R, header: ContentType): r.type = {
        val sr = new StringRendering
        val headerString = (sr ~~ headers.`Content-Type` ~~ header ~~ CrLf).get.replaceFirst(matcher, replacer)
        r ~~ headerString
      }
      def transformContentType[R <: Rendering](r: R, bytes: Array[Byte]): r.type = {
        val headerString = new String(bytes).replaceFirst(matcher, replacer)
        r ~~ headerString
      }
      def transformHeader[R <: Rendering](r: R, header: HttpHeader): r.type = r ~~ header ~~ CrLf
    }
  }

  def nil: SwedishArmyKnife = Nil

  object Nil extends SwedishArmyKnife {
    def transformContentType[R <: Rendering](r: R, header: ContentType): r.type = r ~~ headers.`Content-Type` ~~ header ~~ CrLf
    def transformContentType[R <: Rendering](r: R, bytes: Array[Byte]): r.type = r ~~ bytes
    def transformHeader[R <: Rendering](r: R, header: HttpHeader): r.type = r ~~ header ~~ CrLf
  }
}
