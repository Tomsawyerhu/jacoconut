package core;


import core.method.MethodCoverageMethodAdapter;
import core.branch.BranchCoverageMethodAdapter;
import core.line.NaiveStatementCoverageMethodAdapter;
import core.test.TestEndMethodAdapter;
import org.objectweb.asm.MethodVisitor;

public class CoverageMethodAdapterFactory {
    private CoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(SCType scType, String className, String methodName, MethodVisitor after){
        if(scType==SCType.STATEMENT_NAIVE){
            return new NaiveStatementCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.BRANCH){
            return new BranchCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.METHOD){
            return new MethodCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.TEST_END){
            return new TestEndMethodAdapter(after,className,methodName);
        }else {
            return null;
        }
    }

}
