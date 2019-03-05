package Shared;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class sessionManager {
    private static sessionManager sharedInstance = new sessionManager();

    public static sessionManager getInstance() {
        return sharedInstance;
    }

    private sessionManager() {
    }

    public Thread createSession(Object object, String methodName, int delay, boolean isRecursive, Scanner scanner,Class[] parameterTypes) {
        Thread thread = new Thread(() ->
        {
                try
                {
                    Method method = object.getClass().getMethod(methodName,parameterTypes);
                    do {
                        sleep(delay);
                        if (scanner == null)
                        {
                            method.invoke(object);
                        }
                        else
                        {
                            method.invoke(object, scanner);
                        }

                    } while (isRecursive);
                }
                catch (IllegalAccessException | InvocationTargetException | InterruptedException | NoSuchMethodException e)
                {
                    e.printStackTrace();
                }

        });
        thread.start();
        return thread;
    }

}
