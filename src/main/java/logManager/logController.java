package logManager;

import Constants.enumeration.logType;

public class logController
{

    /*Shared Instance*/
    private static final logController sharedInstance = new logController();

    /*Variable Declaration*/
    private int logFileCounter = 0;
    private int logCounter = 0;

    public static logController getInstance()
    {
        return sharedInstance;
    }

    public void showLogUI() throws Exception {
        /*Objects Declaration*/
        logViewController viewController = new logViewController();
        viewController.run();
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
    public void logAddThreadCount(int threadCount)
    {
        logModel.getInstance().addThreadCount(threadCount);
    }


    int getLogFileCounter()
    {
        return logFileCounter;
    }
    
    void updateLogFileCounter()
    {
        logFileCounter+=1;
    }

    void resetLogFileCounter()
    {
        logFileCounter=0;
    }
    
    int getLogCounter()
    {
        return logCounter;
    }

    void updateLogCounter()
    {
        logCounter+=1;
    }
    
    void resetLogCounter()
    {
        logCounter=0;
    }

}
