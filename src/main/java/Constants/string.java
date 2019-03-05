package Constants;

public class string
{

    /*Network*/
    public static String proxyIP = "127.0.0.1";
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:63.0) Gecko/20100101 Firefox/63.0";
    public static String baseLink = "http://hss3uro2hsxfogfq.onion/index.php?q=onion+links&session=ylo%2BaWe53pinhJ19IIYhijYXOowAu%2BUA6OGB4zYxixY%3D&numRows=20&hostLimit=20&template=0";
    //public static String baseLink = "http://msydqstlz2kzerdg.onion/onions/";

    /*Constants*/
    public static String emptyString = "";
    public static String none = "none";
    public static String typeOnion = ".onion";
    public static String textOnion = "onion";
    public static String datePattern = "yyyy_MM_dd_HH_mm_ss";

    /*Regular Expressions*/
    public static String urlHostMacherRegex = "(https?://)([^:^/]*)(:\\d*)?(.*)?";
    public static String isNumeric = "-?\\d+(\\.\\d+)?";

    /*Directories*/
    public static String filepath_queue_manager = "queue_manager";
    public static String filepath_queue_manager_backup = "queue_backup//queue_manager_";
    public static String logDirectory = "Logs/";
    public static String dictionary = "Dictionary/wordlist.txt";
    public static String url_stack = "url_stack.txt";
    public static String financial_file = "CatagorizedURL/finance.txt";
    public static String news_file = "CatagorizedURL/news.txt";


    /*SQLConnections*/
    public static String connectionString = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static String driverString = "jdbc:sqlserver://localhost:1433;characterEncoding=utf-8;databaseName=TorCrawler;user=toor;password=toor";

    /*Post Data Strings*/
    public static String postUrl = "http://boogle.store/update_cache";
}
