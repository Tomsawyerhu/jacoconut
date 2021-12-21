package test;
//switch-case测试
public class TestB {
    public void test(int a){
        switch (a){
            case 1:
                System.out.println(1);
                break;
            case 2:
                System.out.println(2);
                break;
            default:
        }
        System.out.println(4);
    }

    public void testCoverage(){
        test(1);
    }
}
