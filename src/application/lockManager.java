package application;

import crawler.crawler;
import java.util.concurrent.locks.ReentrantLock;

public class lockManager
{

    private static final lockManager sharedInstance = new lockManager();

    public static lockManager getInstance()
    {
        return sharedInstance;
    }

    public String getHtmlParserKey(ReentrantLock lock, crawler htmlParser) throws InterruptedException
    {
        lock.lock();
        try
        {
            return htmlParser.getKey();
        }
        finally
        {
            lock.unlock();
        }
    }

    public void pauseThread(ReentrantLock lock, Thread thread, torWebCrawler parent) throws InterruptedException
    {
        lock.lock();
        try
        {
            parent.pauseThread(thread);
        }
        finally
        {
            lock.unlock();
        }
    }

}
