package crawler;

import Constants.enumeration;

import java.io.Serializable;
import java.net.URISyntaxException;

public class urlModel implements Serializable
{
    private String cacheURL;
    private String URL;
    private final int urlDepth;
    private enumeration.UrlDataTypes isCatagorical;

    
    public urlModel(String URL, int urlDepth, enumeration.UrlDataTypes isCatagorical)
    {
        this.URL = URL;
        this.cacheURL = URL;
        this.urlDepth = urlDepth;
        this.isCatagorical = isCatagorical;
    }

    public void setCatagory(enumeration.UrlDataTypes status)
    {
        isCatagorical = status;
    }

    public String getURL()
    {
        return URL;
    }

    public String getCacheURL()
    {
        return cacheURL;
    }

    public void setURL(String url) throws URISyntaxException
    {
        if(!url.equals(this.URL))
        {
            duplicationFilter.getInstance().is_url_duplicate(url);;
        }
        this.URL = URL;
    }

    public enumeration.UrlDataTypes getCatagory()
    {
        return isCatagorical;
    }

    public int getDepth()
    {
        return urlDepth;
    }
}
