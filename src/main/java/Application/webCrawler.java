package Application;

import static java.lang.Thread.sleep;

import Shared.*;
import Constants.enumeration;
import Constants.eventListner;
import logManager.log;
import Constants.preferences;
import Constants.status;
import Constants.string;
import crawler.crawler;
import crawler.urlModel;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import logManager.logController;
import org.apache.commons.lang.SerializationUtils;

public class webCrawler
{

    /*INSTANCES DECLARATIONS PRIVATE*/
    private final ArrayList<Thread> emptyThreadQueue = new ArrayList<>();
    private final ArrayList<Thread> pausedThreadQueue = new ArrayList<>();
    private final ArrayList<Thread> runningThreadQueue = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock pauseUpdateLock = new ReentrantLock();

    private crawler htmlParser;
    private int pausedThreadCounter = preferences.maxThreadCount;

    private static final webCrawler sharedInstance = new webCrawler();

    public static webCrawler getInstance()
    {
        return sharedInstance;
    }

    public void initialize()
    {

    }

    /*CRAWLER INITIALIZATION*/
    private webCrawler()
    {
        int threadCount = 0;
        while (threadCount < preferences.maxThreadCount)
        {
            threadCount++;
            createCrawler();
        }

        htmlParser = (crawler) helperMethod.readObjectFromFile();
        threadInitialization();
        threadController();
        htmlParser.queueURLInitialization();
    }

    private void threadInitialization()
    {
        int count = 0;
        while (count < runningThreadQueue.size())
        {
            if (!runningThreadQueue.get(count).isAlive())
            {
                runningThreadQueue.get(count).start();
                count = 0;
            }
            count++;
        }
    }

    /*CRAWLER HELPER METHODS*/

    private void threadController()
    {
        sessionManager.getInstance().createSession(this, "queueReloadInvoke", preferences.requestQueueReloadGap,true,null,null);
        sessionManager.getInstance().createSession(this, "threadManagerInvoke", preferences.threadAwakeTime,true,null,null);
        sessionManager.getInstance().createSession(this, "backupManagerInvoke", preferences.backupTimer,true,null,null);
        sessionManager.getInstance().createSession(this, "backupManagerForcedInvoke", preferences.backupTimerForced,true,null,null);
    }


    private void createCrawler()
    {
        Thread thread = new Thread()
        {
            @Override
            synchronized public void run()
            {
                String host = string.emptyString;
                urlModel urlmodel;

                while (true)
                {
                    try
                    {
                        lockManager.getInstance().pauseThread(lock, this, webCrawler.this, host);
                        wait();

                        if (status.appStatus == enumeration.appStatus.running)
                        {

                            updatePauseCounter(-1);
                            if (htmlParser.isHostEmpty(host))
                            {
                                htmlParser.removeFromHostIfParsed(host);
                                log.logMessage("RE-Fethcing URL", "THID : " + this.getId() + " : Thread Status");
                                host = lockManager.getInstance().getHtmlParserKey(lock, htmlParser);
                                log.logMessage("URL Fethched : " + host, "THID : " + this.getId() + " : Thread Status");
                            }

                            urlmodel = htmlParser.getUrl(host);
                            log.logMessage("Fethcing From Same Host", "THID : " + this.getId() + " : Thread Status");
                            String url = urlmodel.getURL();
                            log.logMessage("Sending Url Request : " + url, "THID : " + this.getId() + " : Thread Status");
                            accessedURLModel model = webRequestHandler.getInstance().requestConnection(url, String.valueOf(this.getId()));
                            String html = model.getContent();
                            urlmodel.setURL(model.getAccessedURL());
                            log.logMessage("Parsing HTML : " + url, "THID : " + this.getId() + " : Thread Status");
                            htmlParser.parse_html(html, urlmodel, String.valueOf(this.getId()));
                            log.logMessage("Parsing Completed : " + url, "THID : " + this.getId() + " : Thread Status");
                            updatePauseCounter(1);

                        }
                    }
                    catch (Exception ex)
                    {
                        updatePauseCounter(1);
                        //log.print("", ex);
                        //ex.printStackTrace();
                        log.logMessage("Thread Error : " + ex.getMessage() + " : " + host, "THID : " + this.getId() + " : Thread Status");
                    }
                }
            }
        };
        runningThreadQueue.add(thread);
    }

