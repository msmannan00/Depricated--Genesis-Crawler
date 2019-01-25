package crawler;

import Constants.enumeration;
import Constants.preferences;
import Constants.string;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

class queueManager implements Serializable
{

    /*LOCAL VARIABLES*/
    private int size;

    /*URL QUEUES*/
    private HashMap<String, Queue<urlModel>> onionQueues;
    private HashMap<String, Queue<urlModel>> onionDataQueues;
    private HashMap<String, Queue<urlModel>> baseQueues;
    private HashMap<String, Queue<urlModel>> parsingQueues;

    /*URL QUEUES KEYS*/
    private ArrayList<String> onionQueuesKeys;
    private ArrayList<String> onionDataQueuesKeys;
    private ArrayList<String> baseQueuesKeys;
    private ArrayList<String> parsingQueuesKeys;

    /*INITIALIZATIONS*/
    queueManager() {
        variable_initialization();
        size = 1;
    }

    private void variable_initialization() {
        onionQueues = new HashMap<String, Queue<urlModel>>();
        onionDataQueues = new HashMap<String, Queue<urlModel>>();
        baseQueues = new HashMap<String, Queue<urlModel>>();
        parsingQueues = new HashMap<String, Queue<urlModel>>();

        onionQueuesKeys = new ArrayList<>();
        onionDataQueuesKeys = new ArrayList<>();
        baseQueuesKeys = new ArrayList<>();
        parsingQueuesKeys = new ArrayList<>();

        setUrl(string.baseLink, new urlModel("",0));

    }

    String getKey() {
        String host = "";
        /*IF REMOVED THREADS WILL COME IN EVEN SYNCRONIZED*/
        if (onionQueuesKeys.size() > 0)
        {
            host = onionQueuesKeys.get(0);
            addToParsingQueues(onionQueues, host);
            onionQueuesKeys.remove(0);
        }
        else if (onionDataQueuesKeys.size() > 0)
        {
            host = onionDataQueuesKeys.get(0);
            addToParsingQueues(onionDataQueues, host);
            onionDataQueuesKeys.remove(0);
        }
        else if (baseQueuesKeys.size() > 0)
        {
            host = baseQueuesKeys.get(0);
            addToParsingQueues(baseQueues, host);
            baseQueuesKeys.remove(0);
        }
        else if (parsingQueuesKeys.size() > 0)
        {
            host = parsingQueuesKeys.get(0);
            parsingQueuesKeys.remove(0);
        }


        return host;

    }

    /*METHOD UPDATE QUEUES AS NEW URL IS FOUND*/
    urlModel getUrl(String host) {
        String URL = string.emptyString;

        try
        {
            synchronized (this)
            {
                if (parsingQueues.containsKey(host))
                {
                    urlModel tempModel = parsingQueues.get(host).poll();

                    URL = tempModel.getURL();
                    removeFromParsingQueues(parsingQueues, host);
                    size--;
                }
                return new urlModel(host + URL,1);
            }
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private void removeFromParsingQueues(HashMap<String, Queue<urlModel>> queue, String host)
    {
        if (queue.get(host).isEmpty())
        {
            queue.remove(host);
        }
    }

    private void addToParsingQueues(HashMap<String, Queue<urlModel>> queue, String host)
    {
        parsingQueues.put(host, queue.get(host));
        queue.remove(host);
    }

    public int getUrlDepth(String host, urlModel parentURL)
    {
        if(urlHelperMethod.getUrlHost(parentURL.getURL()).equals(host))
        {
            return preferences.maxUrlDepth;
        }
        else
        {
            return parentURL.getDepth()+1;
        }
    }

    private void addToQueue(HashMap<String, Queue<urlModel>> priorityQueue, String host, String subUrl, urlModel parentURL)
    {
        if (priorityQueue.containsKey(host))
        {
            int depth = getUrlDepth(host,parentURL);
            priorityQueue.get(host).add(new urlModel(subUrl,depth));
        }
        else if (parsingQueues.containsKey(host))
        {
            int depth = getUrlDepth(host,parentURL);
            parsingQueues.get(host).add(new urlModel(subUrl,depth));
        }
        else
        {
            Queue<urlModel> tempList = new LinkedList();
            if(subUrl.length()<=0 || (host+subUrl).equals(string.baseLink))
            {
                tempList.add(new urlModel(subUrl,1));
            }
            else
            {
                tempList.add(new urlModel(subUrl,3));
                tempList.add(new urlModel(host,1));
                size++;
            }
            priorityQueue.put(host, tempList);
        }
    }

    /*CHECK URL DEPTH TO LIMIT TREE HEIGHT SO THAT CRAWLER DOENST DIVERT INTO URLS THAT DONT CONTAIN INFORMATION REGARDING ONION LINKS*/
    void setUrl(String URLLink, urlModel pModel) {
        enumeration.UrlTypes type = urlHelperMethod.getNetworkType(URLLink);

        String host = urlHelperMethod.getUrlHost(URLLink);
        String subUrl = urlHelperMethod.getSubUrl(URLLink);
        if (type == enumeration.UrlTypes.onion)
        {
            addToQueue(onionQueues, host, subUrl, pModel);
            if (!onionQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                onionQueuesKeys.add(host);
            }
        }
        else if (type == enumeration.UrlTypes.base && URLLink.contains(string.textOnion))
        {
            addToQueue(onionDataQueues, host, subUrl, pModel);
            if (!onionDataQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                onionDataQueuesKeys.add(host);
            }
        }
        else
        {
            addToQueue(baseQueues, host, subUrl, pModel);
            if (!baseQueuesKeys.contains(host) && !parsingQueues.containsKey(host))
            {
                baseQueuesKeys.add(host);
            }
        }
        size += 1;
    }

    /*Helper Method*/

    boolean isHostEmpty(String host)
    {
        return host.length()<=0 || !parsingQueues.containsKey(host);
    }

    int size()
    {
        return size;
    }

    int queueSize()
    {
        return onionQueuesKeys.size() + onionDataQueuesKeys.size() + baseQueuesKeys.size();
    }



}
