package scala_docs.zipfile

import java.util.zip.ZipEntry

sealed trait CompressionMethod

object CompressionMethod {

  case object Stored extends CompressionMethod
  case object Deflated extends CompressionMethod

  def apply(i: Int): Option[CompressionMethod] = i match {
    case -1 => None
    case ZipEntry.STORED => Some(Stored)
    case ZipEntry.DEFLATED => Some(Deflated)
    case _ => throw new Exception("Unrecognized entry: {}".format(i))
  }
}
