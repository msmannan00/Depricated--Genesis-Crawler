package logManager;

import constants.enumeration;
import constants.eventListner;
import constants.status;
import java.awt.Color;

public class logEvents
{

    /*Shared Instance*/
    private static final logEvents sharedInstance = new logEvents();
    private logViewController view;

    public static logEvents getInstance()
    {
        return sharedInstance;
    }

    public void Initialize(logViewController view)
    {
        this.view = view;
    }

    public void onCreateBackup()
    {
        eventListner.setBackupState(true);
    }

    public void onRestart()
    {
        eventListner.setBackupState(true);
    }

    public void onClearLogs()
    {
        view.jSystemProgressPane.setText("");
        view.jServerErrorPane.setText("");
        view.jUrlFoundPane.setText("");
        view.jWarningPane.setText("");
        view.crawlerObject.clearQueues();
    }

    public void onUpdateLogs()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                view.pausedThread.setText("  " + logModel.getInstance().getPausedThread());
                view.runningThread.setText("  " + logModel.getInstance().getRunningThread());
                view.jCurrentUrlFound.setText("  " + view.crawlerObject.size());
                view.jStatus.setText("  " + status.appStatus);
                if (eventListner.getBackupState())
                {
                    setButtonState(false);
                }
                else
                {
                    setButtonState(true);
                    if(status.appStatus == enumeration.appStatus.paused)
                    {
                        view.jPauseBtn.setEnabled(false);
                        view.jStartBtn.setEnabled(true);
                    }
                    else if(status.appStatus == enumeration.appStatus.running)
                    {
                        view.jPauseBtn.setEnabled(true);
                        view.jStartBtn.setEnabled(false);
                    }
                }
            }
        }.start();
    }

    public void setButtonState(boolean state)
    {
        view.jBackupBtn.setEnabled(state);
        view.jClearBtn.setEnabled(state);
        view.jPauseBtn.setEnabled(state);
        view.jPreferenceBtn.setEnabled(state);
        view.jRestartBtn.setEnabled(state);
        view.jStartBtn.setEnabled(state);
        view.jUpdateLogBtn.setEnabled(state);
    }

    public void onPauseCrawler()
    {
        status.appStatus = enumeration.appStatus.paused;
        view.jStartBtn.setBackground(new Color(240, 240, 240));
        view.jPauseBtn.setBackground(Color.green);
    }

    public void onStartCrawler()
    {
        status.appStatus = enumeration.appStatus.running;
        view.jPauseBtn.setBackground(new Color(240, 240, 240));
        view.jStartBtn.setBackground(Color.green);
    }
}
