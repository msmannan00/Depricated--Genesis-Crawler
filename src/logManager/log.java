package logManager;

import application.fileHandler;
import constants.enumeration.logType;
import constants.string;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class log
{

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
        logController.getInstance().logMessage(messageType, message,logType);
    }

    public static void logMessage(String message, String messageIdentifier)
    {
        try
        {
            fileHandler.appendFile("log.txt", messageIdentifier + " : " + message + "\n");
        }
        catch (IOException | URISyntaxException ex)
        {
            Logger.getLogger(log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void logThreadCount(int threadCount)
    {
        logController.getInstance().logThreadCount(threadCount);
    }

}
