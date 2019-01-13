package crawler;

import application.helperMethod;
import constants.status;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import logManager.log;
import org.apache.commons.lang.StringUtils;

public class duplicationFilter
{

    public Character currentCharacter;
    Connection con = null;

    /*Shared Instance*/
    private static final duplicationFilter sharedInstance = new duplicationFilter();

    public static duplicationFilter getInstance()
    {
        return sharedInstance;
    }


    /*PRIVATE VARIABLES*/
    ArrayList<duplicationFilter> child = new ArrayList<duplicationFilter>();

    public duplicationFilter()
    {
        con = helperMethod.getCacheConnection();
    }

    /*INITIALIZATION*/
    public void Initialize() throws URISyntaxException
    {
    }

    /*HELPER METHODS*/
    public boolean is_url_duplicate(String URLLink) throws URISyntaxException
    {

        if (!status.mySqlStatus || StringUtils.countMatches("a.b.c.d", ".")>6)
        {
            return false;
        }
        
        
        String staticBytes = urlHelperMethod.getUrlWithoutParameters(URLLink);
        String dynamicBytes = URLLink.replace(staticBytes, "");

        byte staticPage[] = staticBytes.getBytes(StandardCharsets.UTF_8);
        byte dynamicPage[] = dynamicBytes.getBytes(StandardCharsets.UTF_8);

        try
        {
            Statement stmt = con.createStatement();
            ResultSet rset;
            int staticHostId = -1;

            String sql = "SELECT id FROM host_url WHERE host_url=? ";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setBytes(1, staticPage);
            rset = pstmt.executeQuery();
            if (rset.next())
            {
                if (dynamicBytes.length() <= 3)
                {
                    return true;
                }

                staticHostId = rset.getInt(1);

                ResultSet rset_sub = stmt.executeQuery("select sub_host from sub_url where fk_id=" + staticHostId);
                int counter_dynamic = 0;
                while (rset_sub.next())
                {
                    if (Arrays.equals(rset_sub.getBytes(1), dynamicPage) || counter_dynamic > 10 || dynamicBytes.equals(""))
                    {
                        return true;
                    }
                    counter_dynamic++;
                }
            }

            if (staticHostId == -1)
            {
                sql = "INSERT INTO host_url(host_url) VALUES(?)";

                pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setBytes(1, staticPage);

                int rowAffected = pstmt.executeUpdate();
                if (rowAffected == 1 && dynamicBytes.length() > 3)
                {
                    int candidateId = 0;
                    rset = pstmt.getGeneratedKeys();
                    if (rset.next())
                    {
                        candidateId = rset.getInt(1);
                        pstmt = con.prepareStatement("INSERT INTO sub_url VALUES(?,?)");
                        pstmt.setInt(1, candidateId);
                        pstmt.setBytes(2, dynamicPage);
                        pstmt.execute();
                    }
                }
            }
            else
            {
                pstmt = con.prepareStatement("INSERT INTO sub_url VALUES(?,?)");
                pstmt.setInt(1, staticHostId);
                pstmt.setBytes(2, dynamicPage);
                pstmt.execute();
            }
        }
        catch (SQLException e)
        {
            log.print("ERROR select id from host_url where host_url='" + staticPage + "'");
            log.print(staticBytes);
            e.printStackTrace();
        }
        return false;
    }

}
