package application;

import logManager.log;
import constants.enumeration;
import constants.string;
import constants.preferences;
import constants.status;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.*;
import crawler.urlHelperMethod;
import static java.lang.Thread.sleep;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class webRequestHandler
{

    /*Shared Instance*/
    private static final webRequestHandler sharedInstance = new webRequestHandler();

    /*Private Variable*/
    private ReentrantLock lock = new ReentrantLock();

    public static webRequestHandler getInstance()
    {
        return sharedInstance;
    }

    public String requestConnection(String url, String threadID) throws MalformedURLException, IOException, Exception
    {
        if (urlHelperMethod.getNetworkType(url).equals(enumeration.UrlTypes.onion))
        {
            return requestOnionConnection(url, threadID);
        }
        else
        {
            return requestBaseConnection(url, threadID);
        }
    }

    /*USE ONION PROXY IF ONION URL F OR FASTER REQUEST*/
    public String requestBaseConnection(String url, String threadID) throws MalformedURLException, IOException, Exception
    {
        log.logMessage("Reqesting Base URL : " + url, "THID : " + threadID + " : Thread Status");

        System.setProperty("http.agent", string.userAgent);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setConnectTimeout(210000);
        con.setReadTimeout(210000);
        String html = getContent(con, "Base", threadID);

        if (!html.contains(string.typeOnion) && status.onionFilterStatus)
        {
            throw new Exception("URL is not in " + string.textOnion + " cluster network");
        }
        return html;
    }

    /*HELPER METHOD USE ONION PROXY IF ONION URL FOR FASTER REQUEST*/
    public String requestOnionConnection(String url, String threadID) throws MalformedURLException, IOException, Exception
    {

        System.setProperty("http.agent", string.userAgent);
        System.setProperty("http.maxRedirects", "5");
        SocketAddress addr = new InetSocketAddress(string.proxyIP, preferences.proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
        con.setConnectTimeout(210000);
        con.setReadTimeout(210000);

        String content = getContent(con, string.textOnion, threadID);

        return content;
    }

    public Thread scannerTimeOut(Scanner scanner)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(310000);
                    scanner.close();
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(webRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread.start();
        return thread;
    }

    public String getContent(HttpURLConnection conn, String networkType, String threadID) throws IOException, Exception
    {
        String actualURL = conn.getURL().toString();
        log.logMessage("Reqesting Onion URL : " + conn.getURL(), "THID : " + threadID + " : Thread Status");
        conn.connect();

        Scanner scanner = new Scanner(conn.getInputStream());
        scanner.useDelimiter("\\A");
        String content = "";
        Thread thread = scannerTimeOut(scanner);
        while (scanner.hasNextLine())
        {
            content += scanner.nextLine() + " ";
        }
        scanner.close();
        thread.stop();

        if (!conn.getURL().toString().equals(actualURL))
        {
            actualURL = " : REDIRECTED TO " + actualURL;
        }

        log.print(networkType + " URL FOUND " + actualURL);
        log.logMessage("Found URL : " + actualURL, "THID : " + threadID + " : Thread Status");

        return content;
    }

    /*UPDATE URL DATABASE*/
    public String updateCache(String url, String threadID) throws MalformedURLException, IOException, URISyntaxException
    {
        try
        {
            String content = "";
            url = url.replaceAll(" ", "%20");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(6000);
            con.setReadTimeout(6000);
            con.setRequestMethod("GET");

            //log.print("__" + url + "__");
            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                log.logMessage("Error Saving Url : " + url, "THID : " + threadID + " : Thread Status");
            }
            else
            {
                Scanner scanner = new Scanner(con.getInputStream());
                scanner.useDelimiter("\\A");
                while (scanner.hasNextLine())
                {
                    content += scanner.nextLine();
                }
                scanner.close();
            }

            return content;
        }
        catch (IOException ex)
        {
            log.print("URL SAVE ERROR : " + url + " : " + ex.getMessage());
            log.logMessage("ERROR SAVING DATA URL : " + url, "THID : " + threadID + " : Thread Status");
            return "";
        }
    }
}
