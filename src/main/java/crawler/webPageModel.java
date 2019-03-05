package crawler;

import java.util.ArrayList;

public class webPageModel
{
    String title = "";
    String logo = "";
    String summary = "";
    String keyword = "";
    ArrayList<String> urlList;

    public webPageModel(String title, String logo, String summary, String keywork, ArrayList<String> urlList)
    {
        this.title = title;
        this.logo = logo;
        this.summary = summary;
        this.keyword = keywork;
        this.urlList = urlList;
    }


}
