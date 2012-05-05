import org.junit.Test
import org.junit.Assert

class Spec{

  @Test
  def void testHello(){
    val obj = new Hello()
    Assert::assertEquals(obj.hello,"hello")
  }

}

