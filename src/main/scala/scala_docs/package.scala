import scalaz.concurrent.Task
import scalaz.stream.{io, text, Process}

package object scala_docs {

  def writeLinesToFile(lines: Seq[String], file: String): Task[Unit] =
    Process(lines: _*)
      .flatMap(Process(_, "\n"))
      .pipe(text.utf8Encode)
      .to(io.fileChunkW(file))
      .runLog[Task, Unit]
      .map[Unit](_ => ())

}
