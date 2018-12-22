package application;

import static java.lang.Thread.sleep;
import constants.enumeration;
import constants.eventListner;
import logManager.log;
import constants.preferences;
import constants.status;
import constants.string;
import crawler.crawler;
import crawler.urlModel;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;

public class torWebCrawler
{

    /*INSTANCES DECLARATIONS PRIVATE*/
    private final ArrayList<Thread> pausedThreadQueue = new ArrayList<Thread>();
    private final ArrayList<Thread> runningThreadQueue = new ArrayList<Thread>();
    private final ReentrantLock lock = new ReentrantLock();

    private crawler htmlParser;
    private int threadCount = 0;

    /*INITIALIZATIONS*/
    torWebCrawler() throws IOException, InterruptedException
    {
        initializations();
    }

    private void initializations() throws IOException, InterruptedException
    {
        crawler tempCrawler = (crawler) helperMethod.readObjectFromFile();
        if (tempCrawler == null)
        {
            htmlParser = new crawler();
        }
        else
        {
            htmlParser = tempCrawler;
        }
        htmlParser.queryManager.clearParsingQueueKey();
    }

    /*CRAWLER INITIALIZATION*/
    public void initializeCrawler() throws InterruptedException, IOException
    {
        while (threadCount < preferences.maxThreadCount)
        {
            threadCount++;
            createCrawler();
        }

        threadController();

    }

    /*CRAWLER HELPER METHODS*/
    public void pauseThread(Thread thread)
    {
        runningThreadQueue.remove(thread);
        pausedThreadQueue.add(thread);
    }

    /*GETTER METHODS*/
    public crawler getHtmlParser()
    {
        return htmlParser;
    }

    public void threadInitialization()
    {
        /*Thread Initialization*/
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

    public boolean backupManager(boolean save_trigger)
    {
        if (!save_trigger)
        {
            save_trigger = true;
            status.appStatus = enumeration.appStatus.paused;
        }
        else
        {
            if (runningThreadQueue.isEmpty())
            {
                crawler parser = (crawler) SerializationUtils.clone(htmlParser);
                helperMethod.writeObjectToFile(parser, preferences.filepath_queue_manager);
                helperMethod.writeObjectBackupToFile(parser, preferences.filepath_queue_manager_backup + "_" + helperMethod.getCurrentDateTime());
                save_trigger = false;
                status.appStatus = enumeration.appStatus.running;
                eventListner.setBackupState(false);
            }
        }
        return save_trigger;
    }

    public void queueReload() throws IOException, URISyntaxException, Exception
    {
        if (htmlParser.size() <= preferences.minQueueSize)
        {
            htmlParser.parse_html("<html><p>" + fileHandler.readQueueStack() + "</p></html>", "", String.valueOf(-1));
        }
    }

    public void threadManager() throws IOException
    {
        if (pausedThreadQueue.size() > 0 && status.appStatus == enumeration.appStatus.running && htmlParser.size() > 0)
        {
            Thread thread = pausedThreadQueue.get(0);
            synchronized (thread)
            {
                pausedThreadQueue.remove(thread);
                runningThreadQueue.add(thread);
                thread.notify();
            }
        }
        if (status.appStatus == enumeration.appStatus.running)
        {
            //htmlParser.validateRetryUrl();
        }
    }

    public void threadController()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                threadInitialization();
                /*Thread Scheduler*/
                int counter_tmanager = 0;
                int counter_bmanager = 0;
                int counter_qmanager = 0;
                boolean save_trigger = false;
                while (true)
                {
                    try
                    {
                        sleep(1000);
                        //if (counter_tmanager >= preferences.requestQueueReloadGap && status.appStatus == enumeration.appStatus.running)
                        //{
                        //queueReload();
                        //counter_qmanager = 0;
                        //}
                        if (counter_tmanager >= preferences.requestTimeGap)
                        {
                            threadManager();
                            counter_tmanager = 0;
                        }
                        if (counter_tmanager >= preferences.backupTimer || save_trigger || eventListner.getBackupState())
                        {
                            log.logMessage("Saving Crawler Object", "Heart Beat");
                            save_trigger = backupManager(save_trigger);
                            counter_bmanager = 0;
                        }
                        counter_tmanager++;
                        counter_bmanager++;
                        //counter_qmanager++;
                        log.logThreadCount(runningThreadQueue.size());

                    }
                    catch (InterruptedException | IOException ex)
                    {
                        Logger.getLogger(torWebCrawler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger(torWebCrawler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    public void createCrawler() throws IOException, InterruptedException
    {
        String threadName = "Thread:" + threadCount;
        Thread thread = new Thread()
        {
            @Override
            synchronized public void run()
            {
                String host = string.emptyString;
                String url = "";
                urlModel urlmodel = new urlModel("", "");

                while (true)
                {
                    try
                    {
                        if (status.appStatus == enumeration.appStatus.running)
                        {
                            if (!htmlParser.isHostEmpty(host))
                            {
                                urlmodel = htmlParser.getUrl(host);
                                log.logMessage("Fethcing From Same Host", "THID : " + this.getId() + " : Thread Status");
                                url = urlmodel.getURL();
                                log.logMessage("Sending Url Request : " + url, "THID : " + this.getId() + " : Thread Status");
                                String html = webRequestHandler.getInstance().requestConnection(url, String.valueOf(this.getId()));
                                log.logMessage("Parsing HTML : " + url, "THID : " + this.getId() + " : Thread Status");
                                htmlParser.parse_html(html, url, String.valueOf(this.getId()));
                                log.logMessage("Parsing Completed : " + url, "THID : " + this.getId() + " : Thread Status");
                                lockManager.getInstance().pauseThread(lock, this, torWebCrawler.this);
                                wait();
                            }
                            else
                            {
                                log.logMessage("RE-Fethcing URL", "THID : " + this.getId() + " : Thread Status");
                                host = lockManager.getInstance().getHtmlParserKey(lock, htmlParser);
                                log.logMessage("URL Fethched : " + host, "THID : " + this.getId() + " : Thread Status");
                            }
                        }

                        if (status.appStatus == enumeration.appStatus.paused)
                        {
                            lockManager.getInstance().pauseThread(lock, this, torWebCrawler.this);
                            log.logMessage("Sleep Mode", "THID : " + this.getId() + " : Thread Status");
                            wait();
                        }
                    }
                    catch (Exception ex)
                    {
                        log.logMessage("Thread Error : " + ex.getMessage() + " : " + host, "THID : " + this.getId() + " : Thread Status");
                        //log.print("", ex);
                        if (urlmodel != null)
                        {
                            //htmlParser.addToRetryQueue(new retryModel(urlmodel.getParentURL(), urlmodel.getURL()));
                        }
                        try
                        {
                            lockManager.getInstance().pauseThread(lock, this, torWebCrawler.this);
                            wait();
                        }
                        catch (InterruptedException ex1)
                        {
                            Logger.getLogger(torWebCrawler.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            }
        };
        runningThreadQueue.add(thread);
    }
}
