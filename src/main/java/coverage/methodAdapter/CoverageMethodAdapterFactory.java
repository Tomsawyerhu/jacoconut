package coverage.methodAdapter;


import org.objectweb.asm.MethodVisitor;

public class CoverageMethodAdapterFactory {
    private CoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(SCType scType, String className,String methodName, MethodVisitor after){
        if(scType==SCType.STATEMENT_NAIVE){
            return new NaiveStatementCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.BRANCH){
            return new BranchCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.CFG){
            return new CfgMethodAdapter(after,className,methodName);
        }else if(scType==SCType.METHOD_STSRT_END){
            return new CfgMethodAdapter.StartEndMethodAdapter(after,className,methodName);
        }else if(scType==SCType.PATH){
            return new PathCoverageMethodAdapter(after,className,methodName);
        } else {
            return null;
        }
    }

}
