package stopwatches;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.text.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

class PnStopWatch extends JPanel implements Runnable{

static private ImageIcon imgPlay;
static private ImageIcon imgStop;
static private ImageIcon imgReset;
static private ImageIcon imgPause;
static private ImageIcon imgPause2;

static {
  imgPlay = new ImageIcon(PnStopWatch.class.getResource("play.gif"));
  imgStop = new ImageIcon(PnStopWatch.class.getResource("stop.gif"));
  imgReset = new ImageIcon(PnStopWatch.class.getResource("reset.gif"));
  imgPause = new ImageIcon(PnStopWatch.class.getResource("pause.gif"));
  imgPause2 = new ImageIcon(PnStopWatch.class.getResource("pause2.gif"));
}

  private JPanel pnButts;
  private JPanel pnTitle;
  private JPanel pnWatch;
  private JPanel pnWatchSpacer;
  private JPanel pnDigital;
  private JButton btClose;
  private JButton btStart;
  private JButton btStop;
  private JButton btPause;
  private JButton btReset;
  private JLabel lbTitle;
  private String title;
  private JTextField ebHour;
  private JTextField ebMin;
  private JTextField ebSec;
  private JTextField ebTenth;
  private long totalTime;
  private long runningTime;
  private long startTime;
  private long initialTimeUsed;
  private boolean stopCommandIssued;
  private boolean resetCommandIssued;
  private boolean running;
  private int currHours;
  private int currMins;
  private int currSecs;
  private int currTenths;
  private boolean pauseMode;
  private JCheckBox cbCountDown;
  private JCheckBox cbAnalogue;
  private JTextField ebInitHour;
  private JTextField ebInitMin;
  private JTextField ebInitSec;
  private JLabel lbInitHour;
  private JLabel lbInitMin;
  private JLabel lbInitSec;
  private boolean countDown;
  private boolean downDir;
  private long lastBeepSecond;
  private long alarmDelay;
  private static Toolkit toolKit;
  private Thread watchThread;
  private JTextField ebAlarmHour;
  private JTextField ebAlarmMin;
  private JTextField ebAlarmSec;
  private JLabel lbAlarmHour;
  private JLabel lbAlarmMin;
  private JLabel lbAlarmSec;
  private PnAnalogue pnAnalogue;
  private boolean dispAnalogue;

  protected PnStopWatch(String aTitle) {
    title=aTitle;
    runningTime=0;
    totalTime=0;
    startTime=0;
    pauseMode=false;
    countDown=false;
    downDir=false;
    lastBeepSecond=0;
    alarmDelay=0;
    initialTimeUsed=0;
    dispAnalogue=false;

    stopCommandIssued=false;   //Watch in stopped mode
    resetCommandIssued=false;   //Watch in stopped mode
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createLoweredBevelBorder());
    toolKit = Toolkit.getDefaultToolkit();

    running=false;

    pnButts = new JPanel();
    pnTitle = new JPanel();
    pnWatch = new JPanel();
    pnWatchSpacer = new JPanel();
    pnDigital = new JPanel();
    pnAnalogue= new PnAnalogue();

    pnWatch.setLayout(new BorderLayout());
    pnWatch.setPreferredSize(new Dimension(110,110));
    pnWatchSpacer.setPreferredSize(new Dimension(1,22));


  //Setup Title
    lbTitle = new JLabel(title);
    pnTitle.setBackground(Color.blue);
    lbTitle.setForeground(Color.white);
    pnTitle.add(lbTitle);

  //Setup Button Panel
    pnButts.setBorder(BorderFactory.createEtchedBorder());
    pnTitle.setBorder(BorderFactory.createEtchedBorder());
    btStart = new JButton("",imgPlay);
    btStop = new JButton("",imgStop);
    btPause = new JButton("",imgPause);
    btReset = new JButton("",imgReset);
    btClose = new JButton("Remove");
    btStop.setEnabled(false);
    btPause.setEnabled(false);
    cbCountDown= new JCheckBox("CountDown");
    cbAnalogue= new JCheckBox("Analogue");

    lbInitHour = new JLabel("Reset Time (HH:MM:SS)");
    lbInitMin = new JLabel(":");
    lbInitSec = new JLabel(":");
    ebInitHour = new JTextField("00");
    ebInitMin = new JTextField("00");
    ebInitSec = new JTextField("00");

    lbAlarmHour = new JLabel("Alarm every (HH:MM:SS)");
    lbAlarmMin = new JLabel(":");
    lbAlarmSec = new JLabel(":");
    ebAlarmHour = new JTextField("00");
    ebAlarmMin = new JTextField("00");
    ebAlarmSec = new JTextField("00");

