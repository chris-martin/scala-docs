package scala_docs.stream

import java.io.InputStream

class NonClosingInputSteam(is: InputStream) extends InputStreamAdapter(is) {

  override def close(): Unit = {}

}

object NonClosingInputSteam {

  def nonClosing(is: InputStream): NonClosingInputSteam =
    new NonClosingInputSteam(is)

}
