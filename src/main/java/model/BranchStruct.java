package model;

import org.objectweb.asm.Label;

public class BranchStruct{
        //unique for a branch
        public int branchId;
        //class#method
        public String callsite;
        //mark lines where it jumps to
        public int[] wheres;
        //mark labels where it jumps to
        public Label[] whereLabels;
        //start
        public int start;
        //type(0 for if,1 for switch)
        public int type;

        public BranchStruct(int branchId, String callsite, int[] wheres,Label[] whereLabels,int start,int type) {
            this.branchId = branchId;
            this.callsite = callsite;
            this.wheres=wheres;
            this.whereLabels=whereLabels;
            this.start=start;
            this.type=type;
        }

        public int[] wheres(){return wheres;}

        public int id(){return branchId;}

        public int start(){return start;}

        public int size(){
            return wheres.length;
        }

        public String type(){
            if(type==0){return "IF";}
            else if(type==1){
                return "SWITCH";
            }else{
                return "UNKNOWN";
            }
        }
    }