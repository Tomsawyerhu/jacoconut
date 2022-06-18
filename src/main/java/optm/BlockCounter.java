package optm;

public class BlockCounter {
    public static final Boolean flag=true;
    public static final Boolean flag1=false;
    public static final Boolean flag2=false;
    public static int blocks=0;
    public static int blocks1=0;
    public static int blocks2=0;
    public static void count(int size){
        synchronized (flag){
            blocks+=size;
        }
    }

    public static void count1(int size){
        synchronized (flag1){
            blocks1+=size;
        }
    }

    public static void count2(int size){
        synchronized (flag2){
            blocks2+=size;
        }
    }
}
