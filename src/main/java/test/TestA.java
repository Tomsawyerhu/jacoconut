package test;

public class TestA {
    public void func1(int i){
        if(i>0){
            System.out.println("no");
            System.out.println("no");
            System.out.println("no");
            System.out.println("no");
        } else{
            System.out.println("yep");
        }
        switch (i){
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
                break;
            case 3:
                System.out.println("3");
                break;
            case 4:
                System.out.println("4");
            case 5:
                System.out.println("5");
                break;
            default:
        }
    }

    public void func2(){
        System.out.println("no");
    }
}
