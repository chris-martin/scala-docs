package scala_docs

import java.io.InputStream
import java.util.zip.ZipInputStream

import scodec.bits.ByteVector

import scala_docs.stream._
import scalaz.concurrent.Task

package object zipfile {

  case class MetaAndContent[A, B](meta: A, content: B)

  type Entry[A] = MetaAndContent[A, ByteVector]

  /**
   * Reads a meta entry, or returns `None` if there are no more.
   */
  def getMeta[A](zis: ZipInputStream)
                (implicit zt: ZipType[A]): Task[Option[A]] =
    Task.delay { Option(zis.getNextEntry).map(zt.fromZipEntry) }

  /**
   * Reads all of the meta-and-content from the `File`.
   */
  def zipEntriesR[A](r: => InputStream)
                    (implicit zt: ZipType[A]): Source[Entry[A]] =
    closeableResource(zt.openStream(r)) { zis =>
      val meta = untilNone(getMeta(zis))
      val content = allBytesR(zis, close = false).repeat
      (meta zipWith content)(MetaAndContent.apply)
    }

}

