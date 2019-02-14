package Application;

import Constants.enumeration;
import Constants.preferences;
import Constants.status;
import Constants.string;
import Shared.accessedURLModel;
import Shared.fileHandler;
import Shared.webRequestHandler;
import crawler.crawler;
import logManager.log;
import crawler.urlModel;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class catagorizedCrawler
{
    private static final catagorizedCrawler sharedInstance = new catagorizedCrawler();
    private crawler newsCrawler = new crawler();
    private crawler financeCrawler = new crawler();
    private ReentrantLock lock;
    private ArrayList<String> seenList = new ArrayList<String>();
    public boolean isCatagorizerRunning = false;

    public static catagorizedCrawler getInstance()
    {
        return sharedInstance;
    }

    public void initializeCatagorizedCrawler()
    {
        initializeReScedular();
    }

    public void initializeParser()
    {
        String finance_list = fileHandler.readCatagoryFromFile(string.financial_file);
        String news_list = fileHandler.readCatagoryFromFile(string.news_file);
        urlModel urlmodelFinance = new urlModel("finance.onion", preferences.maxUrlDepth-2, enumeration.UrlDataTypes.finance);
        urlModel urlmodelNews = new urlModel("news.onion",preferences.maxUrlDepth-2, enumeration.UrlDataTypes.news);
        try
        {
            newsCrawler.parse_html(news_list,urlmodelNews,"-2");
            financeCrawler.parse_html(finance_list,urlmodelFinance,"-2");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initializeReScedular()
    {
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try { if (status.appStatus == enumeration.appStatus.running)
                        {
                            if(!isCatagorizerRunning)
                            {
                                isCatagorizerRunning = true;
                                initializeParser();
                                startCatagorizedCrawler(newsCrawler);
                                startCatagorizedCrawler(financeCrawler);
                            }
                            sleep(Long.parseLong("16400000"));
                        }
                        sleep(Long.parseLong("2000"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void startCatagorizedCrawler(crawler crawl)
    {
        new Thread()
        {
            public void run()
            {
                String host = string.emptyString;
                urlModel urlmodel;
                while (true)
                {
                    try
                    {
                        sleep(2000);
                        if (crawl.size()>0)
                        {
                            if (crawl.isHostEmpty(host))
                            {
                                host = crawl.getKey();
                            }
                            urlmodel = crawl.getUrl(host);
                            String url = urlmodel.getURL();
                            if(!seenList.contains(url))
                            {
                                seenList.add(url);
                                log.print(urlmodel.getURL() + " -- " + urlmodel.getDepth() + " -- " + crawl.size() + " -- " + urlmodel.getCatagory());
                                accessedURLModel model = webRequestHandler.getInstance().requestConnection(url, "-2");
                                //String html = model.getContent();
                                //urlmodel.setURL(model.getAccessedURL());
                                //crawl.parse_html(html, urlmodel, "-2");
                            }
                        }
                        else
                        {
                            seenList.clear();
                            isCatagorizerRunning = false;
                            break;
                        }
                    }
                    catch (Exception ex)
                    {
                        log.print("ERROR CATAGORY : " + ex.getMessage());
                    }
                }
            }
        }.start();
    }
}
