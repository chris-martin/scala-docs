package scala_docs

import java.io.FileInputStream
import java.nio.file.Paths

import scala.util.Try
import scala_docs.zipfile.ZipMeta
import scalaz.concurrent.Task
import scalaz.effect.{IO, SafeApp}

object App extends SafeApp {

  override def runc = taskIO(foo)

  def taskIO[A](task: Task[A]): IO[A] = IO(task.run)

  val taskFile = "/home/chris/downloads/concurrent/Task.class"

  val jarFile = "/home/chris/.gradle/caches/modules-2/files-2.1/org.scalaz/scalaz-concurrent_2.11/7.1.1/ed2f0389b2ce3f2b6a26d7b655308a8762f354a2/scalaz-concurrent_2.11-7.1.1.jar"

  def jarInputStream = new FileInputStream(Paths.get(jarFile).toFile)

  val foo: Task[Unit] =
    zipfile.entriesResourceR[ZipMeta](jarInputStream)
      .filter(_.meta.name.endsWith(".class"))
      .evalMap[Task, Unit](x => {
        Task.delay(println(
          x.meta.name + "\n" + Try(scala.tools.scalap.Main.decompileScala(x.content.toArray, false)).toOption.getOrElse("ERROR") + "\n--------------"
        ))
      })
      .run

}
