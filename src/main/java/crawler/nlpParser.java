package crawler;

import java.net.URISyntaxException;

import Constants.enumeration;
import Constants.preferences;
import Shared.helperMethod;
import Shared.wordChecker;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import Constants.string;
import java.util.ArrayList;

class nlpParser
{

    /*Shared Instance*/
    private static final nlpParser sharedInstance = new nlpParser();

    public static nlpParser getInstance()
    {
        return sharedInstance;
    }

    public webPageModel extractData(String HTML,urlModel host, String threadId) throws Exception
    {
        Document doc = Jsoup.parse(HTML);
        String title = extractTitle(doc);
        String extractLogo = extractLogo(doc);
        String summary = extractSummary(doc);
        ArrayList<String> urlList = extractAndSaveUrlsFromContent(doc, host, threadId);
        String keywords = extractKeyword(doc.text())+" none";
        return new webPageModel(title,extractLogo,summary,keywords,urlList);
    }

    public String extractKeyword(String doc)
    {
        String desc[] = doc.split(" ");
        String final_desc = "";

        for(int e=0;e<desc.length;e++)
        {
            if(helperMethod.isAlpha(desc[e]) && desc[e].length()>2 && wordChecker.getInstance().isWordValid(desc[e]))
            {
                final_desc = final_desc + " " + desc[e];
            }
        }
        return final_desc;
    }

    private String extractLogo(Document document)
    {
        String logoUrl = "";

        Elements element = document.head().select("meta[itemprop=image]");

        if (element != null && element.size()>0)
        {
            logoUrl = element.attr("content");
        }

        return logoUrl;
    }

    private String extractTitle(Document document)
    {
        String title = document.title();
        title = trimTitle(title.replaceAll("[–—\\[\\](){}*$!,></=\"]", " ")
                .replace("..",".")
                .replaceAll("[|:~-]", " | "))
                .toLowerCase();
        title = helperMethod.toCapital(title);
        title = helperMethod.removeSymbolsAtEnd(title,"|");

        String titlelist[] = title.split(" ");
        String final_list = "";

        for(int e=0;e<titlelist.length;e++)
        {
            if(helperMethod.isAlpha(titlelist[e]) && titlelist[e].length()>2)
            {
                final_list = final_list + " " + titlelist[e];
            }
        }

        return final_list;
    }

    private String extractSummary(Document document)
    {
        float isNotAlphaCount = 0;
        String description = document.title();

        Elements metaTags = document.getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String content = metaTag.attr("content");
            String name = metaTag.attr("name");

            if("description".equals(name)) {
                description = description + " " + content + " ";
                break;
            }
        }

        Elements paragraphs = document.select("p");
        for(Element p : paragraphs)
        {
            description = description + " " + p.text() + " ";
        }

        Elements divs = document.select("div");
        for(Element d : divs)
        {
            if(d.text().length()>=4)
            {
                description = description + " " + d.ownText() + " ";
            }
        }

        for(Element d : divs)
        {
            if(d.text().length()>=4)
            {
                description = description + " " + d.text() + " ";
            }
        }

        description = description + " " + document.text();

        //description = trimTitle(description.replaceAll("/n", description)
        //        .replaceAll("[^a-zA-Z0-9\\s+]", "")
        //        .toLowerCase());
        //description = helperMethod.toCapital(description);

        String preprocess = "";
        String[] split = description.split(" ");
        for(int e=0;e<split.length&&preprocess.length()<1050;e++)
        {
            if(helperMethod.isAlpha(split[e]))
            {
                preprocess = preprocess + " " + split[e];
            }
            else
            {
                isNotAlphaCount++;
            }
        }

        description = preprocess.replace("'", "").toLowerCase();
        description = helperMethod.toCapital(description);
        return description;
    }

    /*THE FOLLOWNG URL FOUNDS URL EMBEDED IN TEXT OR CONTENT OF PAGE*/
    private ArrayList<String> extractAndSaveUrlsFromContent(Document document, urlModel host, String threadId) throws Exception
    {

        String hostStr = host.getURL();

        ArrayList<String> urlListFiltered = new ArrayList<>();

        /*Images Start*/
        Elements links = document.select("img[src]");
        for (int counter = 0; counter <= links.size() - 1; counter++)
        {
            String URLLink = links.get(counter).attr("abs:src");

            URLLink = preProcessUrl(URLLink,3,hostStr,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }

            urlListFiltered.add(URLLink);
        }
        /*Images End*/

        /*Depth Breaker*/
        if(host.getDepth()>= preferences.maxUrlDepth)
        {
            return urlListFiltered;
        }
        /*Images End*/

        /*HRef Start*/
        links = document.select("a[href]");
        for (int counter = 0; counter <= links.size() - 1; counter++)
        {
            String URLLink = links.get(counter).attr("href");
            URLLink = urlHelperMethod.isHRefValid(hostStr, URLLink);

            URLLink = preProcessUrl(URLLink,2,hostStr,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }

            urlListFiltered.add(URLLink);
        }
        /*HRef End*/

        if((!host.getCatagory().equals(enumeration.UrlDataTypes.news) && !host.getCatagory().equals(enumeration.UrlDataTypes.finance) && !host.getURL().equals(string.baseLink)) || (host.getCatagory().equals(enumeration.UrlDataTypes.news) || host.getCatagory().equals(enumeration.UrlDataTypes.finance)) && !host.getURL().equals("finance.onion") && !host.getURL().equals("news.onion"))
        {
            return urlListFiltered;
        }

        /*Text Start*/
        String html = document.body().text();
        String[] urlList = html.split(" ");

        for (String URLLink : urlList)
        {
            URLLink = preProcessUrl(URLLink,1,hostStr,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }
            urlListFiltered.add(URLLink);
        }
        return urlListFiltered;
    }

    public String preProcessUrl(String urlLink, int type, String host, String threadId) throws URISyntaxException
    {
        if(!urlHelperMethod.getNetworkType(urlLink).equals(enumeration.UrlTypes.onion))
        {
            return "";
        }

        String subUrl = urlHelperMethod.getSubUrl(urlLink);
        if (urlLink.contains("#") | (subUrl.length()<=3 && subUrl.length()>1) || urlLink.length()>600 || urlLink.length() < 2)
        {
            return "";
        }

        urlLink = urlLink.replace(" ", "");

        if (urlLink.charAt(0) == '/' && urlLink.charAt(1) == '/' || !urlLink.startsWith("http"))
        {
            urlLink = "http:" + urlLink;
        }

        if(type == 1 && (!urlHelperMethod.isUrlValid(urlLink) || !urlLink.contains(".onion") || (urlHelperMethod.getUrlExtension(urlLink).equals("link") && !host.equals(string.baseLink) && !threadId.equals("-1")))
        || type == 2 && (!urlHelperMethod.isUrlValid(urlLink) || !urlLink.contains(".onion"))
        || type == 3 && !urlHelperMethod.isUrlValid(urlLink) || (!urlLink.contains(".onion") && urlHelperMethod.getUrlExtension(urlLink).equals("link")))
        {
            return "";
        }

        return urlLink;
    }


    @NotNull
    private String trimTitle(String title) {
        title = title.trim().replaceAll(" +", " ");

        if (title.length() > 0 && title.charAt(0) == ' ')
        {
            title = title.substring(1);
        }
        return title;
    }

    public void textSummarizerNew()
    {

    }

}
