package utils;

import com.github.javaparser.utils.Pair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XmlReader {
    private static Logger logger= Logger.getLogger(XmlReader.class);

    public static double calculateCoverage(String path, XmlType type){
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(p);
            List<Element> l1=doc.getRootElement().elements("testResult").get(0).elements();
            String name;
            switch (type){
                case STATEMENT_COVERAGE:
                    name="lineList";
                    break;
                case EDGE_COVERAGE:
                    name="edgeList";
                    break;
                case BLOCK_COVERAGE:
                    name="blockList";
                    break;
                case BRANCH_COVERAGE:
                    name="branchList";
                    break;
                case METHOD_COVERAGE:
                    name="methodList";
                    break;
                default:
                    throw new RuntimeException("no such xmltype");
            }
            List<Element> l2=doc.getRootElement().elements(name).get(0).elements();
            return (double)100*l1.size()/(double)l2.size();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("read xml error");
        }
    }

    //哪些行被覆盖（只有动态信息）
    public static Map<String,Set<Integer>> readXml(String path, XmlType type) throws IOException {
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        //文件不存在
        if(!new File(p).exists()){
            return new HashMap<>();
        }
        if(type== XmlType.STATEMENT_COVERAGE){return readStatementCoverageXml(p);}
        return null;
    }

    //包覆盖（静态信息+动态信息）
    public static Map<String, Pair<Integer,Integer>> readXml2(String path, XmlType type) throws IOException {
        String p=path;
        if(!path.endsWith(".xml")) p+=".xml";
        //文件不存在
        if(!new File(p).exists()){
            return null;
        }
        if(type== XmlType.STATEMENT_COVERAGE){return readStatementCoverageXml2(p);}
        return null;
    }

    private static Map<String, Set<Integer>> readStatementCoverageXml(String path){
        SAXReader reader = new SAXReader();
        try {
            Map<String,Set<Integer>> m=new HashMap<>();
            Document doc = reader.read(path);
            List<Element> results=doc.getRootElement().elements("testResult");
            for(Element element:results.get(0).elements("line")){
                String className=element.attributeValue("className");
                int lineNumber=Integer.parseInt(element.attributeValue("lineNum"));
                m.putIfAbsent(className,new HashSet<>());
                m.get(className).add(lineNumber);
            }
            return m;
        } catch (DocumentException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    private static Map<String, Pair<Integer,Integer>> readStatementCoverageXml2(String path){
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(path);
            //分两部分进行统计
            //统计每个包的行数
            Map<String,Integer> pkgLines=new HashMap<>();
            List<Element> lines=doc.getRootElement().elements("lineList");
            for(Element line:lines.get(0).elements("line")){
                String className=line.attributeValue("className");
                int index=className.lastIndexOf("/");
                if(index<0) continue;
                String pkgName=className.substring(0,index).replace("/",".");
                pkgLines.putIfAbsent(pkgName,0);
                pkgLines.put(pkgName,pkgLines.get(pkgName)+1);
            }
            //统计每个包覆盖的行数
            List<Element> testResult=doc.getRootElement().elements("testResult");
            Map<String,Integer> pkgLinesCovered=new HashMap<>();
            for(Element element:testResult.get(0).elements("line")){
                String className=element.attributeValue("className");
                int index=className.lastIndexOf("/");
                String pkgName=className.substring(0,index).replace("/",".");
                pkgLinesCovered.putIfAbsent(pkgName,0);
                pkgLinesCovered.put(pkgName,pkgLinesCovered.get(pkgName)+1);
            }

            Map<String, Pair<Integer,Integer>> m=new HashMap<>();
            for(String pkg:pkgLines.keySet()){
                m.put(pkg,new Pair<>(pkgLines.get(pkg),pkgLinesCovered.getOrDefault(pkg,0)));
            }
            return m;
        } catch (DocumentException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }



}
