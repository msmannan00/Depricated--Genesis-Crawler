package application;

import constants.preferences;
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class fileHandler
{

    /*Helper Methods*/
    public static void removeFile(String FilePath)
    {
        File file = new File(FilePath);
        file.delete();
    }

    public static void clearFile(String path) throws FileNotFoundException
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

}
