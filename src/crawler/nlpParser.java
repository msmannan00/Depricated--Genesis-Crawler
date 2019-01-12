package crawler;

import application.helperMethod;
import constants.enumeration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import constants.string;
import java.util.ArrayList;
import logManager.log;
import org.apache.commons.lang.StringUtils;

public class nlpParser
{

    static Set<String> nounPhrases = new HashSet<>();
    static Set<String> adjectivePhrases = new HashSet<>();
    static Set<String> verbPhrases = new HashSet<>();
    String keyWords = "";

    public void getNounPhrases(Parse p)
    {
        if (p.getType().equals("NN") || p.getType().equals("NNS") || p.getType().equals("NNP") || p.getType().equals("NNPS"))
        {
            if (!keyWords.contains(p.getCoveredText()))
            {
                keyWords += p.getCoveredText() + "_";
            }
        }

        //if (p.getType().equals("JJ") || p.getType().equals("JJR") || p.getType().equals("JJS"))
        //{
        //keyWords += p.getCoveredText()+"_";
        //}
        //if (p.getType().equals("VB") || p.getType().equals("VBP") || p.getType().equals("VBG") || p.getType().equals("VBD") || p.getType().equals("VBN"))
        //{
        //keyWords += p.getCoveredText()+"_";
        //}
        for (Parse child : p.getChildren())
        {
            getNounPhrases(child);
        }
    }

    public String parserAction(String doc) throws Exception
    {
        InputStream is = new FileInputStream("en-parser-chunking.bin");
        ParserModel model = new ParserModel(is);
        Parser parser = ParserFactory.create(model);
        Parse topParses[];
        topParses = ParserTool.parseLine(doc, parser, 1);
        return keyWords;
    }

    public static String preProcessUrl(String URLLink, int type,String host,String threadId) throws URISyntaxException
    {
        if (URLLink.length() > 1 && URLLink.charAt(0) == '/' && URLLink.charAt(1) == '/')
        {
            URLLink = "http:" + URLLink;
        }

        else if (URLLink.indexOf("http") != 0)
        {
            URLLink = "http://" + URLLink;
        }
        if (URLLink.contains("#"))
        {
            URLLink = URLLink.split("#")[0];
        }
        URLLink = URLLink.replace(" ", "");

        if (type == 1)
        {
            if (urlHelperMethod.isUrlValid(URLLink) && URLLink.contains(".onion") && (!urlHelperMethod.getUrlExtension(URLLink).equals("link") || string.fullyParsableUrls.contains(host) || threadId.equals("-1")))
            {
            }
            else
            {
                return "";
            }
        }
        else if (type == 2)
        {
            if (urlHelperMethod.isUrlValid(URLLink) && URLLink.contains(".onion"))
            {
            }
            else
            {
                return "";
            }
        }
        else
        {
            if (urlHelperMethod.isUrlValid(URLLink) && (URLLink.contains(".onion") || !urlHelperMethod.getUrlExtension(URLLink).equals("link")))
            {
            }
            else
            {
                return "";
            }
        }

        String staticPage = urlHelperMethod.getUrlWithoutParameters(URLLink);
        String dynamicPage = URLLink.replace(staticPage, "");
        if (staticPage.length() > 500 || dynamicPage.length() > 500)
        {
            return "";
        }

        return URLLink;
    }

