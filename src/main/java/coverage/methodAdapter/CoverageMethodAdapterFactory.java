package coverage.methodAdapter;


import org.objectweb.asm.MethodVisitor;

public class CoverageMethodAdapterFactory {
    private CoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(SCType scType, String className,String methodName, MethodVisitor after){
        if(scType==SCType.BASIC_BLOCK_RECORD){
            return new BasicBlockRecoderMethodAdapter(after,className,methodName);
        }else if(scType==SCType.STATEMENT_BASIC_BLOCK){
            return new BasicBlockRecoderMethodAdapter.StatementCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.STATEMENT_NAIVE){
            return new NaiveStatementCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.BRANCH){
            return new BranchCoverageMethodAdapter(after,className,methodName);
        }else if(scType==SCType.BASIC_BLOCK_CFG){
            return new PathCoverageMethodAdapter.CfgMethodAdapter(after,className,methodName);
        }else{
            return null;
        }
    }

}
