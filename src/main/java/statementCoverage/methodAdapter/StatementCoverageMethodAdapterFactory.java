package statementCoverage.methodAdapter;


import org.objectweb.asm.MethodVisitor;

public class StatementCoverageMethodAdapterFactory {
    private StatementCoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(SCType scType, String className,String methodName, MethodVisitor after){
        if(scType==SCType.BASIC_BLOCK_RECORD){
            return new StatementCoverageByBasicBlockMethodAdapter(after,className,methodName);
        }else if(scType==SCType.BASIC_BLOCK_EXEC){
            return new StatementCoverageByBasicBlockMethodAdapter.StatementCoverageMethodAdapterExecutor(after,className,methodName);
        }else if(scType==SCType.NAIVE){
            return new NaiveStatementCoverageMethodAdapter(after,className,methodName);
        }else{
            return null;
        }
    }

}
