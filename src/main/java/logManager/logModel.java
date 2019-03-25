package logManager;

import Shared.fileHandler;
import Constants.enumeration;
import Constants.preferences;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class logModel
{

    /*Shared Instance*/
    private static final logModel sharedInstance = new logModel();
    private ReentrantLock lock = new ReentrantLock();

    /*Queue Initialization*/
    private final ArrayList<logMessageModel> errorDataQueue = new ArrayList<>();
    private final ArrayList<logMessageModel> requestDataQueue = new ArrayList<>();
    private final ArrayList<logMessageModel> foundURLDataQueue = new ArrayList<>();

    /*Data Initialization*/
    private int totalRunningThread = 0;

    /*Initialization*/
    public static logModel getInstance()
    {
        return sharedInstance;
    }

    public void resetQueues()
    {
        errorDataQueue.removeAll(errorDataQueue);
        requestDataQueue.removeAll(requestDataQueue);
        foundURLDataQueue.removeAll(foundURLDataQueue);
    }

    /*Setter or Log Methods*/
    public void logRequest(String messageType, String message)
    {
        logMessageModel model = new logMessageModel(message, messageType);
        requestDataQueue.add(model);
    }

    public void logError(String messageType, String message)
    {
        logMessageModel model = new logMessageModel(message, messageType);
        errorDataQueue.add(model);
    }

    public void logFoundURL(String messageType, String message)
    {
        logMessageModel model = new logMessageModel(message, messageType);
        foundURLDataQueue.add(model);
    }

    public logMessageModel logErrorModel()
    {
        lock.lock();
        try
        {
            logMessageModel model = errorDataQueue.get(0);
            errorDataQueue.remove(0);
            return model;
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }

    public void setThreadCount(int runningThread)
    {
        totalRunningThread = runningThread;
        if(runningThread<0)
        {
            runningThread = 0;
        }
    }

    public void addThreadCount(int runningThread)
    {
        totalRunningThread = totalRunningThread + runningThread;
    }

    /*Getter Methods*/
    public logMessageModel getRequestModel()
    {
        lock.lock();
        try
        {
            logMessageModel model = requestDataQueue.get(0);
            requestDataQueue.remove(0);
            return model;
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }

    public logMessageModel getFoundURLModel()
    {
        lock.lock();
        try
        {
            logMessageModel model = foundURLDataQueue.get(0);
            foundURLDataQueue.remove(0);
            return model;
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }

    /*Helper Method*/
    public boolean isErrorModelEmpty()
    {
        lock.lock();
        try
        {
            return errorDataQueue.isEmpty();
        }
        finally
        {
            lock.unlock();
        }
    }

    public boolean isRequestModelEmpty()
    {
        lock.lock();
        try
        {
            return requestDataQueue.isEmpty();
        }
        finally
        {
            lock.unlock();
        }
    }

    public boolean isFoundURLModelEmpty()
    {
        lock.lock();
        try
        {
            return foundURLDataQueue.isEmpty();
        }
        finally
        {
            lock.unlock();
        }

    }

    public int getPausedThread()
    {
        return preferences.maxThreadCount - totalRunningThread;
    }

    public int getRunningThread()
    {
        return totalRunningThread;
    }

    public int getSize()
    {
        return errorDataQueue.size() + requestDataQueue.size() + foundURLDataQueue.size();
    }

    public void logMessgeToFile(String message, String messageType, enumeration.logType Type)
    {
        try
        {
            fileHandler.appendFile("log.txt", message + "\n");
        }
        catch (IOException | URISyntaxException ex)
        {
            Logger.getLogger(logController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
