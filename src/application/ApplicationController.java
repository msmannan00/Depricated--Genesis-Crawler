package application;

import java.io.FileNotFoundException;
import logManager.log;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import javax.swing.UnsupportedLookAndFeelException;
import logManager.logController;

public class ApplicationController
{

    /*INITIALIZATIONS*/
    public static WebCrawler crawler = null;

    public static void preInitialization() throws FileNotFoundException, IOException
    {
        FileHandler.clearDirectory("Logs/");
        log.logMessage("Starting Application", "Application Status");
    }

    /*TRIGGER CRAWLER*/
    public static void main(String[] args) throws InterruptedException, IOException, InstantiationException, InstantiationException, ParseException, ClassNotFoundException, IllegalAccessException, IllegalAccessException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException, UnsupportedLookAndFeelException, URISyntaxException
    {

        try
        {
            preInitialization();
            crawlerInitialization();
            logInitialization();
        }
        catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | InterruptedException | ParseException | UnsupportedLookAndFeelException ex)
        {
            ex.printStackTrace();
            log.logMessage("Global Error : " + ex.getMessage(), "Critical | Exception");
        }
    }

    /*HELPER CRAWLER INITIALIATION*/
    public static void crawlerInitialization() throws IOException, InterruptedException
    {
        crawler = new WebCrawler();
        crawler.initializeCrawler();
    }

    /*HELPER FRONTEND LOG UI*/
    public static void logInitialization() throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
        log.logMessage("Opening User Interface", "Application Status");
        logController.getInstance().showLogUI();
    }

}
