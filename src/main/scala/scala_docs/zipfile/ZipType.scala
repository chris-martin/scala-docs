package scala_docs.zipfile

import java.io.InputStream
import java.util.zip.{ZipEntry, ZipInputStream}

trait ZipType[A] {

  def fromZipEntry(entry: ZipEntry): A

  def openStream(is: InputStream): ZipInputStream

}
