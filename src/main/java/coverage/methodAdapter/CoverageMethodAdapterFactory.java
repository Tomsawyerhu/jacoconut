package coverage.methodAdapter;


import org.objectweb.asm.MethodVisitor;
import storage.Property;

public class CoverageMethodAdapterFactory {
    private CoverageMethodAdapterFactory() {}

    public static MethodVisitor getMethodVisitor(String className,String methodName, MethodVisitor after){
        if(Property.LINE_COV){
            return new NaiveStatementCoverageMethodAdapter(after,className,methodName);
        }else if(Property.BRANCH_COV){
            return new BranchCoverageMethodAdapter(after,className,methodName);}
//        }else if(scType==SCType.CFG){
//            return new CfgMethodAdapter(after,className,methodName);
//        }else if(scType==SCType.METHOD_STSRT_END){
//            return new CfgMethodAdapter.StartEndMethodAdapter(after,className,methodName);
//        }else if(scType==SCType.PATH){
//            return new PathCoverageMethodAdapter(after,className,methodName);
//        }
         else {
            return null;
        }
    }

}
