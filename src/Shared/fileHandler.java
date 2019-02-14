package Shared;

import Constants.preferences;
import Constants.string;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.sun.org.apache.xpath.internal.operations.Bool;
import crawler.crawler;
import logManager.log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.jsoup.Jsoup;

public class fileHandler
{

    /*Helper Methods*/
    public static void removeFile(String FilePath)
    {
        File file = new File(FilePath);
        file.delete();
    }

    public static void clearFile(String path) throws FileNotFoundException, IOException
    {
        try (PrintWriter writer = new PrintWriter(path))
        {
            writer.print("");
        }
    }
 
    public static void clearDirectory(String path) throws FileNotFoundException, IOException
    {
        File file = new File(path);
        FileUtils.cleanDirectory(file);
    }

    public static void appendFile(String FilePath, String content) throws IOException, MalformedURLException, MalformedURLException, URISyntaxException, URISyntaxException, URISyntaxException
    {
        File file = new File(FilePath);

        if (!file.exists())
        {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, true);
        try (BufferedWriter bw = new BufferedWriter(fw))
        {
            bw.write(content);
        }
    }

    public static void copyDirectory(File src, File dest) throws IOException
    {
        if (dest.exists())
        {
            return;
        }

        if (src.isDirectory())
        {

            //if directory not exists, create it
            dest.mkdir();

            //list all the directory contents
            String files[] = src.list();

            for (String file : files)
            {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyDirectory(srcFile, destFile);
            }

        }
        else
        {
            OutputStream out;
            try ( //if file, then copy it
            //Use bytes stream to support all file types
                    InputStream in = new FileInputStream(src))
            {
                out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                //copy the file content in bytes
                while ((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            out.close();
        }
    }

    public static Map<String, Boolean> readDictFromFile()
    {
        Map<String, Boolean> dict = new HashMap<String, Boolean>();
        try (BufferedReader br = new BufferedReader(new FileReader(string.dictionary))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                dict.put(line,true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.print("DICT LOADED");
        return dict;
    }

    public static String readCatagoryFromFile(String path)
    {
        String list = new String();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                list += " " + line ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void writeObjectBackupToFile(Object serObj, String address)
    {
        File files_in_directory = new File("queue_backup//");
        if (files_in_directory.list().length - 1 > preferences.max_backup_files_count)
        {
            File[] files = files_in_directory.listFiles();
            files[0].delete();
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        }

        writeObjectToFile(serObj,address);
    }

    public static void writeObjectToFile(Object serObj, String address)
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream(string.filepath_queue_manager);
            ObjectOutputStream objectOut;
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static int getFileCount(String path)
    {
        int count = 0;
        try (Stream<Path> files = Files.list(Paths.get(path)))
        {
            count = (int) files.count();
        }
        catch (IOException ex)
        {
            Logger.getLogger(helperMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public static Object readObjectFromFile(String file_path)
    {
        try
        {
            if (!new File(file_path).exists())
            {
                return new crawler();
            }

            FileInputStream fileIn = new FileInputStream(file_path);
            ObjectInputStream objectIn;
            objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            objectIn.close();
            return obj;

        }
        catch (IOException | ClassNotFoundException ex)
        {
            ex.printStackTrace();
            return new crawler();
        }
    }

    public static ArrayList readClassifierData(String filepath) throws FileNotFoundException
    {
        ArrayList<String> list;
        try (Scanner s = new Scanner(new File(filepath + ".txt")))
        {
            list = new ArrayList<String>();
            while (s.hasNext())
            {
                list.add(s.next());
            }
        }
        return list;
    }

    public static void htmlFileToText()
    {
        try
        {
            String html;
            try (Scanner s = new Scanner(new File("html_sample.txt")))
            {
                html = "";
                while (s.hasNext())
                {
                    html += s.next();
                }
            }
            html = Jsoup.parse(html).body().text();
            html = html.replaceAll("[^a-zA-Z0-9]", " ");
            html = html.replaceAll("\\s{2,}", " ").trim();
            log.print(html);

        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(fileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static ArrayList<String> readQueueStack() throws IOException
    {
        ArrayList<String> backedURLList = new ArrayList<String>();
        int rowLength = 2000;
        //Initial write position
        try (RandomAccessFile raf = new RandomAccessFile(string.url_stack, "rw"))
        {
            //Initial write position
            long writePosition = raf.getFilePointer();
            for (int e = 0; e < rowLength; e++)
            {
                String tempData = raf.readLine();
                if (tempData != null)
                {
                    backedURLList.add(tempData);
                }
            }
            // Shift the next lines upwards.
            long readPosition = raf.getFilePointer();
            
            byte[] buff = new byte[1024];
            int n;
            while (-1 != (n = raf.read(buff)))
            {
                raf.seek(writePosition);
                raf.write(buff, 0, n);
                readPosition += n;
                writePosition += n;
                raf.seek(readPosition);
            }
            raf.setLength(writePosition);
        }

        return backedURLList;
    }
}
