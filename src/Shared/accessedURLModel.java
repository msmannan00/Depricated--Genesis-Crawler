package Shared;

public class accessedURLModel
{
    String url;
    String content;

    public accessedURLModel(String url,String content)
    {
        this.url = url;
        this.content = content;
    }

    public String getAccessedURL()
    {
        return url;
    }

    public String getContent()
    {
        return content;
    }
}
