import junit.TestDriver;
import org.apache.maven.it.VerificationException;
import org.junit.Test;

import java.net.MalformedURLException;

public class JunitTest {
    @Test
    public void test1(){
        try {
            TestDriver driver=new TestDriver("");
        } catch (MalformedURLException | VerificationException e) {
            e.printStackTrace();
        }
    }
}
