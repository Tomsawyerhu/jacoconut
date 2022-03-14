package visualize;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import storage.Storage;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class XmlWriter {
    public enum XmlType {
        STATEMENT_COVERAGE

    }

    public static void generateXml(String path, XmlType type) throws IOException {
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        if(type== XmlType.STATEMENT_COVERAGE){generateStatementCoverageXml(p);}
    }

    private static void generateStatementCoverageXml(String path) throws IOException {
        String separator="#";

        //tag
        String methodTag="method";
        String lineTag="line";
        String linesTag="lineList";
        String testTag="test";
        String testsTag="testList";
        String rootTag="lineCov";


        //attribute
        String classNameAttr="className";
        String descAttr="desc";
        String methodNameAttr="name";
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
                element12.addAttribute(descAttr,ss[2]);
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
            Element element111=element0.addElement(methodTag);
            element111.addAttribute(classNameAttr,ss[0]);
            element111.addAttribute(methodNameAttr,ss[1]);
            element111.addAttribute(descAttr,ss[2]);

            Map<Integer, Set<String>> m=Storage.exec_lines2.get().get(method);
            for(Integer i:m.keySet()){
                Set<String> s=m.get(i);
                Element element2=element111.addElement(lineTag);
                element2.addAttribute(lineNumberAttr,String.valueOf(i));
                element2.addAttribute(callCountAttr,String.valueOf(s.size()));
                for(String test:s){
                    Element element3=element2.addElement(testTag);
                    String[] sss=test.split(separator);
                    element3.addAttribute(classNameAttr,sss[0]);
                    element3.addAttribute(methodNameAttr,sss[1]);
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

}