    cbAnalogue.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          setDispAnalogue(cbAnalogue.isSelected());
        }
      }
    );

    btClose.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          doClose();
        }
      }
    );

    btStart.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          doStart();
        }
      }
    );

    btStop.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          doStop();
        }
      }
    );

    btPause.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        doPause();
      };
    }
    );

    btReset.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        doReset();
      };
    }
    );

    Dimension btSize= new Dimension(80,25);
    Dimension btIconSize= new Dimension(25,25);
    btStart.setPreferredSize(btIconSize);
    btStop.setPreferredSize(btIconSize);
    btPause.setPreferredSize(btIconSize);
    btReset.setPreferredSize(btIconSize);
    btClose.setPreferredSize(btSize);


    ebHour = new JTextField();
    ebMin = new JTextField();
    ebSec = new JTextField();
    ebTenth = new JTextField();
    ebHour.setBackground(Color.black);
    ebHour.setForeground(Color.green);
    ebMin.setBackground(Color.black);
    ebMin.setForeground(Color.green);
    ebSec.setBackground(Color.black);
    ebSec.setForeground(Color.green);
    ebTenth.setBackground(Color.black);
    ebTenth.setForeground(Color.green);
    Font timeFont= new Font(ebHour.getFont().getName(),ebHour.getFont().getStyle(),40);
    ebHour.setEditable(false);
    ebMin.setEditable(false);
    ebSec.setEditable(false);
    ebTenth.setEditable(false);

    ebHour.setFont(timeFont);
    ebMin.setFont(timeFont);
    ebSec.setFont(timeFont);
    ebTenth.setFont(timeFont);




    currHours=-1;
    currMins=-1;
    currSecs=-1;
    currTenths=-1;
    displayTime();

    pnDigital.add(ebHour);
    pnDigital.add(ebMin);
    pnDigital.add(ebSec);
    pnDigital.add(ebTenth);


    Box bxButtons=Box.createVerticalBox();

    Box bxBt1=Box.createHorizontalBox();
    bxBt1.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt1.add(btStart);
    bxBt1.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt1.add(btPause);
    bxBt1.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt1.add(btStop);
    bxBt1.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt1.add(btReset);
    bxBt1.add(Box.createGlue()); //Pad to End
    bxBt1.add(cbCountDown);

    Box bxBt2=Box.createHorizontalBox();
    bxBt2.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt2.add(lbInitHour);
    bxBt2.add(Box.createHorizontalStrut(4));  //Force padding

    bxBt2.add(Box.createGlue()); //Pad to End
    bxBt2.add(ebInitHour);
    bxBt2.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt2.add(lbInitMin);
    bxBt2.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt2.add(ebInitMin);
    bxBt2.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt2.add(lbInitSec);
    bxBt2.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt2.add(ebInitSec);


    Box bxBt3=Box.createHorizontalBox();
    bxBt3.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt3.add(lbAlarmHour);
    bxBt3.add(Box.createHorizontalStrut(4));  //Force padding
    bxBt3.add(Box.createGlue()); //Pad to End
    bxBt3.add(ebAlarmHour);
    bxBt3.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt3.add(lbAlarmMin);
    bxBt3.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt3.add(ebAlarmMin);
    bxBt3.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt3.add(lbAlarmSec);
    bxBt3.add(Box.createHorizontalStrut(2));  //Force padding
    bxBt3.add(ebAlarmSec);


    Box bxBt4=Box.createHorizontalBox();
    bxBt4.add(cbAnalogue);
    bxBt4.add(Box.createGlue()); //Pad to End
    bxBt4.add(btClose);

    bxButtons.add(Box.createVerticalStrut(2));  //Force padding
    bxButtons.add(bxBt1);
    bxButtons.add(Box.createVerticalStrut(6));  //Force padding
    bxButtons.add(bxBt2);
    bxButtons.add(Box.createVerticalStrut(6));  //Force padding
    bxButtons.add(bxBt3);
    bxButtons.add(Box.createVerticalStrut(6));  //Force padding
    bxButtons.add(bxBt4);
    bxButtons.add(Box.createGlue());            //Pad to End

    pnButts.add(bxButtons);

    pnWatch.add(pnWatchSpacer,BorderLayout.NORTH);
    pnWatch.add(pnDigital,BorderLayout.CENTER);

    this.add(pnTitle,BorderLayout.NORTH);
    this.add(pnButts,BorderLayout.SOUTH);
    this.add(pnWatch,BorderLayout.CENTER);

     ebInitHour.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebInitHour_focusLost(e);
      }
    });
    ebInitMin.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebInitMin_focusLost(e);
      }
    });
    ebInitSec.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebInitSec_focusLost(e);
      }
    });

    ebAlarmHour.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebAlarmHour_focusLost(e);
      }
    });
    ebAlarmMin.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebAlarmMin_focusLost(e);
      }
    });
    ebAlarmSec.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ebAlarmSec_focusLost(e);
      }
    });

    pnAnalogue.setOkToDrawClock();
  }

  private void doClose(){
    if (running==true){
      watchThread.interrupt();      //Stop the thread Running - will then call doClose again
    }
    else MainFrame.frame.removeWatch(this);
  };

  private void doStart(){
    //Start StopWatch Panel as Thread
    this.setPauseMode(false);
    watchThread = new Thread(this);
    watchThread.setDaemon(true);    //destroyed when owner destroyed
    watchThread.start();
    btStart.setEnabled(false);
    btStop.setEnabled(true);
    btPause.setEnabled(true);
 };


  private void doStop(){
    this.setPauseMode(false);
    btStop.setEnabled(false);
    btPause.setEnabled(false);
    stopCommandIssued=true;     //Stop the Watch
  };


  public void run(){
    long time;
    running=true;
    startTime = System.currentTimeMillis();
    runningTime=0;
    boolean stopAndRemoveWatch=false;    //To stop and remove a watch

    while (stopCommandIssued==false && stopAndRemoveWatch==false){
      if (resetCommandIssued==true) resetWatch(false);  //Reset with initial time
      if (downDir != cbCountDown.isSelected()){
         this.resetWatch(true);    //Reset watch to start with current time
         downDir=cbCountDown.isSelected();
         }
      time=System.currentTimeMillis();
      runningTime=(time-startTime);

      displayTime();
      try {
        Thread.sleep(10);
        }
      catch (InterruptedException e) {
        stopAndRemoveWatch=true;    //Cause exit loop and removeal of watch
      }
    }
    stopCommandIssued=false;    //Watch has now been stopped.
    if (downDir==true) totalTime-=runningTime;
    else totalTime+=runningTime;
    running=false;
    if (stopAndRemoveWatch==true) doClose();
    btStart.setEnabled(true);
  };

  private void doPause(){
    this.setPauseMode(!this.getPauseMode());
  };

  private void doReset(){
    this.setPauseMode(false);
    if (running) resetCommandIssued=true; //Causes reset called in Thread
    else resetWatch(false);               //Can safely reset as not in thread - use initial time
  };


  //Pass true to base on the current stop watch time
  //Pass false to base on the entered initial time
  private void resetWatch(final boolean useCurrent){
    resetCommandIssued=false;
    startTime = System.currentTimeMillis();
    if (useCurrent==true){
      totalTime= (
          (Integer.parseInt(ebHour.getText())*3600000)
          +
          (Integer.parseInt(ebMin.getText())*60000)
          +
          (Integer.parseInt(ebSec.getText())*1000)
          );
    }
    else{           //Use Initial Time
      totalTime= (
        (Integer.parseInt(ebInitHour.getText())*3600000)
        +
        (Integer.parseInt(ebInitMin.getText())*60000)
        +
        (Integer.parseInt(ebInitSec.getText())*1000)
        );
    };
    initialTimeUsed=totalTime;    //Store Time the clock is based on for alarms
    runningTime=0;
    lastBeepSecond=0;
    currSecs=-1;    //Cause Analogue update as this only updates if seconds change
    displayTime();
  };

  final private void displayTime(){
    if (this.getPauseMode()==true) return;
    long currTime;
    currTime=getCurrTime();

    //Check For Negative Time
    if (downDir==true) {
      if (currTime<0) {       //Do not do negative count
        if (stopCommandIssued==true) return;  //Already set to stop
        setCurrTenths(0);   //Ensure this shows 0 - just in case;
        toolKit.beep();
        doStop();             //Perform Stop
        return;               //Return
      };
    }

    //Parform Alarm check
    if (alarmDelay!=0) checkAlarm(currTime);

    this.setCurrHours((int)currTime/3600000);
    currTime=(currTime % 3600000);
    this.setCurrMins((int) currTime/60000);
    currTime=(currTime % 60000);
    this.setCurrSecs((int) currTime/1000);
    currTime=(currTime % 1000);
    this.setCurrTenths((int) currTime/100);
  };


  final private void checkAlarm(long currTime){
     long currBeepSecond=currTime/1000;

    long calcSecond=currBeepSecond;
    //If down base alarm on hitting 4.9 (otherwise we may miss the exact tenth of a second the timer is exactly on 5.0)

    if (downDir==false) calcSecond-=(initialTimeUsed/1000); //Add Inital Time
    else calcSecond=((initialTimeUsed/1000)-(calcSecond+1));

    if (lastBeepSecond!=currBeepSecond) {
      if (calcSecond!=0){
        if (calcSecond%alarmDelay==0) {
          toolKit.beep();
          };
        };
      lastBeepSecond=currBeepSecond;
    };
  };


  final private void setCurrHours(int hour){
    DecimalFormat df = new DecimalFormat("#,#00");
    if (currHours!=hour) {
      pnAnalogue.hours=(hour%12);
      ebHour.setText(df.format(hour));
      currHours=hour;
    };
  };

  final private void setCurrMins(int mins){
    DecimalFormat df = new DecimalFormat("#,#00");
    if (currMins!=mins) {
      pnAnalogue.minutes=mins;
      ebMin.setText(df.format(mins));
      currMins=mins;
    };
  };

  final private void setCurrSecs(int secs){
    DecimalFormat df = new DecimalFormat("#,#00");
    if (currSecs!=secs) {
      pnAnalogue.seconds=secs;
      if (dispAnalogue==true) {
//        if (pnAnalogue.getOkToDrawClock()==true) pnAnalogue.paintNow();
        if (pnAnalogue.getOkToDrawClock()==true) pnAnalogue.repaint();
      }
      ebSec.setText(df.format(secs));
      currSecs=secs;
      };
  };

  final private void setCurrTenths(int tenths){
    if (currTenths!=tenths) {
      ebTenth.setText(String.valueOf(tenths));
      currTenths=tenths;
    };
  };

  private boolean getPauseMode(){
    return pauseMode;
  };

  private void setPauseMode(boolean pm){
    if (pauseMode==pm) return;
    pauseMode=pm;
    if (pauseMode==true) btPause.setIcon(imgPause2);
    else btPause.setIcon(imgPause);
  };

 private void ebInitHour_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
      checkEditRange(ebInitHour,0,99,"Hours");
  }

 private void ebInitMin_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
      checkEditRange(ebInitMin,0,59,"Minutes");
  }

 private void ebInitSec_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
    checkEditRange(ebInitSec,0,59,"Seconds");
  }


 private void ebAlarmHour_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
    checkEditRange(ebAlarmHour,0,99,"Hours");
    calcAlarmDelay();
  }

 private void ebAlarmMin_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
    checkEditRange(ebAlarmMin,0,59,"Minutes");
    calcAlarmDelay();
  }

 private void ebAlarmSec_focusLost(FocusEvent e) {
    if (e.isTemporary()==true) return;
    checkEditRange(ebAlarmSec,0,59,"Seconds");
    calcAlarmDelay();
  }



