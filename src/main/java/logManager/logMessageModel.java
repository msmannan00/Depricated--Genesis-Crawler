package logManager;

class logMessageModel
{
    /*Variable Initialization*/
    private final String messageType;
    private final String message;
    
    /*Initializations*/
    logMessageModel(String messageType, String message)
    {
        this.message = message;
        this.messageType = messageType;
    }
    
    /*Getter Setter Methods*/
    String getMessageType()
    {
        return messageType;
    }

    String getMessage()
    {
        return message;
    }
}
