package logManager;

import constants.enumeration.logType;
import java.util.concurrent.locks.ReentrantLock;

public class logController
{

    /*Shared Instance*/
    private static final logController sharedInstance = new logController();
    private int logFileCounter = 0;
    private int logCounter = 0;

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

    public int getLogFileCounter()
    {
        return logFileCounter;
    }
    
    public void updateLogFileCounter()
    {
        logFileCounter+=1;
    }

    public void resetLogFileCounter()
    {
        logFileCounter=0;
    }
    
    public int getLogCounter()
    {
        return logCounter;
    }

    public void updateLogCounter()
    {
        logCounter+=1;
    }
    
    public void resetLogCounter()
    {
        logCounter=0;
    }

}
