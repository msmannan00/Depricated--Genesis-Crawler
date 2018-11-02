package crawler;

import constants.enumeration;
import constants.string;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class queueManager
{

    /*URL QUEUES*/
    private HashMap<String, Queue<urlModel>> onionQueues;
    private HashMap<String, Queue<urlModel>> onionDataQueues;
    private HashMap<String, Queue<urlModel>> baseQueues;
    private HashMap<String, Queue<urlModel>> parsingQueues;

    /*URL QUEUES KEYS*/
    private ArrayList<String> onionQueuesKeys;
    private ArrayList<String> onionDataQueuesKeys;
    private ArrayList<String> baseQueuesKeys;

    /*VARIABLE INITIALIZATION*/

    /*INITIALIZATIONS*/
    public queueManager() throws IOException
    {
        variable_initalization();
    }

    private void variable_initalization() throws IOException
    {
        onionQueues = new HashMap<String, Queue<urlModel>>();
        onionDataQueues = new HashMap<String, Queue<urlModel>>();
        baseQueues = new HashMap<String, Queue<urlModel>>();
        parsingQueues = new HashMap<String, Queue<urlModel>>();

        onionQueuesKeys = new ArrayList<String>();
        onionDataQueuesKeys = new ArrayList<String>();
        baseQueuesKeys = new ArrayList<String>();

        setUrl(string.baseLink,"");
    }

    public boolean isHostEmpty(String host)
    {
        return !parsingQueues.containsKey(host);
    }

    public int size()
    {
        return parsingQueues.size() + onionQueues.size() + onionDataQueues.size() + baseQueues.size();
    }

    public boolean isUrlPresent()
    {
        if (parsingQueues.size() > 0 || onionQueues.size() > 0 || onionDataQueues.size() > 0 || baseQueues.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getKey() throws InterruptedException
    {
        synchronized (this)
        {
            String host = "";
            /*IF REMOVED THREADS WILL COME IN EVEN SYNCRONIZED*/
            if (onionQueuesKeys.size() > 0)
            {
                host = onionQueuesKeys.get(0);
                moveToParsedQueues(onionQueues, host);
                onionQueuesKeys.remove(0);
            }
            else if (onionDataQueuesKeys.size() > 0)
            {
                host = onionDataQueuesKeys.get(0);
                moveToParsedQueues(onionDataQueues, host);
                onionDataQueuesKeys.remove(0);
            }
            else if (baseQueuesKeys.size() > 0)
            {
                host = baseQueuesKeys.get(0);
                moveToParsedQueues(baseQueues, host);
                baseQueuesKeys.remove(0);
            }
            return host;
        }
    }

    /*METHOD UPDATE QUEUES AS NEW URL IS FOUND*/
    public urlModel getUrl(String host) throws InterruptedException
    {
        String URL = "";
        String parentURL = "";

        try
        {
            synchronized (this)
            {
                if (parsingQueues.containsKey(host))
                {
                    urlModel tempModel = parsingQueues.get(host).poll();
                    URL = tempModel.getURL();
                    parentURL = tempModel.getParentURL();
                    removeHostIfParsed(parsingQueues, host);
                }
                return new urlModel(parentURL,host + URL);
            }
        }
        catch (Exception ex)
        {

            return null;
        }
    }

    public void removeHostIfParsed(HashMap<String, Queue<urlModel>> queue, String host)
    {
        if (queue.get(host).isEmpty())
        {
            queue.remove(host);
        }
    }

    public void moveToParsedQueues(HashMap<String, Queue<urlModel>> queue, String host)
    {
        parsingQueues.put(host, queue.get(host));
        queue.remove(host);
    }

    public void addToQueue(HashMap<String, Queue<urlModel>> priorityQueue, String host, String subUrl,String parentURL)
    {
        subUrl = subUrl + " ";
        if (priorityQueue.containsKey(host))
        {
            priorityQueue.get(host).add(new urlModel(parentURL,subUrl));
        }
        else
        {
            if (parsingQueues.containsKey(host))
            {
                parsingQueues.get(host).add(new urlModel(parentURL,subUrl));
            }
            else
            {
                Queue<urlModel> tempList = new LinkedList();
                tempList.add(new urlModel(parentURL,subUrl));
                priorityQueue.put(host, tempList);
            }
        }
    }

    /*CHECK URL DEPTH TO LIMIT TREE HEIGHT SO THAT CRAWLER DOENST DIVERT INTO URLS THAT DONT CONTAIN INFORMATION REGARDING ONION LINKS*/
    public void setUrl(String URLLink,String parentURL) throws IOException
    {
        enumeration.UrlTypes type = urlHelperMethod.getNetworkType(URLLink);

        String host = urlHelperMethod.getUrlHost(URLLink);
        String subUrl = urlHelperMethod.getSubUrl(URLLink);

        if (type == enumeration.UrlTypes.onion)
        {
            addToQueue(onionQueues, host, subUrl,parentURL);
            if (!onionQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                onionQueuesKeys.add(host);
            }
        }
        else if (type == enumeration.UrlTypes.base && URLLink.contains("onion"))
        {
            addToQueue(onionDataQueues, host, subUrl,parentURL);
            if (!onionDataQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                onionDataQueuesKeys.add(host);
            }
        }
        else
        {
            addToQueue(baseQueues, host, subUrl,parentURL);
            if (!baseQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                baseQueuesKeys.add(host);
            }
        }
    }
}
