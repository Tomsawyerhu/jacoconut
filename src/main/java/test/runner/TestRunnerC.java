package test.runner;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class TestRunnerC {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        URLClassLoader ucl = new URLClassLoader(new URL[]{new URL("file:"+"C:\\Users\\tom\\Desktop\\Hotel\\target\\classes\\"),new URL("file:"+"C:\\Users\\tom\\Desktop\\Hotel\\target\\test-classes\\")});
        Class cls = ucl.loadClass("com.example.hotel.blImpl.order.OrderServiceImplTest");
        Object o=cls.newInstance();
        Thread.currentThread().setContextClassLoader(ucl);
        cls.getMethod("getHotelOrdersTest").invoke(o);
    }
}
