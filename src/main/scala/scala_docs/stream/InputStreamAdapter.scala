package scala_docs.stream

import java.io.InputStream

class InputStreamAdapter(is: InputStream) extends InputStream {

  override def close(): Unit = is.close()

  override def available(): Int = is.available()

  override def mark(readlimit: Int): Unit = is.mark(readlimit)

  override def skip(n: Long): Long = is.skip(n)

  override def markSupported(): Boolean = is.markSupported()

  override def read(): Int = is.read()

  override def read(b: Array[Byte]): Int = is.read(b)

  override def read(b: Array[Byte], off: Int, len: Int): Int =
    is.read(b, off, len)

  override def reset(): Unit = is.reset()

}
