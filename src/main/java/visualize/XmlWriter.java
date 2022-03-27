package visualize;


import coverage.methodAdapter.BranchCoverageMethodAdapter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import storage.Storage;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlWriter {
    public enum XmlType {
        STATEMENT_COVERAGE,
        BRANCH_COVERAGE,
        METHOD_COVERAGE

    }

    public static void generateXml(String path, XmlType type) throws IOException {
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        if(type== XmlType.STATEMENT_COVERAGE){generateStatementCoverageXml(p);}
        else if(type==XmlType.BRANCH_COVERAGE){generateBranchCoverageXml(p);}
        else if(type==XmlType.METHOD_COVERAGE){generateMethodCoverageXml(p);}
    }

    private static void generateStatementCoverageXml(String path) throws IOException {
        String separator="#";

        //tag
        String lineTag="line";
        String linesTag="lineList";
        String testTag="test";
        String testsTag="testList";
        String rootTag="lineCov";


        //attribute
        String classNameAttr="className";
        String methodDescAttr="methodDesc";
        String methodNameAttr="methodName";
        String lineNumberAttr="lineNum";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(linesTag);
        Element element11=element0.addElement(testsTag);

        //lineList
        for(String method:Storage.lines.get().keySet()){
            Set<Integer> lines=Storage.lines.get().get(method);
            lines.stream().sorted().forEach(i -> {
                Element element12=element1.addElement(lineTag);
                String[] ss=method.split(separator);
                element12.addAttribute(lineNumberAttr,String.valueOf(i));
                element12.addAttribute(classNameAttr,ss[0]);
                element12.addAttribute(methodNameAttr,ss[1]);
                element12.addAttribute(methodDescAttr,ss[2]);
            });
        }
        //testList
        for(String testClass:Storage.tests.get().keySet()){
            for(String testMethod:Storage.tests.get().get(testClass)){
                Element element112=element11.addElement(testTag);
                element112.addAttribute(classNameAttr,testClass);
                element112.addAttribute(methodNameAttr,testMethod);
            }
        }
        //coverage
        for(String method: Storage.exec_lines2.get().keySet()){
            String[] ss=method.split(separator);
            Map<Integer, Set<String>> m=Storage.exec_lines2.get().get(method);
            for(Integer i:m.keySet()){
                Set<String> s=m.get(i);
                Element element112=element11.addElement(lineTag);
                element112.addAttribute(classNameAttr,ss[0]);
                element112.addAttribute(methodNameAttr,ss[1]);
                element112.addAttribute(methodDescAttr,ss[2]);
                element112.addAttribute(lineNumberAttr,String.valueOf(i));
                element112.addAttribute(callCountAttr,String.valueOf(s.size()));
                for(String test:s){
                    Element element11123=element112.addElement(testTag);
                    String[] sss=test.split(separator);
                    element11123.addAttribute(classNameAttr,sss[0]);
                    element11123.addAttribute(methodNameAttr,sss[1]);
                }
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File(path);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();
    }

    private static void generateBranchCoverageXml(String path) throws IOException {
        String separator="#";

        //tag
        String branchTag="branch";
        String branchesTag="branchList";
        String testTag="test";
        String testsTag="testList";
        String rootTag="branchCov";


        //attribute
        String classNameAttr="className";
        String methodDescAttr="methodDesc";
        String methodNameAttr="methodName";
        String branchTypeAttr="type";
        String branchLineNum="lineNum";
        String branchIdAttr="branchId";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(branchesTag);
        Element element11=element0.addElement(testsTag);

        //branchList
        for(String method:Storage.branches.get().keySet()){
            List<BranchCoverageMethodAdapter.BranchStruct> branches=Storage.branches.get().get(method);
            branches.stream().sorted(Comparator.comparingInt(BranchCoverageMethodAdapter.BranchStruct::id)).forEach(bs -> {
                Element element12=element1.addElement(branchTag);
                String[] ss=method.split(separator);
                element12.addAttribute(branchIdAttr,String.valueOf(bs.id()));
                element12.addAttribute(classNameAttr,ss[0]);
                element12.addAttribute(methodNameAttr,ss[1]);
                element12.addAttribute(methodDescAttr,ss[2]);
                element12.addAttribute(branchLineNum,String.valueOf(bs.lineNum()));
                element12.addAttribute(branchTypeAttr,bs.type());
            });
        }
        //testList
        for(String testClass:Storage.tests.get().keySet()){
            for(String testMethod:Storage.tests.get().get(testClass)){
                Element element112=element11.addElement(testTag);
                element112.addAttribute(classNameAttr,testClass);
                element112.addAttribute(methodNameAttr,testMethod);
            }
        }

        //coverage
        for(int branchId: Storage.exec_branches2.get().keySet()){
            Element element111=element0.addElement(branchTag);
            element111.addAttribute(branchIdAttr,String.valueOf(branchId));

            Set<String> tests=Storage.exec_branches2.get().get(branchId);
            element111.addAttribute(callCountAttr,String.valueOf(tests.size()));

            for(String test:tests){
                String[] s=test.split(separator);
                Element element1112=element111.addElement(testTag);
                element1112.addAttribute(classNameAttr,s[0]);
                element1112.addAttribute(methodNameAttr,s[1]);
                element1112.addAttribute(methodDescAttr,s[2]);
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File(path);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();
    }


    private static void generateMethodCoverageXml(String path) throws IOException {
        String separator="#";

        //tag
        String methodTag="method";
        String methodsTag="methodList";
        String testTag="test";
        String testsTag="testList";
        String rootTag="testCov";


        //attribute
        String classNameAttr="className";
        String methodDescAttr="methodDesc";
        String methodNameAttr="methodName";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(methodsTag);
        Element element11=element0.addElement(testsTag);

        //methodList
        for(String clazz:Storage.methods.get().keySet()){
            Set<String> methods=Storage.methods.get().get(clazz);
            methods.stream().sorted().forEach(m -> {
                Element element12=element1.addElement(methodTag);
                String[] ss=m.split(separator);
                element12.addAttribute(classNameAttr,clazz);
                element12.addAttribute(methodNameAttr,ss[0]);
                element12.addAttribute(methodDescAttr,ss[1]);
            });
        }
        //testList
        for(String testClass:Storage.tests.get().keySet()){
            for(String testMethod:Storage.tests.get().get(testClass)){
                Element element112=element11.addElement(testTag);
                element112.addAttribute(classNameAttr,testClass);
                element112.addAttribute(methodNameAttr,testMethod);
            }
        }
        //coverage
        for(String method: Storage.exec_methods2.get().keySet()){
            String[] ss=method.split(separator);
            Set<String> tests=Storage.exec_methods2.get().get(method);

            Element element112=element11.addElement(methodTag);
            element112.addAttribute(classNameAttr,ss[0]);
            element112.addAttribute(methodNameAttr,ss[1]);
            element112.addAttribute(methodDescAttr,ss[2]);
            element112.addAttribute(callCountAttr,String.valueOf(tests.size()));
            for(String test:tests){
                Element element11123=element112.addElement(testTag);
                String[] sss=test.split(separator);
                element11123.addAttribute(classNameAttr,sss[0]);
                element11123.addAttribute(methodNameAttr,sss[1]);
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file = new File(path);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();
    }

}
