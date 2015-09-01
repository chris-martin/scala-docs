package scala_docs

import java.io.{Closeable, InputStream}

import scodec.bits.ByteVector

import scala_docs.stream.NonClosingInputSteam.nonClosing
import scalaz.concurrent.Task
import scalaz.stream.{Process, io}

/**
 * Generic stream utilities.
 */
package object stream {

  type Source[A] = Process[Task, A]

  def resource[A, B](open: Task[A], close: A => Task[Unit])
                    (process: A => Source[B]): Source[B] =
    Process.await(open)(a => process(a).onComplete(Process.eval_(close(a))))

  def closeCloseable(x: Closeable): Task[Unit] = Task(x.close())

  def closeableResource[A <: Closeable, B]
      (open: Task[A])(process: A => Source[B]): Source[B] =
    resource(open, closeCloseable)(process)

  val chunkSize = 4096

  /**
   * Reads the entire stream, produces a single `ByteVector`, and closes
   * the stream.
   */
  def allBytesR(is: InputStream, close: Boolean = true): Source[ByteVector] =
    io.chunkR(if (close) is else nonClosing(is))
      .evalMap(_(chunkSize)).reduce(_ ++ _).lastOr(ByteVector.empty)

  def untilNone[A](t: Task[Option[A]]): Source[A] =
    Process.eval(t).repeat.takeWhile(_.isDefined).map(_.get)

}
