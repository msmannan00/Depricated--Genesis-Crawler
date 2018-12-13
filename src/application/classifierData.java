package application;

import constants.preferences;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import logManager.log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class classifierData
{

    /*Shared Instance*/
    private static final classifierData sharedInstance = new classifierData();
    ArrayList<String> classifierUrl = new ArrayList<String>();

    public static classifierData getInstance()
    {
        return sharedInstance;
    }

    public void startClassifier() throws FileNotFoundException, Exception
    {
        classifierUrl = fileHandler.readClassifierData("classifier_"+preferences.classfier_type);
        for(int e=0;e<classifierUrl.size();e++)
        {
            try
            {
                saveClassifierData(webRequestHandler.getInstance().requestOnionConnection(classifierUrl.get(e),"101"));
            }
            catch (Exception ex)
            {
                log.print(ex.getMessage());
            }
        }
    }

    public void saveClassifierData(String html)
    {
        Document doc = Jsoup.parse(html);
        String title = doc.title();
        String text = doc.body().text();
        fileHandler.saveClassifierData(text, title,preferences.classfier_type);
    }

}
