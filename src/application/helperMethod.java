package application;

import constants.enumeration.logType;
import constants.preferences;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class helperMethod
{

    public static void removeTorInstances() throws IOException
    {
        if (!preferences.useTorFromOS)
        {
            Runtime.getRuntime().exec("kill $(lsof -t -i:8123)");
            Runtime.getRuntime().exec("sudo killall tor");
        }
    }

    public static String getCurrentDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM dd yyyy");
        Date date = new Date();
        return date.toString();
    }

    public static Point centreDimension(int frameWidth, int frameHeight)
    {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Point position = new Point();
        position.x = (int) ((dimension.getWidth() - frameWidth) / 2);
        position.y = (int) ((dimension.getHeight() - frameHeight) / 2) - 12;
        return position;
    }

    public static String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void writeObjectBackupToFile(Object serObj, String address)
    {
        FileHandler.writeObjectBackupToFile(serObj, address);
    }

    public static void writeObjectToFile(Object serObj, String address)
    {
        FileHandler.writeObjectToFile(serObj, address);
    }

    public static int getFileCount(String path)
    {
        return FileHandler.getFileCount(path);
    }

    public static Object readObjectFromFile()
    {
        return FileHandler.readObjectFromFile(preferences.filepath_queue_manager);
    }

    public static logType getErrorMessageType(String errorMessage)
    {
        if (errorMessage.contains("java.io.FileNotFoundException"))
        {
            return logType.warning;
        }
        else
        {
            return logType.error;
        }
    }

    public static Date addMinutesToDate(Date date, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        long t = cal.getTimeInMillis();
        date = new Date(t + (minutes * 60000));
        return date;
    }

    public static boolean isDeadlinePassed(Date date)
    {
        Date cDate = new Date();
        if (cDate.after(date))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static Connection getCacheConnection()
    {
        Connection con = null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;characterEncoding=utf-8;databaseName=TorCrawler;user=msmannan00;password=imammehdi0");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        return con;
    }
}