    /*THE FOLLOWNG URL FOUNDS URL EMBEDED IN TEXT OR CONTENT OF PAGE*/
    public static ArrayList<String> extractAndSaveUrlsFromContent(String HTML, String host,String threadId) throws MalformedURLException, IOException, URISyntaxException, Exception
    {
        Document doc = Jsoup.parse(HTML);
        ArrayList<String> urlListFiltered = new ArrayList<String>();

        Elements links = doc.select("a[href]");

        /*HRef Start*/
        for (int counter = 0; counter <= links.size() - 1; counter++)
        {
            String URLLink = links.get(counter).attr("href");
            URLLink = urlHelperMethod.isHRefValid(host, URLLink);

            URLLink = preProcessUrl(URLLink,2,host,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }

            urlListFiltered.add(URLLink);
        }
        /*HRef End*/

        /*Images Start*/
        links = doc.select("img[src]");
        for (int counter = 0; counter <= links.size() - 1; counter++)
        {
            String URLLink = links.get(counter).attr("abs:src");

            URLLink = preProcessUrl(URLLink,3,host,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }

            urlListFiltered.add(URLLink);
        }
        /*Images End*/

        /*Text Start*/
        String html = doc.body().text();
        String[] urlList = html.split(" ");

        for (String URLLink : urlList)
        {
            URLLink = preProcessUrl(URLLink,1,host,threadId);
            if (URLLink.equals(string.emptyString))
            {
                continue;
            }
            urlListFiltered.add(URLLink);
        }
        /*Text End*/
        /*
        String htmlUnspaced = HTML.replaceAll(" ", "");
        String currentToken = "";
        String currentUrl = "";
        boolean urlFound = false;

        for (int counter = 0; counter < htmlUnspaced.length(); counter++)
        {
            if (urlFound)
            {
                if (htmlUnspaced.charAt(counter) == ')')
                {
                    String URLLink = urlHelperMethod.getUrlExtension(currentUrl);
                    URLLink = preProcessUrl(URLLink);
                    if (!URLLink.contains(host))
                    {
                        currentUrl = urlHelperMethod.getUrlHost(host) + "/" + currentUrl;
                    }
                    if (URLLink.equals("image"))
                    {
                        urlListFiltered.add(currentUrl);
                    }
                    currentToken = "";
                    currentUrl = "";
                    urlFound = false;
                }
                else
                {
                    currentUrl += htmlUnspaced.charAt(counter);
                }
            }
            else if ("background-image:url".equals(currentToken))
            {
                urlFound = true;
            }
            else if ("background-image:url(".contains(htmlUnspaced.charAt(counter) + ""))
            {
                currentToken += htmlUnspaced.charAt(counter);
            }
            else
            {
                currentToken = "";
                currentUrl = "";
                urlFound = false;
            }
        }*/
        return urlListFiltered;
    }

    public static String extractKeywords(String doc) throws Exception
    {
        nlpParser parser = new nlpParser();
        return parser.parserAction(doc);
    }

    public static String extractLogo(String HTML)
    {
        String logoUrl = "";
        Document document = Jsoup.parse(HTML);

        Elements element = document.head().select("meta[itemprop=image]");

        if (element != null && element.size()>0)
        {
            Element item = element.first();
            if (element != null)
            {
                logoUrl = element.attr("content");
            }
        }

        return logoUrl;
    }

    public static String extractTitle(String HTML)
    {
        Document document = Jsoup.parse(HTML);
        String title = document.title();
        title = title.replaceAll("[–—\\[\\](){}*$!,></=\"]", " ");
        title = title.replace("..",".");
        title = title.replaceAll("[|:~-]", " | ");
        title = title.trim().replaceAll(" +", " ");

        if (title.length() > 0 && title.charAt(0) == ' ')
        {
            title = title.substring(1);
        }
        title = title.toLowerCase();
        if (title.contains("|"))
        {
            int index = title.indexOf("|");
            if (title.length() > index + 2 && Character.isLetter(title.charAt(index + 2)))
            {
                title = title.substring(0, index + 2) + (title.charAt(index + 2) + "").toUpperCase() + title.substring(index + 3).replaceAll("\\|", " ");
            }
            else
            {
                title = title.substring(0, index + 2) + title.substring(index + 2).replaceAll("\\|", "");
            }
        }
        if (title.length() > 0)
        {
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }
        else
        {
            return "";
        }

        //log.print(title);
        return title;
    }

    public static String extractSummary(String HTML)
    {
        Document document = Jsoup.parse(HTML);

        Elements paragraphs = document.select("p:not(:has(#coordinates))");
        String description = document.body().text();

        Elements metaTags = document.getElementsByTag("meta");

        for (Element metaTag : metaTags)
        {
            String content = metaTag.attr("content");
            String name = metaTag.attr("name");

            if ("d.description".equals(name))
            {
                description = content;
                break;
            }
        }

        description = description + " " + document.text();

        if (description.length() > 1050)
        {
            description = description.substring(0, 1050);
        }

        description = description.replaceAll("/n", "");
        description = description.replaceAll("[^a-zA-Z0-9\\s+]", "");
        description = description.toLowerCase();
        description = description.trim().replaceAll(" +", " ");
        if (description.length() > 0 && description.charAt(0) == ' ')
        {
            description = description.substring(1);
        }

        if (description.length() > 0)
        {
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
        }

        return description;
    }

