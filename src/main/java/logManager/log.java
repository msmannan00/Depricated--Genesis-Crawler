package logManager;

import Shared.fileHandler;
import Constants.preferences;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class log
{

    private static final ReentrantLock lock = new ReentrantLock();

    /*Print Message or Log*/
    public static void print(String message)
    {
        System.out.println(message);
    }

    /*Print Exceptions*/
    public static void print(String message, Exception error)
    {
        if(preferences.isErrorEnabled)
        {
            System.out.println(message);

            if (error != null)
            {
                error.printStackTrace();
            }
        }
    }

    /*Log Exceptions Errors and Warnings*/
    public static void logMessage(String message, String messageIdentifier)
    {
        lock.lock();
        try
        {
            if (logController.getInstance().getLogFileCounter() >= preferences.maxLogFiles)
            {
                logController.getInstance().resetLogFileCounter();
                fileHandler.clearFile("Logs/log_" + logController.getInstance().getLogFileCounter() + ".txt");
            }
            else if (logController.getInstance().getLogCounter() > preferences.maxLogLines)
            {
                logController.getInstance().updateLogFileCounter();
                logController.getInstance().resetLogCounter();
                fileHandler.clearFile("Logs/log_" + logController.getInstance().getLogFileCounter() + ".txt");
            }
            logController.getInstance().updateLogCounter();
            int counter = logController.getInstance().getLogFileCounter();
            fileHandler.appendFile("Logs/log_" + counter + ".txt", messageIdentifier + " : " + message + "\n");

        }
        catch (IOException | URISyntaxException ex)
        {
            Logger.getLogger(log.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            lock.unlock();
        }
    }

    public static void logThreadCount(int threadCount)
    {
        logController.getInstance().logThreadCount(threadCount);
    }

}
