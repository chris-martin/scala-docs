package scala_docs.zipfile

import java.io.InputStream
import java.util.zip.{ZipEntry, ZipInputStream}

import scalaz.concurrent.Task

trait ZipType[A] {

  def fromZipEntry(entry: ZipEntry): A

  def openStream(is: InputStream): Task[ZipInputStream]

}
