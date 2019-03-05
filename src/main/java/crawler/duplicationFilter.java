package crawler;

import Shared.helperMethod;
import Constants.status;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import Shared.sessionManager;
import logManager.log;


@SuppressWarnings("ALL")
public class duplicationFilter
{

    /*Variable Declarations*/
    private Connection con;
    Map<String, Integer> hostCache;

    /*Shared Instance*/
    private static final duplicationFilter sharedInstance = new duplicationFilter();

    public static duplicationFilter getInstance()
    {
        return sharedInstance;
    }

    /*Initializations*/
    private duplicationFilter()
    {
        con = helperMethod.getSqlServerConnection();
        sessionManager.getInstance().createSession(this, "invokeClearCache", 1800000,true,null,null);
        hostCache = new HashMap<String, Integer>();
    }

    /*HELPER METHODS*/
    public boolean is_url_duplicate(String URLLink) throws URISyntaxException
    {

        if (!status.mySqlStatus)
        {
            return false;
        }
        String host = urlHelperMethod.getUrlHost(URLLink);
        String subHost = URLLink.substring(host.length());

        boolean hostFound = false;
        boolean subHostFound = false;

        byte[] hostByte = host.getBytes(StandardCharsets.UTF_8);
        byte[] subHostByte = subHost.getBytes(StandardCharsets.UTF_8);


        try
        {
            Statement stmt = con.createStatement();
            ResultSet rset;
            int hostId = -1;

            ResultSet resultSet;

            hostId = getHostId(con,host,hostByte);
            if(hostId!=-1) /*Host Found*/
            {
                hostFound = true;
                subHostFound = sqlQueryManager.getInstance().doSubhostExists(con,hostId,subHost,subHostByte);

            }

            if(!hostFound) /*No Host Found*/
            {
                hostId = sqlQueryManager.getInstance().createAndReturnHostId(con,hostByte);
                sqlQueryManager.getInstance().addSubURl(con,hostId,subHostByte);
            }
            else if(!subHostFound)
            {
                sqlQueryManager.getInstance().addSubURl(con,hostId,subHostByte);
            }
            
        }
        catch (SQLException e)
        {
            log.print("ERROR select id from host_url where host_url='" + hostByte + "'");
            log.print(host);
            e.printStackTrace();
        }
        return (hostFound && subHostFound);
    }

    /*HELPER METHODS*/
    public void invokeClearCache()
    {
        hostCache.clear();
    }

    private Integer getHostId(Connection con,String host,byte[] hostByte) throws SQLException
    {
        int hostId;
        if(hostCache.containsKey(host))
        {
            hostId = hostCache.get(host);
        }
        else
        {
            hostId = sqlQueryManager.getInstance().getHostId(con,hostByte);
            if(hostId != -1)
            {
                hostCache.put(host,hostId);
            }
        }
        return hostId;
    }

}