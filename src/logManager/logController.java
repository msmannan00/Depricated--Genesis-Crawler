package logManager;

import application.fileHandler;
import constants.enumeration.logType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class logController
{

    /*Shared Instance*/
    private static final logController sharedInstance = new logController();
    ReentrantLock lock = new ReentrantLock();

    public static logController getInstance()
    {
        return sharedInstance;
    }

    /*Log message to crawler UI Model*/
    public void logMessage(String message, String messageType, logType Type)
    {
        logModel.getInstance().logMessgeToFile(message, messageType, Type);
    }

    /*Log Thread Count*/
    public void logThreadCount(int threadCount)
    {
        logModel.getInstance().setThreadCount(threadCount);
    }

}
