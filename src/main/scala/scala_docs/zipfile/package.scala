package scala_docs

import java.io.InputStream
import java.util.zip.ZipInputStream

import scodec.bits.ByteVector

import scalaz.concurrent.Task
import scalaz.stream._

package object zipfile {

  case class MetaAndContent[A, B](meta: A, content: B)

  type Entry[A] = MetaAndContent[A, ByteVector]

  type Source[A] = Process[Task, A]

  /**
   * Reads the entire stream, producing a single `ByteVector`.
   */
  def allBytesR(is: InputStream): Source[ByteVector] =
    io.chunkR(new NonClosingInputSteam(is))
      .evalMap(_(4096))
      .reduce(_ ++ _)
      .lastOr(ByteVector.empty)

  def getMeta[A : ZipType](zis: ZipInputStream): Task[Option[A]] =
    Task.delay(Option(zis.getNextEntry).map(implicitly[ZipType[A]].fromZipEntry))
  
  /**
   * Reads all of the meta entries from the zip stream.
   */
  def zipMetaR[A : ZipType](zis: ZipInputStream): Source[A] =
    Process.eval(getMeta(zis)).repeat.takeWhile(_.isDefined).map(_.get)

  /**
   * Reads all of the meta-and-content from the zip stream.
   */
  def entriesR[A : ZipType](zis: ZipInputStream): Source[Entry[A]] =
    for { meta    <- zipMetaR(zis)
          content <- allBytesR(zis) }
    yield MetaAndContent(meta, content)

  def resource[A, B](open: Task[A], close: A => Task[Unit],
                     process: A => Source[B]): Source[B] =
    Process.await(open)(a => process(a).onComplete(Process.eval_(close(a))))

  /**
   * Reads all of the meta-and-content from the `File`.
   */
  def entriesResourceR[A : ZipType](r: => InputStream): Source[Entry[A]] =
    resource[ZipInputStream, Entry[A]](
      open = Task(implicitly[ZipType[A]].openStream(r)),
      close = is => Task(is.close()),
      process = entriesR
    )

}