    public static boolean isStopWord(String query)
    {
        String words = "a able about above abroad according accordingly across actually adj after afterwards again against ago ahead ain't all allow allows almost alone along alongside already also although always am amid amidst among amongst an and another any anybody anyhow anyone anything anyway anyways anywhere apart appear appreciate appropriate are aren't around as a's aside ask asking associated at available away awfully b back backward backwards be became because become becomes becoming been before beforehand begin behind being believe below beside besides best better between beyond both brief but by c came can cannot cant can't caption cause causes certain certainly changes clearly c'mon co co. com come comes concerning consequently consider considering contain containing contains corresponding could couldn't course c's currently d dare daren't definitely described despite did didn't different directly do does doesn't doing done don't down downwards during e each edu eg eight eighty either else elsewhere end ending enough entirely especially et etc even ever evermore every everybody everyone everything everywhere ex exactly example except f fairly far farther few fewer fifth first five followed following follows for forever former formerly forth forward found four from further furthermore g get gets getting given gives go goes going gone got gotten greetings h had hadn't half happens hardly has hasn't have haven't having he he'd he'll hello help hence her here hereafter hereby herein here's hereupon hers herself he's hi him himself his hither hopefully how howbeit however hundred i i'd ie if ignored i'll i'm immediate in inasmuch inc inc. indeed indicate indicated indicates inner inside insofar instead into inward is isn't it it'd it'll its it's itself i've j just k keep keeps kept know known knows l last lately later latter latterly least less lest let let's like liked likely likewise little look looking looks low lower ltd m made mainly make makes many may maybe mayn't me mean meantime meanwhile merely might mightn't mine minus miss more moreover most mostly mr mrs much must mustn't my myself n name namely nd near nearly necessary need needn't needs neither never neverf neverless nevertheless new next nine ninety no nobody non none nonetheless noone no-one nor normally not nothing notwithstanding novel now nowhere o obviously of off often oh ok okay old on once one ones one's only onto opposite or other others otherwise ought oughtn't our ours ourselves out outside over overall own p particular particularly past per perhaps placed please plus possible presumably probably provided provides q que quite qv r rather rd re really reasonably recent recently regarding regardless regards relatively respectively right round s said same saw say saying says second secondly see seeing seem seemed seeming seems seen self selves sensible sent serious seriously seven several shall shan't she she'd she'll she's should shouldn't since six so some somebody someday somehow someone something sometime sometimes somewhat somewhere soon sorry specified specify specifying still sub such sup sure t take taken taking tell tends th than thank thanks thanx that that'll thats that's that've the their theirs them themselves then thence there thereafter thereby there'd therefore therein there'll there're theres there's thereupon there've these they they'd they'll they're they've thing things think third thirty this thorough thoroughly those though three through throughout thru thus till to together too took toward towards tried tries truly try trying t's twice two u un under underneath undoing unfortunately unless unlike unlikely until unto up upon upwards us use used useful uses using usually v value various versus very via viz vs w want wants was wasn't way we we'd welcome well we'll went were we're weren't we've what whatever what'll what's what've when whence whenever where whereafter whereas whereby wherein where's whereupon wherever whether which whichever while whilst whither who who'd whoever whole who'll whom whomever who's whose why will willing wish with within without wonder won't would wouldn't x y yes yet you you'd you'll your you're yours yourself yourselves you've z zero";
        if (words.contains(query))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String extractKeyWords(String HTML)
    {

        String keyWord = "";
        Document document = Jsoup.parse(HTML);
        HTML = document.title();
        Elements paragraphs = document.select("p");
        for (Element p : paragraphs)
        {
            HTML = HTML + " " + p.text();
        }

        HTML = HTML.replaceAll("[^a-zA-Z0-9\\s+]", " ");
        HTML = HTML.trim().replaceAll(" +", " ");
        HTML = HTML.toLowerCase();

        String[] tokenList = nlpParser.extractSummary(HTML).split(" ");

        for (int e = 0; e < tokenList.length; e++)
        {
            String token = tokenList[e];
            String linkType = urlHelperMethod.getUrlExtension(token);
            enumeration.UrlTypes urlType = urlHelperMethod.getNetworkType(token);

            if (!nlpParser.isStopWord(token) && !helperMethod.isNumeric(token) && token.length() <= 10 && !keyWord.contains(token))
            {
                keyWord = keyWord + token + ".";
            }
        }

        if (keyWord.equals(""))
        {
            return "null_null";
        }
        keyWord = keyWord.toLowerCase();
        return keyWord;
    }

}
