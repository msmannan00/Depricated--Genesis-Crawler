package crawler;

import java.io.Serializable;
import java.util.ArrayList;

public class duplicationFilter implements Serializable
{

    public Character currentCharacter;

    /*PRIVATE VARIABLES*/
    ArrayList<duplicationFilter> child = new ArrayList<duplicationFilter>();

    public duplicationFilter()
    {
    }

    /*INITIALIZATION*/
    public void Initialize()
    {
        is_url_duplicate(0, "b");
    }

    /*HELPER METHODS*/
    public boolean is_url_duplicate(int index, String URLLink)
    {
        /*BASE CONDITION : LINK ALREADY FOUND*/
        if (index >= URLLink.length())
        {
            return true;
        }

        char urlChar = URLLink.charAt(index);
        for (int e = 0; e < child.size(); e++)
        {
            if (child.get(e).currentCharacter == urlChar)
            {
                return child.get(e).is_url_duplicate(index + 1, URLLink);
            }
        }

        addUrl(0, URLLink.substring(index));
        return false;
    }

    private void addUrl(int index, String URLLink)
    {
        /*BASE CONDITION*/
        if (index >= URLLink.length())
        {
            return;
        }

        /*NEW NODE INITIALIZATION*/
        char urlCharacter = URLLink.charAt(index);
        duplicationFilter filterNode = new duplicationFilter();
        filterNode.currentCharacter = urlCharacter;
        child.add(filterNode);
        filterNode.addUrl(index + 1, URLLink);
    }

}
