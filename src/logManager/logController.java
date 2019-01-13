package logManager;

import constants.enumeration.logType;
import java.text.ParseException;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.UnsupportedLookAndFeelException;
import static application.ApplicationController.crawler;

public class logController
{

    /*Shared Instance*/
    private static final logController sharedInstance = new logController();

    /*Objects Declaration*/
    private static logViewController viewController;

    /*Variable Declaration*/
    private int logFileCounter = 0;
    private int logCounter = 0;

    ReentrantLock lock = new ReentrantLock();

    public static logController getInstance()
    {
        return sharedInstance;
    }

    public void showLogUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, ParseException
    {
        viewController = new logViewController();
        viewController.crawlerObject = crawler.getHtmlParser();
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
