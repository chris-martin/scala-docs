package scala_docs.zipfile

import java.io.InputStream
import java.security.CodeSigner
import java.security.cert.Certificate
import java.util.jar.{JarInputStream, Attributes, JarEntry}
import java.util.zip.{ZipInputStream, ZipEntry}

import scalaz.concurrent.Task

case class JarMeta(
  zipEntry: ZipMeta,
  manifestAttributes: Option[Attributes],
  certificates: Option[Seq[Certificate]],
  codeSigner: Option[Seq[CodeSigner]]
)

object JarMeta {

  def fromJarEntry(x: JarEntry): JarMeta = JarMeta(
    zipEntry = ZipMeta.fromZipEntry(x),
    manifestAttributes = Option(x.getAttributes),
    certificates = Option(x.getCertificates),
    codeSigner = Option(x.getCodeSigners)
  )

  implicit object zipTypeInstance extends ZipType[JarMeta] {

    override def fromZipEntry(entry: ZipEntry): JarMeta =
      fromJarEntry(entry.asInstanceOf[JarEntry])

    override def openStream(is: InputStream): Task[ZipInputStream] =
      Task.delay { new JarInputStream(is) }

  }

}
