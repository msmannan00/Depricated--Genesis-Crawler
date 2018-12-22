package crawler;

import application.fileHandler;
import static crawler.nlpParser.extractTitle;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import constants.enumeration;
import application.webRequestHandler;
import constants.enumeration.UrlTypes;
import constants.preferences;
import constants.status;
import constants.string;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;
import logManager.log;
import org.jsoup.Jsoup;


public class crawler implements Serializable
{

    /*URL QUEUES*/
    public queueManager queryManager;

    /*VARIABLES DECLARATIONS*/
    public duplicationFilter duplicateFilter;
    public ReentrantLock lock = new ReentrantLock();

    /*INITIALIZATIONS*/
    public crawler() throws IOException
    {
        variable_initalization();
     }

    public int size()
    {
        return queryManager.size();
    }

    public void clearQueues()
    {
        queryManager.clearQueues();
    }

    private void variable_initalization() throws IOException
    {
        queryManager = new queueManager();
        duplicateFilter = new duplicationFilter();
        duplicateFilter.Initialize();
    }

    public urlModel getUrl(String host) throws InterruptedException
    {
        return queryManager.getUrl(host);
    }

    public String getKey() throws InterruptedException
    {
        synchronized (this)
        {
            return queryManager.getKey();
        }
    }

    public boolean isHostEmpty(String host) throws InterruptedException
    {
        return queryManager.isHostEmpty(host);
    }

    /*METHOD PARSER*/
    public void parse_html(String HTML, String Url, String threadID) throws MalformedURLException, IOException, URISyntaxException, Exception
    {
        ArrayList<String> urlList = null;
        lock.lock();
        try
        {
            log.logMessage("Extracting URLS", "THID : " + threadID + " : Thread Status");
            urlList = nlpParser.extractAndSaveUrlsFromContent(HTML, Url);
            log.logMessage("Extracted URLS : " + urlList.size(), "THID : " + threadID + " : Thread Status");
            log.logMessage("Extracting Keywords", "THID : " + threadID + " : Thread Status");
            String keyWords = nlpParser.extractKeyWords(HTML);
            log.logMessage("Saving Current URL : " + Url, "THID : " + threadID + " : Thread Status");
            saveCurrentUrl(HTML, Url, keyWords, urlList, threadID);
        }
        finally
        {
            lock.unlock();
        }
    }

    public void saveCurrentUrl(String HTML, String Url, String keywords, ArrayList<String> urlList, String threadID) throws Exception
    {
        String title = extractTitle(HTML);
        String extractLogo = nlpParser.extractLogo(HTML);
        String currentUrlKey = "";
        if (urlHelperMethod.getNetworkType(Url).equals(enumeration.UrlTypes.onion) && status.cacheStatus)
        {
            String summary = nlpParser.extractSummary(HTML).replace("'", "");
            if(summary.length()>30)
            {
                String content = urlHelperMethod.createCacheUrl(Url, title, summary, enumeration.UrlDataTypes.all.toString(), keywords, extractLogo);
                currentUrlKey = saveUrlToServer(content, threadID);
            }
        }
        saveExtractedUrl(urlList, HTML, Url, title, threadID, currentUrlKey);
    }

    public String saveUrlToServer(String content, String threadID) throws Exception
    {
        return webRequestHandler.getInstance().updateCache(content, threadID);
    }

    public void validateRetryUrl() throws IOException
    {
        queryManager.validateRetryUrl();
    }

    public void addToRetryQueue(retryModel rmodel)
    {
        queryManager.addToRetryQueue(rmodel);
    }

    public void saveExtractedUrl(ArrayList<String> urlList, String html, String parentURL, String parentTitle, String threadID, String currentUrlKey) throws Exception
    {
        lock.lock();
        try
        {
            for (int e = 0; e < urlList.size(); e++)
            {
                String URLLink = urlList.get(e);
                String linkType = urlHelperMethod.getUrlExtension(URLLink);
                UrlTypes urlType = urlHelperMethod.getNetworkType(URLLink);
                if (urlHelperMethod.isUrlValid(URLLink))
                {
                    if (threadID.equals("-1") || !duplicateFilter.is_url_duplicate(0,URLLink))
                    {
                        if (size() > preferences.maxQueueSize)
                        {
                            fileHandler.appendFile(string.url_stack,URLLink+"\n");
                        }
                        else
                        {
                            if (linkType.equals("link"))
                            {
                                queryManager.setUrl(URLLink, parentURL);
                            }
                            else if (!currentUrlKey.equals(string.emptyString) && urlHelperMethod.getNetworkType(URLLink).equals(enumeration.UrlTypes.onion))
                            {
                                String title = "";
                                if (parentTitle.length() > 45)
                                {
                                    parentTitle = parentTitle.substring(0, 45) + "...";
                                }
                                if (URLLink.length() > preferences.maxDLinkUrlSize)
                                {
                                    title = URLLink.substring(URLLink.length() - preferences.maxDLinkUrlSize);
                                }
                                log.print("FILE " + " URL FOUND" + " " + URLLink);
                                String content = urlHelperMethod.createDLink(URLLink, title, urlHelperMethod.getUrlExtension(URLLink), currentUrlKey);
                                saveUrlToServer(content, threadID);
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}
