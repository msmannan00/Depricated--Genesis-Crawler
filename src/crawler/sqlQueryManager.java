package crawler;

import logManager.log;

import java.sql.*;

@SuppressWarnings("SqlNoDataSourceInspection")
public class sqlQueryManager {
    private static sqlQueryManager ourInstance = new sqlQueryManager();

    public static sqlQueryManager getInstance() {
        return ourInstance;
    }

    private sqlQueryManager() {
    }

    public int getHostId(Connection con, byte[] hostByte) throws SQLException
    {
        String sql = "SELECT id FROM host_url WHERE host_url=? ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setBytes(1, hostByte);
        ResultSet rset = pstmt.executeQuery();
        if (rset.next())
        {
            return rset.getInt(1);
        }
        else
        {
            return -1;
        }
    }

    public boolean doSubhostExists(Connection con, int hostId,String subHost,byte[] subHostByte) throws SQLException
    {
        if(subHost.length()>0)
        {
            String sql = "SELECT Top 1 fk_id FROM sub_url WHERE fk_id=? AND sub_host=?";

            PreparedStatement prepareStatement = con.prepareStatement(sql);
            prepareStatement.setInt(1, hostId);
            prepareStatement.setBytes(2, subHostByte);
            ResultSet resultSet = prepareStatement.executeQuery();

            if (resultSet.next())
            {
                return true;
            }
            return false;
        }
        return true;
    }

    protected void addSubURl(Connection con, int hostId, byte[] subHostByte) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement("INSERT INTO sub_url VALUES(?,?)");
        prepareStatement.setInt(1, hostId);
        prepareStatement.setBytes(2, subHostByte);
        prepareStatement.execute();
        prepareStatement.close();
    }

    public int createAndReturnHostId(Connection con, byte[] hostByte) throws SQLException
    {
        String sql = "INSERT INTO host_url(host_url) VALUES(?)";

        PreparedStatement prepareStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        prepareStatement.setBytes(1, hostByte);
        int rowAffected = prepareStatement.executeUpdate();
        if (rowAffected == 1)
        {
            ResultSet resultSet = prepareStatement.getGeneratedKeys();
            if (resultSet.next())
            {
                return resultSet.getInt(1);
            }
        }
        prepareStatement.close();
        return -1;
    }

}