//Generic Range Checker with Error messaging for Validation
private void checkEditRange(JTextField ebox, final int min, final int max, String title){
    int value;
    try {
      value=Integer.parseInt(ebox.getText());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(getParent(),title + " must be a value between " + min + " and " + max, "Validation",JOptionPane.ERROR_MESSAGE);
      ebox.requestFocus();
      return;
    }
    if (value>max) {
      JOptionPane.showMessageDialog(getParent(),title + " can not Exceed " + max, "Validation",JOptionPane.ERROR_MESSAGE);
      ebox.setText(String.valueOf(max));
      ebox.requestFocus();
      return;
      }
    if (value<min) {
      JOptionPane.showMessageDialog(getParent(),title + " must be at least " + min, "Validation",JOptionPane.ERROR_MESSAGE);
      ebox.setText(String.valueOf(min));
      ebox.requestFocus();
      return;
      }
  }

  private void calcAlarmDelay(){
      alarmDelay= (
        (Integer.parseInt(ebAlarmHour.getText())*3600)
        +
        (Integer.parseInt(ebAlarmMin.getText())*60)
        +
        Integer.parseInt(ebAlarmSec.getText())
        );
  };

  final private long getCurrTime(){
    if (downDir==true) return (totalTime-runningTime);
    else return (totalTime+runningTime);
  };

  private void setDispAnalogue(boolean value){
    if (dispAnalogue==value) return;
    if (value==true) {
      pnWatch.setVisible(false);
      this.add(pnAnalogue,BorderLayout.CENTER);
      pnAnalogue.setVisible(true);
    }
    else {
      pnAnalogue.setVisible(false);
      pnWatch.setVisible(true);
      this.add(pnWatch,BorderLayout.CENTER);
    }
    dispAnalogue=value;
//    pnAnalogue.validate();
  };

}

