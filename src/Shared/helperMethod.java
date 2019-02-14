package Shared;

import Constants.preferences;
import Constants.string;
import logManager.log;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class helperMethod
{

    public static String getCurrentDate()
    {
        Date date = new Date();
        return date.toString();
    }

    public static Point getCentreDimension(int frameWidth, int frameHeight)
    {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Point position = new Point();
        position.x = (int) ((dimension.getWidth() - frameWidth) / 2);
        position.y = (int) ((dimension.getHeight() - frameHeight) / 2) - 12;
        return position;
    }

    public static String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat(string.datePattern);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void createBackup(Object serObj, String address)
    {
        fileHandler.writeObjectToFile(serObj, address);
        fileHandler.writeObjectBackupToFile(serObj, address);
    }

    public static Object readObjectFromFile()
    {
        return fileHandler.readObjectFromFile(string.filepath_queue_manager);
    }

    public static String trimString(String str,int size)
    {
        if (str.length() > 1050)
        {
            str = str.substring(0, 1050);
        }
        return str;
    }

    public  static boolean isAlpha(String word)
    {
        if(! Pattern.matches(".*[a-zA-Z]+.*", word))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static String removeSymbolsAtEnd(String str,String symbol)
    {
        if (str.contains(symbol))
        {
            int index = str.indexOf(symbol);
            if (str.length() > index + 2 && Character.isLetter(str.charAt(index + 2)))
            {
                return str.substring(0, index + 2) + (str.charAt(index + 2) + "").toUpperCase() + str.substring(index + 3).replaceAll("\\|", " ");
            }
            else
            {
                return str.substring(0, index + 2) + str.substring(index + 2).replaceAll("\\|", "");
            }
        }
        return str;
    }
        public static String toCapital(String str)
    {
        if (str.length() > 0)
        {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        else
        {
            return "";
        }
    }

    public static Date addMinutesToDate(Date date, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        long t = cal.getTimeInMillis();
        date = new Date(t + (minutes * 60000));
        return date;
    }

    public static boolean hasDeadlinePassed(Date date)
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

    public static void initializeNetworkSettings()
    {
        System.setProperty("http.agent", string.userAgent);
        System.setProperty("http.maxRedirects", "5");
    }

    public static boolean isNumeric(String str)
    {
        return str.matches(string.isNumeric);  //match a number with optional '-' and decimal.
    }

    public static Connection getSqlServerConnection()
    {
        Connection con = null;
        try
        {
            Class.forName(string.connectionString);
            con = DriverManager.getConnection(string.driverString);
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        return con;
    }

    public static Class[] createScannerFunctionParameter()
    {
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = Scanner.class;
        return  parameterTypes;
    }

}
