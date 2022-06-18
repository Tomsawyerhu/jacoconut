package core.edge;

import org.apache.log4j.Logger;
import soot.*;
import soot.options.Options;

import java.io.File;
import java.util.Collections;

public class EdgeSootDriver {
    private static Logger logger=Logger.getLogger(EdgeSootDriver.class);
    public static void main(String[] args) {
        if(args.length==0){
            System.exit(0);
        }
        sootSetUp(args[0]);
        Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter", new EdgeInstrumenter()));
        Scene.v().loadNecessaryClasses();
        String[] clazz=new String[Scene.v().getApplicationClasses().size()];
        int j=0;
        for (SootClass i: Scene.v().getApplicationClasses()) {
            //System.out.println(i.getName());
            clazz[j]=i.getName();
            j+=1;
        }
        Main.main(clazz);
    }

    private static void sootSetUp(String project){
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(System.getProperty("user.dir") + File.separator + "target" + File.separator + "classes;"+project+File.separator+"target"+File.separator+"classes");
        Options.v().set_process_dir(Collections.singletonList(project + File.separator + "target" + File.separator + "classes"));
        Options.v().set_interactive_mode(true);
        Options.v().set_src_prec(Options.src_prec_only_class);
        Options.v().set_keep_line_number(true);
        Options.v().set_output_dir(project + File.separator + "target" + File.separator + "classes");
    }
}
