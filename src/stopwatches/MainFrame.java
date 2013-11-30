package stopwatches;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class MainFrame extends JFrame {

  public static MainFrame frame;
  private Vector vecWatches;
  private Container pane;     //Main Container Pane
  private JPanel pnWatches;
  private JPanel pnButts;
  private JButton btAdd;
  private JButton btClose;
  private JTextField ebWatchName;
  private int watchCount;     //Total number of watches added since run
  private static Toolkit toolKit;


  public MainFrame() {
    watchCount=0;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setTitle("Stop Watches");
    vecWatches = new Vector(8);
  }


  public static void main(String[] args) {

  //Setup GUI Look and feel from the app
    try {
      //Operating System Specific Look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    toolKit = Toolkit.getDefaultToolkit();
    frame = new MainFrame();
    frame.setup();
    frame.setFrameSize();

  }

  private void setup(){
    setIconImage(toolKit.createImage(MainFrame.class.getResource("clock.gif")));

    pane = this.getContentPane();
    pane.setLayout(new BorderLayout());

    pnButts = new JPanel();
    pnButts.setLayout(new FlowLayout(FlowLayout.LEFT));
    pnButts.setBorder(BorderFactory.createEtchedBorder());
    btAdd = new JButton("ADD New StopWatch");
    btClose = new JButton("Close");
    ebWatchName = new JTextField();
    ebWatchName.setPreferredSize(new Dimension(100,20));
    this.setDefaultText();    //Setup Default Watch Name
    pnButts.add(btAdd);
    pnButts.add(ebWatchName);
    pnButts.add(btClose);

    btAdd.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
          addWatch(new PnStopWatch(ebWatchName.getText()));
        };
      }
    );

    btClose.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
          cancel();
        };
      }
    );

    pnWatches = new JPanel();
    pnWatches.setBackground(Color.white);;
    pnWatches.setLayout(new FlowLayout());

    pane.add(pnButts,BorderLayout.NORTH);
    pane.add(pnWatches);
  };

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

 /**Close the dialog*/
  void cancel() {
    System.exit(0);
  }

  private void addWatch(PnStopWatch sw){
    vecWatches.add(sw);
    pnWatches.add(sw);
    frame.setFrameSize();
    watchCount++;
    this.setDefaultText();    //Setup Default Watch Name
  };

  //Removes Watch From Display
  protected void removeWatch(PnStopWatch sw){
    int pos = vecWatches.indexOf(sw);
    pnWatches.remove(sw);
    vecWatches.remove(pos);
    frame.setFrameSize();
    pnWatches.repaint();      //Send message for clean paint
  };

  private void setDefaultText(){
    ebWatchName.setText("Watch: " + (watchCount+1));
  };

  public String toString(){
    return ("This is a Stop Watch Application");
  };

  private void setFrameSize(){
    Dimension screenSize = toolKit.getScreenSize();
    int clocks=vecWatches.size();
    if (clocks<=2) {
      if (clocks<=0) this.setSize(500,300);
      else frame.pack();
      }
    else{
      PnStopWatch sw;
      sw=(PnStopWatch)vecWatches.get(0);
      int width = sw.getWidth();
      int height = sw.getHeight();
      int itemsDown=1;
      int itemsAcross=clocks;
      double scrWidth = (screenSize.getWidth());

      if ((itemsAcross*width)>scrWidth) {
        itemsAcross = (int)scrWidth/ width;
        itemsDown = (clocks/itemsAcross);
        if (clocks%itemsAcross!=0) itemsDown++;
      };

      double requiredHeight=(height*itemsDown)+80;
      double requiredWidth=(width*itemsAcross)+40;

      if (requiredHeight>screenSize.getHeight()) requiredHeight=(screenSize.getHeight()-30);

      this.setSize((int)requiredWidth,(int)requiredHeight);

    }
    frame.show();
  };

}