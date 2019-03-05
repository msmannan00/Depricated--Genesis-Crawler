package Constants;

import Shared.webRequestHandler;

public class eventListner
{
    /*Event Identifiers*/
    private static boolean backupTrigger = false;

    /*Shared Instance*/
    private static final eventListner sharedInstance = new eventListner();

    /*Event Listners*/
    public static eventListner getInstance()
    {
        return sharedInstance;
    }
    
    public boolean getBackupState()
    {
        return backupTrigger;
    }

    public void setBackupState(boolean state)
    {
        backupTrigger = state;
    }
}
