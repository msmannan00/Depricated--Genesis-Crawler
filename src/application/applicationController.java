package application;

import java.io.FileNotFoundException;
import logManager.logViewController;
import logManager.log;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.UnsupportedLookAndFeelException;

public class applicationController
{

    public static torWebCrawler torRequestHandler = null;
    public static logViewController crawlerUI;

    public static void preInitialization() throws FileNotFoundException
    {
        fileHandler.clearFile("log.txt");
        log.logMessage("Starting Application", "Application Status");
    }

    /*TRIGGER CRAWLER*/
    public static void main(String[] args) throws InterruptedException, IOException, InstantiationException, InstantiationException, ParseException, ClassNotFoundException, IllegalAccessException, IllegalAccessException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException
    {
        try
        {
            preInitialization();
            torRequestHandler = new torWebCrawler();
            torRequestHandler.initializeCrawler();
            openUI();
        }
        catch (IOException | UnsupportedLookAndFeelException ex)
        {
            log.logMessage("Global Error : " + ex.getMessage(), "Critical | Exception");
        }
    }

    public static void openUI() throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
        log.logMessage("Opening User Interface", "Application Status");
        crawlerUI = new logViewController();
        crawlerUI.crawlerObject = torRequestHandler.getHtmlParser();
        crawlerUI.run();
    }

}
