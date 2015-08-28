package scala_docs

import scalaz.concurrent.Task
import scalaz.effect.{IO, SafeApp}
import scalaz.stream.{io, text, Process}

object App extends SafeApp {

  override def runc = taskIO(writeLinesToFile(
    lines = Seq("one", "two", "three"),
    file = "build/scala-docs/index.html"))

  def writeLinesToFile(lines: Seq[String], file: String): Task[Unit] =
    Process(lines: _*)
      .flatMap(Process(_, "\n"))
      .pipe(text.utf8Encode)
      .to(io.fileChunkW(file))
      .runLog[Task, Unit]
      .map[Unit](_ => ())

  def taskIO[A](task: Task[A]): IO[A] = IO(task.run)

}