    /*Crawler Invoke Method*/

    public void backupManagerInvoke() throws InterruptedException
    {
        executeBackup();
    }

    public void backupManagerForcedInvoke() throws InterruptedException
    {
        if (eventListner.getInstance().getBackupState())
        {
            executeBackup();
        }
    }

    private void executeBackup() throws InterruptedException
    {
        status.appStatus = enumeration.appStatus.paused;
        while(!runningThreadQueue.isEmpty())
        {
            sleep(1000);
        }

        crawler parser = (crawler) SerializationUtils.clone(htmlParser);
        helperMethod.createBackup(parser, string.filepath_queue_manager_backup + "_" + helperMethod.getCurrentDateTime());
        status.appStatus = enumeration.appStatus.running;
        eventListner.getInstance().setBackupState(false);
    }

    public void queueReloadInvoke() throws Exception
    {
        log.logThreadCount(runningThreadQueue.size());
        if (htmlParser.getOnionQueuesSize()<=1 && status.appStatus == enumeration.appStatus.running)
        {
            ArrayList<String> data = fileHandler.readQueueStack();
            for(int counter=0;counter<data.size();counter++)
            {
                String url = data.get(counter);
                htmlParser.saveBackupURL(url.substring(2),Character.getNumericValue(url.charAt(0)),Character.getNumericValue(url.charAt(1)));
            }
        }
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void threadManagerInvoke()
    {
        if ((emptyThreadQueue.size()>0 || pausedThreadQueue.size() > 0 ) && status.appStatus == enumeration.appStatus.running)
        {
            Thread thread;
            if (emptyThreadQueue.size() > 0 && htmlParser.queueSize() > 0)
            {
                thread = emptyThreadQueue.get(0);
                emptyThreadQueue.remove(thread);
            }
            else if (pausedThreadQueue.size() > 0)
            {
                thread = pausedThreadQueue.get(0);
                pausedThreadQueue.remove(thread);
            }
            else if ((emptyThreadQueue.size() > 0 || pausedThreadCounter>0)&& htmlParser.queueSize() > 0)
            {
                thread = emptyThreadQueue.get(0);
                emptyThreadQueue.remove(thread);
            }
            else
            {
                return;
            }
            runningThreadQueue.add(thread);

            synchronized (thread)
            {
                thread.notify();
            }
        }
    }

    /*Helper Method*/

    public int getOnionThreads()
    {
        return htmlParser.getOnionThreads();
    }

    public int getParsingThreads()
    {
        return htmlParser.getParsingThreads();
    }
    
    public int getQueueSize()
    {
        return htmlParser.size();
    }

    public void pauseThread(Thread thread, String host)
    {
        runningThreadQueue.remove(thread);
        if (host.equals(""))
        {
            emptyThreadQueue.add(thread);
        }
        else
        {
            pausedThreadQueue.add(thread);
        }
    }

    private void updatePauseCounter(int count)
    {
        pauseUpdateLock.lock();
        try
        {
            pausedThreadCounter+=count;
            logController.getInstance().logThreadCount(preferences.maxThreadCount - pausedThreadCounter);
        }
        finally
        {
            pauseUpdateLock.unlock();
        }
    }

    public int getOnionQueuesSize()
    {
        return htmlParser.getOnionQueuesSize();
    }

    public int getParsingQueuesSize()
    {
        return htmlParser.getParsingQueuesSize();
    }

    public String priorityQueueLogs()
    {
        return htmlParser.priorityQueueLogs();
    }
    public String onionQueueLogs()
    {
        return htmlParser.onionQueueLogs();
    }

}
