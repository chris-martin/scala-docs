package scala_docs.zipfile

import java.io.InputStream

class NonClosingInputSteam(is: InputStream) extends InputStreamAdapter(is) {

  override def close(): Unit = {}

}
