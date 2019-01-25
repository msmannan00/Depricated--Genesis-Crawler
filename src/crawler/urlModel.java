package crawler;

import java.io.Serializable;

public class urlModel implements Serializable
{
    private final String URL;
    private final int urlDepth;

    
    public urlModel(String URL,int urlDepth)
    {
        this.URL = URL;
        this.urlDepth = urlDepth;
    }

    public String getURL()
    {
        return URL;
    }

    public int getDepth()
    {
        return urlDepth;
    }
}
