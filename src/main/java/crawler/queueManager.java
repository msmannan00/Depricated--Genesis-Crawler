package crawler;

import Constants.enumeration;
import Constants.preferences;
import Constants.string;
import logManager.log;

import java.io.Serializable;
import java.util.*;

class queueManager implements Serializable
{

    /*LOCAL VARIABLES*/
    private int onionQueueSize;
    private int parsingQueueSize;

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
        onionQueueSize = 1;
    }

    private void variable_initialization() {
        onionQueues = new HashMap<String, Queue<urlModel>>();
        onionDataQueues = new HashMap<String, Queue<urlModel>>();
        baseQueues = new HashMap<String, Queue<urlModel>>();
        parsingQueues = new HashMap<String, Queue<urlModel>>();

        onionQueuesKeys = new ArrayList<>();
        onionDataQueuesKeys = new ArrayList<>();
        baseQueuesKeys = new ArrayList<>();
    }

    public void parsingKeysInitialization()
    {
        if(parsingQueues.keySet().size()>0)
        {
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(parsingQueues.keySet());
            parsingQueuesKeys = temp;
        }
    }

    public void urlInitialization()
    {
        setUrl(string.baseLink, new urlModel("",0,enumeration.UrlDataTypes.all));
        parsingKeysInitialization();
    }

    String getKey()
    {
        String host = "";
        /*IF REMOVED THREADS WILL COME IN EVEN SYNCRONIZED*/
        if (parsingQueuesKeys!=null && parsingQueuesKeys.size() > 0)
        {
            host = parsingQueuesKeys.get(0);
            parsingQueuesKeys.remove(0);
        }
        else if (onionQueuesKeys.size() > 0)
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

        return host;

    }

    /*METHOD UPDATE QUEUES AS NEW URL IS FOUND*/
    urlModel getUrl(String host) {
        String URL = string.emptyString;
        int depth = 1;
        enumeration.UrlDataTypes urlTypes = enumeration.UrlDataTypes.all;

        try
        {
            synchronized (this)
            {
                if (parsingQueues.containsKey(host))
                {
                    urlModel tempModel = parsingQueues.get(host).poll();

                    URL = tempModel.getURL();
                    depth = tempModel.getDepth();
                    urlTypes = tempModel.getCatagory();
                    parsingQueueSize-=1;
                }
                return new urlModel(host + URL,depth,urlTypes);
            }
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public void removeFromParsingQueues(String host)
    {
        if (parsingQueues!=null && !parsingQueues.isEmpty() && parsingQueues.get(host)!=null && parsingQueues.get(host).isEmpty() && !host.equals(string.emptyString))
        {
            parsingQueues.remove(host);
        }
    }

    private void addToParsingQueues(HashMap<String, Queue<urlModel>> queue, String host)
    {
        onionQueueSize -=queue.get(host).size();
        parsingQueueSize += queue.get(host).size();
        parsingQueues.put(host, queue.get(host));
        queue.remove(host);
    }

    public int getUrlDepth(String host, urlModel parentURL)
    {
        if(!urlHelperMethod.getUrlHost(parentURL.getURL()).equals(host))
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
            //log.print("1 adding url : " + subUrl);
            int depth = getUrlDepth(host,parentURL);
            priorityQueue.get(host).add(new urlModel(subUrl,depth,parentURL.getCatagory()));
            onionQueueSize++;
        }
        else if (parsingQueues.containsKey(host))
        {
            //log.print("2 adding url : " + subUrl);
            parsingQueueSize+=1;
            int depth = getUrlDepth(host,parentURL);
            parsingQueues.get(host).add(new urlModel(subUrl,depth,parentURL.getCatagory()));
        }
        else
        {
            Queue<urlModel> tempList = new LinkedList();

            if(parentURL.getCatagory().equals(enumeration.UrlDataTypes.finance) || parentURL.getCatagory().equals(enumeration.UrlDataTypes.news))
            {
                //log.print("3 adding url : " + subUrl);
                tempList.add(new urlModel(subUrl,parentURL.getDepth()+1,parentURL.getCatagory()));
                onionQueueSize++;
            }
            else if(subUrl.length()<=0 || (host+subUrl).equals(string.baseLink))
            {
                //log.print("4 adding url : " + subUrl);
                tempList.add(new urlModel(subUrl,1,parentURL.getCatagory()));
                onionQueueSize++;
            }
            else
            {
                //log.print("5 adding url : " + subUrl + " -> " + host);
                tempList.add(new urlModel(host,1,parentURL.getCatagory()));
                tempList.add(new urlModel(subUrl,preferences.maxUrlDepth-1,parentURL.getCatagory()));
                onionQueueSize+=2;
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
    }

    /*Helper Method*/

    boolean isHostEmpty(String host)
    {
        return host.length()<=0 || !parsingQueues.containsKey(host) || parsingQueues.get(host).size()<=0;
    }

    int size()
    {
        return onionQueueSize + parsingQueueSize;
    }

    int queueSize()
    {
        return onionQueuesKeys.size() + onionDataQueuesKeys.size() + baseQueuesKeys.size();
    }

    int getOnionQueuesSize()
    {
        return onionQueueSize-1;
    }

    int getParsingQueuesSize()
    {
        return parsingQueueSize;
    }

    public boolean hasHostBackupLimitReached(String host)
    {
        host = urlHelperMethod.getUrlHost(host);
        boolean parseHostExists = parsingQueues.containsKey(host);
        boolean onionHostExists = onionQueues.containsKey(host);

        if(parseHostExists && parsingQueues.get(host).size()>=preferences.maxQueueSize)
        {
            return true;
        }
        else if(onionHostExists && onionQueues.get(host).size()>=preferences.maxQueueSize)
        {
            return true;
        }
        else if((onionQueuesKeys.size() + parsingQueues.keySet().size())>=preferences.maxThreadCount*4 && !parseHostExists && !onionHostExists)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getOnionThreads()
    {
        return onionQueues.keySet().size();
    }

    public int getParsingThreads()
    {
        return parsingQueues.keySet().size();
    }

    public String priorityQueueLogs()
    {
        String logs = "";

        for (String link : parsingQueues.keySet())
        {
            logs += link + " : " + parsingQueues.get(link).size() + "<br>";
        }

        return logs;
    }

    public String onionQueueLogs()
    {
        String logs = string.emptyString;
        Set<String> keyset = onionQueues.keySet();

        for (String link : keyset)
        {
            logs += link + " : " + onionQueues.get(link).size() + "<br>";
        }

        return logs;
    }

}
