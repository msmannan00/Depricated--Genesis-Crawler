package logManager;

import Application.webCrawler;
import Constants.enumeration;
import Constants.eventListner;
import Constants.status;
import java.awt.Color;

import static java.lang.Thread.sleep;

public class logEvents
{

    /*Shared Instance*/
    private static final logEvents sharedInstance = new logEvents();
    private logViewController view;

    public static logEvents getInstance()
    {
        return sharedInstance;
    }

    void Initialize(logViewController view)
    {
        this.view = view;
    }

    void onCreateBackup()
    {
        eventListner.getInstance().setBackupState(true);
    }

    void onRestart()
    {
        eventListner.getInstance().setBackupState(true);
    }

    void onClearLogs()
    {
        view.jSystemProgressPane.setText("");
        view.jServerErrorPane.setText("");
        view.jUrlFoundPane.setText("");
        view.jWarningPane.setText("");
    }

    int sleepCounter=0;
    void onUpdateLogs()
    {
        new Thread(() -> {
            try {
                view.pausedThread.setText("  " + logModel.getInstance().getPausedThread());
                view.runningThread.setText("  " + logModel.getInstance().getRunningThread());
                view.jCurrentUrlFound.setText("  O:" + webCrawler.getInstance().getOnionQueuesSize() + "-P:" + webCrawler.getInstance().getParsingQueuesSize());
                view.ParsingQueues.setText("  " + webCrawler.getInstance().getParsingThreads());
                view.onionQueues.setText("  " + webCrawler.getInstance().getOnionThreads());

                view.jStatus.setText("  " + status.appStatus);
                if (eventListner.getInstance().getBackupState())
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

                if(sleepCounter==10)
                {
                    sleepCounter = 0;
                    view.jSystemProgressPane.setText(webCrawler.getInstance().priorityQueueLogs());
                    view.jServerErrorPane.setText(webCrawler.getInstance().onionQueueLogs());
                }
                sleep(100);
                sleepCounter++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void setButtonState(boolean state)
    {
        view.jBackupBtn.setEnabled(state);
        view.jClearBtn.setEnabled(state);
        view.jPauseBtn.setEnabled(state);
        view.jPreferenceBtn.setEnabled(state);
        view.jRestartBtn.setEnabled(state);
        view.jStartBtn.setEnabled(state);
        view.jUpdateLogBtn.setEnabled(state);
    }

    void onPauseCrawler()
    {
        status.appStatus = enumeration.appStatus.paused;
        view.jStartBtn.setBackground(new Color(240, 240, 240));
        view.jPauseBtn.setBackground(Color.green);
    }

    void onStartCrawler()
    {
        status.appStatus = enumeration.appStatus.running;
        view.jPauseBtn.setBackground(new Color(240, 240, 240));
        view.jStartBtn.setBackground(Color.green);
    }
}
