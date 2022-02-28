package junit;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;

import java.net.MalformedURLException;
import java.util.Arrays;

public class TestDriver {
    private Verifier v;

    public TestDriver(String project) throws MalformedURLException, VerificationException {
        this.v=new Verifier(project);
    }

    private TestDriver(){}

    public void run(String clazz, String method) throws VerificationException {
        if(v.isAutoclean()) v.setAutoclean(false);
        v.executeGoals(Arrays.asList("surefire:test","-Dtest="+clazz+'#'+method));
    }
}
