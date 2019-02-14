package Shared;

import logManager.log;
import Constants.string;
import Constants.preferences;

import java.io.DataOutputStream;
import java.net.*;
import crawler.urlHelperMethod;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class webRequestHandler
{

    /*Shared Instance*/
    private static final webRequestHandler sharedInstance = new webRequestHandler();

    public static webRequestHandler getInstance()
    {
        return sharedInstance;
    }

    public accessedURLModel requestConnection(String url, String threadID) throws Exception
    {
        return requestOnionConnection(url, threadID);
    }

    /*HELPER METHOD USE ONION PROXY IF ONION URL FOR FASTER REQUEST*/
    private accessedURLModel requestOnionConnection(String url, String threadID) throws Exception
    {
        log.logMessage("Reqesting Onion URL : " + url, "THID : " + threadID + " : Thread Status");
        HttpURLConnection con = urlHelperMethod.createHTTPConnection(url);
        con.connect();
        String content = getContent(con);
        log.print(string.textOnion + " URL FOUND " + con.getURL());
        log.logMessage("Found URL : " + con.getURL(), "THID : " + threadID + " : Thread Status");
        accessedURLModel model = new accessedURLModel(con.getURL().toString(),content);
        return model;
    }

    @SuppressWarnings("deprecation")
    private String getContent(HttpURLConnection conn) throws Exception
    {
        Scanner scanner = new Scanner(conn.getInputStream());
        scanner.useDelimiter("\\A");
        StringBuilder content = new StringBuilder();

        Thread thread = sessionManager.getInstance().createSession(this, "invokeScannerThread", preferences.httpScannerTimeOut,false,scanner,helperMethod.createScannerFunctionParameter());

        while (scanner.hasNextLine())
        {
            content.append(scanner.nextLine()).append(" ");
        }
        thread.stop();
        scanner.close();

        return content.toString();
    }

    /*UPDATE URL DATABASE*/
    public String updateCache(String postData, String threadID) throws Exception
    {
        URL obj = new URL(string.postUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);

        String urlParameters = postData;
        byte[] postUrl = urlParameters.getBytes(StandardCharsets.UTF_8);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postUrl);
        }

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK)
        {
            log.print("Error Response Code : " + responseCode + " - " + postData);
            log.logMessage("Error Saving Url : " + postData, "THID : " + threadID + " : Thread Status");
        }
        else
        {
            return getContent(con);
        }

        return string.emptyString;
    }

    /*Helper Method*/
    public void invokeScannerThread(Scanner scanner)
    {
        scanner.close();
    }


}
