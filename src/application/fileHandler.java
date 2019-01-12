package application;

import constants.enumeration;
import constants.preferences;
import constants.string;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
        File file = new File(path);
        FileUtils.cleanDirectory(file);
    }

    public static void clearLogFile(String path) throws FileNotFoundException, IOException
    {
        PrintWriter writer = new PrintWriter(path);
        writer.print("");
        writer.close();
    }

    public static void appendFile(String FilePath, String content) throws IOException, MalformedURLException, MalformedURLException, URISyntaxException, URISyntaxException, URISyntaxException
    {
        File file = new File(FilePath);

        if (!file.exists())
        {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
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
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes 
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    static int classifierCount = 1;

    public static void saveClassifierData(String html, String title, enumeration.classificationType classifier)
    {
        try
        {
            html = html.replaceAll("[^a-zA-Z0-9]", " ");
            html = html.replaceAll("\\s{2,}", " ").trim();

            String content = title + " " + html;

            File file = new File("classifiers/" + classifier + "/class_" + classifierCount + ".csv");

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            classifierCount += 1;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

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

        try
        {
            FileOutputStream fileOut = new FileOutputStream(address);
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

    public static void writeObjectToFile(Object serObj, String address)
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream(address);
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
                return null;
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
            return null;
        }
    }

    public static ArrayList readClassifierData(String filepath) throws FileNotFoundException
    {
        Scanner s = new Scanner(new File(filepath + ".txt"));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext())
        {
            list.add(s.next());
        }
        s.close();
        return list;
    }

    public static void htmlFileToText()
    {
        try
        {
            Scanner s = new Scanner(new File("html_sample.txt"));
            String html = "";
            while (s.hasNext())
            {
                html += s.next();
            }
            s.close();
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

    public static String readQueueStack() throws IOException
    {
        int rowLength = preferences.maxQueueSize - preferences.minQueueSize;
        String data = "";
        RandomAccessFile raf = new RandomAccessFile(string.url_stack, "rw");
        //Initial write position                                             
        long writePosition = raf.getFilePointer();
        for (int e = 0; e < rowLength; e++)
        {
            String tempData = raf.readLine();
            if (tempData != null)
            {
                data += tempData + "\n";
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
        raf.close();

        return data;
    }
}
