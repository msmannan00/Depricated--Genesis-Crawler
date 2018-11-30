package constants;

import application.webRequestHandler;

public class eventListner
{
    /*Event Listners*/
    private static boolean backupTrigger = false;

    /*Shared Instance*/
    private static final webRequestHandler sharedInstance = new webRequestHandler();

    public static webRequestHandler getInstance()
    {
        return sharedInstance;
    }
    
    public static boolean getBackupState()
    {
        return backupTrigger;
    }

    public static void setBackupState(boolean state)
    {
        backupTrigger = state;
    }
}
