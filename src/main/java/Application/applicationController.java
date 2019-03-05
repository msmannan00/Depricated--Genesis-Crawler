package Application;

import Shared.fileHandler;
import Constants.string;
import Shared.helperMethod;
import Shared.wordChecker;
import logManager.log;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.UnsupportedLookAndFeelException;
import logManager.logController;

public class applicationController
{

    private static void appInitialization() throws IOException
    {
        helperMethod.initializeNetworkSettings();
        fileHandler.clearDirectory(string.logDirectory);
        log.logMessage("Starting Application", "Application Status");
        wordChecker.getInstance().initialize();
        webCrawler.getInstance().initialize();
    }

    /*TRIGGER CRAWLER*/
    public static void main(String[] args) throws Exception
    {
        try
        {
            appInitialization();
            logInitialization();
        }
        catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | InterruptedException | ParseException | UnsupportedLookAndFeelException ex)
        {
            ex.printStackTrace();
            log.logMessage("Global Error : " + ex.getMessage(), "Critical | Exception");
        }
    }

    /*HELPER FRONTEND LOG UI*/
    private static void logInitialization() throws Exception {
        catagorizedCrawler.getInstance().initializeCatagorizedCrawler();
        log.logMessage("Opening User Interface", "Application Status");
        logController.getInstance().showLogUI();
    }

}
