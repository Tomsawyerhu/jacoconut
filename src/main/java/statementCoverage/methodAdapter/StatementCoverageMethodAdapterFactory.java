package statementCoverage.methodAdapter;

import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;

public class StatementCoverageMethodAdapterFactory {
    private StatementCoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(SCType scType,String name,MethodVisitor after){
        if(scType==SCType.BASIC_BLOCK_RECORD){
            return new StatementCoverageByBasicBlockMethodAdapter(after,name);
        }else if(scType==SCType.BASIC_BLOCK_EXEC){
            return new StatementCoverageByBasicBlockMethodAdapter.StatementCoverageMethodAdapterExecutor(after,name);
        }else if(scType==SCType.NAIVE){
            return new NaiveStatementCoverageMethodAdapter(after,name);
        }else{
            return null;
        }
    }

}
