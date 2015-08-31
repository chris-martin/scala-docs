package scala_docs.zipfile

import java.io.InputStream
import java.nio.file.attribute.FileTime
import java.util.zip.{ZipInputStream, ZipEntry}

import scodec.bits.ByteVector

/**
 * Times are in milliseconds since the epoch. Sizes are in number of bytes.
 */
case class ZipMeta(
  name: String,
  time: Option[Long],
  lastModifiedTime: Option[FileTime],
  lastAccessTime: Option[FileTime],
  creationTime: Option[FileTime],
  uncompressedSize: Option[Long],
  compressedSize: Option[Long],
  crc32checksum: Option[Long],
  method: Option[CompressionMethod],
  extra: Option[ByteVector],
  comment: Option[String]
)

object ZipMeta {

  def longOption(x: Long): Option[Long] =
    if (x == -1) None else Some(x)

  def fromZipEntry(x: ZipEntry): ZipMeta = ZipMeta(
    name = x.getName,
    time = longOption(x.getTime),
    lastModifiedTime = Option(x.getLastModifiedTime),
    lastAccessTime = Option(x.getLastAccessTime),
    creationTime = Option(x.getCreationTime),
    uncompressedSize = longOption(x.getSize),
    compressedSize = longOption(x.getCompressedSize),
    crc32checksum = longOption(x.getCrc),
    method = CompressionMethod(x.getMethod),
    extra = Option(x.getExtra).map(ByteVector(_)),
    comment = Option(x.getComment)
  )

  implicit object zipTypeInstance extends ZipType[ZipMeta] {

    override def fromZipEntry(entry: ZipEntry): ZipMeta =
      ZipMeta.fromZipEntry(entry)

    override def openStream(is: InputStream): ZipInputStream =
      new ZipInputStream(is)
  }

}
