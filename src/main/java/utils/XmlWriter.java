package utils;

import com.github.javaparser.utils.Pair;
import core.branch.BranchCoverageMethodAdapter;
import model.BasicBlock;
import model.Edge;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import storage.Storage;

import java.io.*;
import java.util.*;

public class XmlWriter {

    public static void generateXml(String path, XmlType type) throws IOException {
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        if(type== XmlType.STATEMENT_COVERAGE){generateStatementCoverageXml(p);}
        else if(type==XmlType.BRANCH_COVERAGE){generateBranchCoverageXml(p);}
        else if(type==XmlType.METHOD_COVERAGE){generateMethodCoverageXml(p);}
        else if(type==XmlType.BLOCK_COVERAGE){generateBlockCoverageXml(p);}
        else if(type==XmlType.EDGE_COVERAGE){generateEdgeCoverageXml(p);}
    }

    private static void generateStatementCoverageXml(String path) throws IOException {
        String separator="#";

        //tag
        String lineTag="line";
        String linesTag="lineList";
        String testTag="test";
        String testsTag="testList";
        String rootTag="lineCov";
        String resultTag="testResult";


        //attribute
        String classNameAttr="className";
        String methodNameAttr="methodName";
        String methodSigAttr="methodSig";
        String lineNumberAttr="lineNum";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(linesTag);
        Element element11=element0.addElement(testsTag);
        Element element111=element0.addElement(resultTag);

        //lineList
        for(String method:Storage.lines.get().keySet()){
            Set<Integer> lines=Storage.lines.get().get(method);
            lines.stream().sorted().forEach(i -> {
                Element element12=element1.addElement(lineTag);
                String[] ss=method.split(separator);
                element12.addAttribute(lineNumberAttr,String.valueOf(i));
                element12.addAttribute(classNameAttr,ss[0]);
                element12.addAttribute(methodSigAttr,ss[1]);
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
                Element element1112=element111.addElement(lineTag);
                element1112.addAttribute(classNameAttr,ss[0]);
                element1112.addAttribute(methodSigAttr,ss[1]);
                element1112.addAttribute(lineNumberAttr,String.valueOf(i));
                element1112.addAttribute(callCountAttr,String.valueOf(s.size()));
                for(String test:s){
                    Element element11123=element1112.addElement(testTag);
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
        String resultTag="testResult";


        //attribute
        String classNameAttr="className";
        String methodNameAttr="methodName";
        String methodSigAttr="methodSig";
        String branchTypeAttr="type";
        String branchLineNum="lineNum";
        String branchIdAttr="branchId";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(branchesTag);
        Element element11=element0.addElement(testsTag);
        Element element111=element0.addElement(resultTag);

        //branchList
        for(String method:Storage.branches.get().keySet()){
            List<BranchCoverageMethodAdapter.BranchStruct> branches=Storage.branches.get().get(method);
            branches.stream().sorted(Comparator.comparingInt(BranchCoverageMethodAdapter.BranchStruct::id)).forEach(bs -> {
                Element element12=element1.addElement(branchTag);
                String[] ss=method.split(separator);
                element12.addAttribute(branchIdAttr,String.valueOf(bs.id()));
                element12.addAttribute(classNameAttr,ss[0]);
                element12.addAttribute(methodSigAttr,ss[1]);
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
            Element element1112=element111.addElement(branchTag);
            element1112.addAttribute(branchIdAttr,String.valueOf(branchId));

            Set<String> tests=Storage.exec_branches2.get().get(branchId);
            element1112.addAttribute(callCountAttr,String.valueOf(tests.size()));

            for(String test:tests){
                String[] s=test.split(separator);
                Element element11123=element1112.addElement(testTag);
                element11123.addAttribute(classNameAttr,s[0]);
                element11123.addAttribute(methodSigAttr,s[1]);
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
        String resultTag="testResult";


        //attribute
        String classNameAttr="className";
        String methodSigAttr="methodSig";
        String methodNameAttr="methodName";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(methodsTag);
        Element element11=element0.addElement(testsTag);
        Element element111=element0.addElement(resultTag);

        //methodList
        for(String clazz:Storage.methods.get().keySet()){
            Set<String> methods=Storage.methods.get().get(clazz);
            methods.stream().sorted().forEach(m -> {
                Element element12=element1.addElement(methodTag);
                String[] ss=m.split(separator);
                element12.addAttribute(classNameAttr,clazz);
                element12.addAttribute(methodSigAttr,ss[0]);
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

            Element element1112=element111.addElement(methodTag);
            element1112.addAttribute(classNameAttr,ss[0]);
            element1112.addAttribute(methodSigAttr,ss[1]);
            element1112.addAttribute(callCountAttr,String.valueOf(tests.size()));
            for(String test:tests){
                Element element11123=element1112.addElement(testTag);
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

    public static void generateBlockCoverageXml(String path) throws IOException {
        String separator="#";

        String rootTag="testCov";
        String blocksTag="blockList";
        String blockTag="block";
        String testTag="test";
        String testsTag="testList";
        String resultTag="testResult";

        String classNameAttr="className";
        String methodSigAttr="methodDesc";
        String methodNameAttr="methodName";
        String headLineAttr="headLine";
        String tailLineAttr="tailLine";
        String blockIdAttr="blockId";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(blocksTag);
        Element element11=element0.addElement(testsTag);
        Element element111=element0.addElement(resultTag);

        //blockList
        for(String method:Storage.blocks.get().keySet()){
            Set<BasicBlock> blocks=Storage.blocks.get().get(method);
            String className=method.split("#")[0];
            String methodSig=method.split("#")[1];
            for(BasicBlock bb:blocks){
                int headLine=bb.headLine;
                int tailLine=bb.tailLine;
                Element element12=element1.addElement(blockTag);
                element12.addAttribute(classNameAttr,className);
                element12.addAttribute(methodSigAttr,methodSig);
                element12.addAttribute(headLineAttr,headLine>=0?String.valueOf(headLine):"unknown");
                element12.addAttribute(tailLineAttr,tailLine>=0?String.valueOf(tailLine):"unknown");
                element12.addAttribute(blockIdAttr,String.valueOf(bb.blockId));
            }
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
        for(int block: Storage.exec_blocks.get().keySet()){
            Set<String> tests=Storage.exec_blocks.get().get(block);

            Element element1112=element111.addElement(blockTag);
            element1112.addAttribute(blockIdAttr, String.valueOf(block));
            element1112.addAttribute(callCountAttr,String.valueOf(tests.size()));
            for(String test:tests){
                Element element1123=element1112.addElement(testTag);
                String[] sss=test.split(separator);
                element1123.addAttribute(classNameAttr,sss[0]);
                element1123.addAttribute(methodNameAttr,sss[1]);
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

    public static void generateEdgeCoverageXml(String path) throws IOException {
        String separator="#";

        String rootTag="testCov";
        String edgesTag="edgeList";
        String edgeTag="edge";
        String startBlockTag="startBlock";
        String endBlockTag="endBlock";
        String testTag="test";
        String testsTag="testList";
        String resultTag="testResult";

        String classNameAttr="className";
        String methodSigAttr="methodDesc";
        String methodNameAttr="methodName";
        String headLineAttr="headLine";
        String tailLineAttr="tailLine";
        String edgeIdAttr="edgeId";
        String blockIdAttr="blockId";
        String callCountAttr="count";

        Document document= DocumentHelper.createDocument();
        Element element0=document.addElement(rootTag);
        Element element1=element0.addElement(edgesTag);
        Element element11=element0.addElement(testsTag);
        Element element111=element0.addElement(resultTag);

        Map<Pair<Integer,Integer>,Integer> edgeMap=new HashMap<>();
        //edgeList
        for(String method:Storage.edges.get().keySet()){
            Set<Edge> edges=Storage.edges.get().get(method);
            String className=method.split("#")[0];
            String methodSig=method.split("#")[1];
            for(Edge edge:edges){
                Element element12=element1.addElement(edgeTag);
                element12.addAttribute(classNameAttr,className);
                element12.addAttribute(methodSigAttr,methodSig);
                element12.addAttribute(edgeIdAttr,String.valueOf(edge.edgeId));
                BasicBlock bb1=edge.start;
                BasicBlock bb2=edge.end;
                Element element123=element12.addElement(startBlockTag);
                Element element1233=element12.addElement(endBlockTag);
                element123.addAttribute(blockIdAttr, String.valueOf(bb1.blockId));
                element123.addAttribute(headLineAttr, String.valueOf(bb1.headLine==-1?"unknown":bb1.headLine));
                element123.addAttribute(tailLineAttr, String.valueOf(bb1.tailLine==-1?"unknown":bb1.tailLine));
                element1233.addAttribute(blockIdAttr, String.valueOf(bb2.blockId));
                element1233.addAttribute(headLineAttr, String.valueOf(bb2.headLine==-1?"unknown":bb2.headLine));
                element1233.addAttribute(tailLineAttr, String.valueOf(bb2.tailLine==-1?"unknown":bb2.tailLine));
                //add to edgeMap
                edgeMap.put(new Pair<>(bb1.blockId,bb2.blockId),edge.edgeId);
            }
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
        for(Pair<Integer,Integer> p: Storage.possible_exec_edges.get().keySet()){
            //not an dege
            if(!edgeMap.containsKey(p))continue;
            int edgeId=edgeMap.get(p);
            Set<String> tests=Storage.possible_exec_edges.get().get(p);

            Element element1112=element111.addElement(edgeTag);
            element1112.addAttribute(edgeIdAttr, String.valueOf(edgeId));
            element1112.addAttribute(callCountAttr,String.valueOf(tests.size()));
            for(String test:tests){
                Element element1123=element1112.addElement(testTag);
                String[] sss=test.split(separator);
                element1123.addAttribute(classNameAttr,sss[0]);
                element1123.addAttribute(methodNameAttr,sss[1]);
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
