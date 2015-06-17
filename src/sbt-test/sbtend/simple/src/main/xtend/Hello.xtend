import org.apache.http.HttpHost

class Hello{
  def hello() {
    "hello"
  }

  def static void main(String[] args) {
    println(new HttpHost("github.com"))
    println("Hello World")
  }
}
