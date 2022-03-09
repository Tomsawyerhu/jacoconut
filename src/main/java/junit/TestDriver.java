package junit;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;

public class TestDriver {
    private Verifier v;

    public TestDriver(String project) throws MalformedURLException, VerificationException {
        this.v=new Verifier(project);
    }

    private TestDriver(){}

    public void run(String clazz, String method) {
        if(v.isAutoclean()) v.setAutoclean(false);
        try {
            v.executeGoals(Arrays.asList("surefire:test","-Dtest="+clazz+'#'+method));
        } catch (VerificationException e) {
            e.printStackTrace();
        }
    }

    public void runAllTests(){
        if(v.isAutoclean()) v.setAutoclean(false);
        try {
            v.executeGoals(Collections.singletonList("surefire:test"));
        } catch (VerificationException e) {
            e.printStackTrace();
        }
    }
}
