package constants;

import java.util.ArrayList;

public class language
{
    /*Shared Instance*/
    private static final language sharedInstance = new language();
    int index = 0;
        /*index == 0 than urdu*/
        /*index == 1 than english*/
    
    /*All Used Words*/
    ArrayList<String> hello = new ArrayList<String>();
    
    
    public static language getInstance()
    {
        return sharedInstance;
    }
    
    public void setLanguage(int index)
    {
       this.index = index; 
    }
    
    public void getWord()
    {
        
    }
    
}
