package crawler;

import java.io.IOException;
import java.net.*;

import Constants.string;
import Constants.enumeration;
import Shared.helperMethod;
import Constants.preferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logManager.log;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.UrlValidator;

public class urlHelperMethod
{

    /*CHECK TYPE OF URL ONION OR BASEURL OR SAME HOST URL*/
    static enumeration.UrlTypes getNetworkType(String URLLink)
    {
        String hostURL = getUrlHost(URLLink);
        if (hostURL.contains(string.typeOnion))
        {
            return enumeration.UrlTypes.onion;
        }
        else if (!hostURL.equals(string.none))
        {
            return enumeration.UrlTypes.base;
        }
        else
        {
            return enumeration.UrlTypes.none;
        }
    }

    @SuppressWarnings("deprecation")
    static boolean isUrlValid(String URLLink)
    {
        URLLink = URLLink.replace(string.textOnion, "com");
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
        return urlValidator.isValid(URLLink);
    }

    static String getUrlWithoutParameters(String url) throws URISyntaxException
    {
        if(url.contains("#"))
        {
            url = url.split("#")[0];
        }
        URI uri = new URI(url);
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null, // Ignore the query part of the input url
                uri.getFragment()).toString();
    }

    static String getUrlHost(String URLLink)
    {
        try
        {
            Pattern pattern = Pattern.compile(string.urlHostMacherRegex);
            Matcher matcher = pattern.matcher(URLLink);
            matcher.find();
            String protocol = matcher.group(1);
            String domain = matcher.group(2);
            return protocol + domain;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    static String getSubUrl(String URLLink)
    {
        return URLLink.replace(getUrlHost(URLLink), "");
    }

    static enumeration.UrlDataTypes getUrlExtension(String URLLink)
    {
        String ext = FilenameUtils.getExtension(URLLink);

        if (URLLink.endsWith(".gif") || URLLink.endsWith(".jpg") || URLLink.endsWith(".png") || URLLink.endsWith(".svg") || URLLink.endsWith(".ico") || URLLink.endsWith(".jpeg"))
        {
            return enumeration.UrlDataTypes.image;
        }
        else if (URLLink.endsWith(".pdf") || URLLink.endsWith(".doc") || URLLink.endsWith(".ppt") || URLLink.endsWith(".txt"))
        {
            return enumeration.UrlDataTypes.doc;
        }
        else if (URLLink.endsWith(".mp4") || URLLink.endsWith(".3gp") || URLLink.endsWith(".mp3") || URLLink.endsWith(".avi") || URLLink.endsWith(".webm") || URLLink.endsWith(".mov"))
        {
            return enumeration.UrlDataTypes.video;
        }
        else if(URLLink.endsWith(".zip"))
        {
            return enumeration.UrlDataTypes.none;
        }
        else if(urlHelperMethod.getUrlHost(URLLink).endsWith(".onion") || URLLink.endsWith(".onion") || URLLink.endsWith(".ajax") || URLLink.endsWith(".php") || URLLink.endsWith(".html")  || URLLink.endsWith(".htm")  || ext.equals(""))
        {
            return enumeration.UrlDataTypes.link;
        }
        else
        {
            return enumeration.UrlDataTypes.none;
        }
    }

    @SuppressWarnings("deprecation")
    static String createCacheUrl(String URL, String Title, String Description, String logo, String keyword, enumeration.UrlDataTypes utype) throws IOException
    {

        if (Title.equals(""))
        {
            Title = "Title not found";
        }
        if (Description.equals(""))
        {
            Description = "Description not found";
        }

        String url = "url=" + URLEncoder.encode(URL, "UTF-8") + "&title=" +URLEncoder.encode(Title) + "&desc=" + URLEncoder.encode(Description, "UTF-8") + "&type=" + preferences.networkType.toLowerCase() + "&n_type=" + preferences.networkType + "&s_type=" + utype.toString() + "&live_date=" + URLEncoder.encode(helperMethod.getCurrentDate(), "UTF-8") + "&key_word="+URLEncoder.encode(keyword, "UTF-8")+"&update_date=" + URLEncoder.encode(helperMethod.getCurrentDate(), "UTF-8") + "&logo=" + URLEncoder.encode(logo, "UTF-8");
        return url;
    }

    static String createDLink(String URL, String datatype, String currentUrlKey) throws IOException {
        if (datatype.equals(""))
        {
            return "";
        }
        return "url=" + URLEncoder.encode(URL, "UTF-8") + "&type=" + preferences.networkType.toLowerCase() + "&n_type=" + preferences.networkType + "&s_type=" + datatype + "&live_date=" + URLEncoder.encode(helperMethod.getCurrentDate(), "UTF-8") + "&update_date=" + URLEncoder.encode(helperMethod.getCurrentDate(), "UTF-8") + "&WP_FK=" + currentUrlKey;
    }

    public static HttpURLConnection createHTTPConnection(String url) throws IOException {

        HttpURLConnection con;
        URL obj = new URL(url);

        if (urlHelperMethod.getNetworkType(url).equals(enumeration.UrlTypes.onion))
        {
            SocketAddress addr = new InetSocketAddress(string.proxyIP, preferences.proxyPort);
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);
            con = (HttpURLConnection) obj.openConnection(proxy);
        }
        else
        {
            con = (HttpURLConnection) obj.openConnection();
        }

        con.setConnectTimeout(preferences.connectionTimeOut);
        con.setReadTimeout(preferences.readTimeOut);

        return con;
    }

    static String isHRefValid(String host, String url)
    {
        try
        {
            return new URL(new URL(host), url).toString();
        }
        catch (MalformedURLException ex)
        {
            return string.emptyString;
        }
    }

    public static String createBackupLink(String URLLink,String parentURL,int Depth)
    {
        if(urlHelperMethod.getUrlHost(URLLink).equals(urlHelperMethod.getUrlHost(parentURL)))
        {
            URLLink = "1"+String.valueOf(Depth+1)+URLLink;
        }
        else
        {
            URLLink = "0"+String.valueOf(3)+URLLink;
        }
        return URLLink+"\n";
    }

}
