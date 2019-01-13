package logManager;

import application.FileHandler;
import constants.enumeration.logType;
import constants.preferences;
import constants.string;
import java.io.IOException;
import java.net.MalformedURLException;
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
        System.out.println(message);

        if (error != null && string.reportException)
        {
            error.printStackTrace();
        }
    }

    /*Log Exceptions Errors and Warnings*/
    public static void logMessage(String message, String messageType, logType logType) throws IOException, MalformedURLException, URISyntaxException
    {
        logController.getInstance().logMessage(messageType, message, logType);
    }

    public static void logMessage(String message, String messageIdentifier)
    {
        lock.lock();
        try
        {
            if (logController.getInstance().getLogFileCounter() >= preferences.maxLogFiles)
            {
                logController.getInstance().resetLogFileCounter();
                FileHandler.clearFile("Logs/log_" + logController.getInstance().getLogFileCounter() + ".txt");
            }
            else if (logController.getInstance().getLogCounter() > preferences.maxLogLines)
            {
                logController.getInstance().updateLogFileCounter();
                logController.getInstance().resetLogCounter();
                FileHandler.clearFile("Logs/log_" + logController.getInstance().getLogFileCounter() + ".txt");
            }
            logController.getInstance().updateLogCounter();
            int counter = logController.getInstance().getLogFileCounter();
            FileHandler.appendFile("Logs/log_" + counter + ".txt", messageIdentifier + " : " + message + "\n");

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
