package stopwatches;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class PnAnalogue extends JPanel {
  private BorderLayout borderLayout1 = new BorderLayout();
  private Ellipse2D.Double face;
  private Line2D.Double hourMark;
  public boolean okToDrawClock=false;
  int diameter;
  final Color CLEAR = new Color(0,0,0,0);
  BasicStroke widePen = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
  BasicStroke narrowPen = new BasicStroke(1.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
  final double TWO_PI = 2.0*Math.PI;
  Line2D.Double hourHand;
  Line2D.Double minuteHand;
  Line2D.Double secondHand;
  int hours = 0;
  int minutes = 0;
  int seconds = 0;
  Ellipse2D.Double center;

  public PnAnalogue() {
    diameter=100;
    face=new Ellipse2D.Double();
    hourMark= new Line2D.Double(0, -diameter*0.38, 0, -diameter*0.48);
    center = new Ellipse2D.Double(-3,-3,6,6);
    hourHand = new Line2D.Double(0,6,0,-diameter*0.25);
    minuteHand = new Line2D.Double(0,8,0,-diameter*0.3);
    secondHand = new Line2D.Double(0,14,0,-diameter*0.35);
//    setOpaque(false);
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.setPreferredSize(new Dimension(110,110));
    this.setBackground(Color.darkGray);
  }

 protected void setOkToDrawClock(){
  okToDrawClock=true;
 };

 protected boolean getOkToDrawClock(){
  return okToDrawClock;
 };


 public void paint(Graphics g){
  super.paint(g);
  double secondAngle=seconds*TWO_PI/60;
  double minuteAngle=(secondAngle+minutes*TWO_PI)/60;
  double hourAngle=(minuteAngle+hours*TWO_PI)/12;
  Dimension size = getSize();
  face.setFrame((size.width-diameter)/2,(size.height-diameter)/2,diameter,diameter);
  Graphics2D g2D = (Graphics2D)g;

  //Clear panel
  g2D.setPaint(CLEAR);
  g2D.fillRect(0,0,size.width,size.height);
  g2D.setPaint(Color.black);
  g2D.fill(face);
  g2D.setPaint(Color.white);
  g2D.setStroke(widePen);
  g2D.draw(face);

  g2D.translate(size.width/2,size.height/2);

  //Paint hour marks
  for (int i=0;i<12;i++){
    if (i%3==0)
      g2D.setStroke(widePen);
    else
      g2D.setStroke(narrowPen);
    g2D.draw(hourMark);
    g2D.rotate(TWO_PI/12.0);
  }


 //Hand
  g2D.setPaint(Color.white);
  AffineTransform transform = g2D.getTransform();

  g2D.setStroke(widePen);
  g2D.rotate(hourAngle);
  g2D.draw(hourHand);

  g2D.setTransform(transform);
  g2D.rotate(minuteAngle);
  g2D.draw(minuteHand);



  g2D.setStroke(narrowPen);
  g2D.setTransform(transform);
  g2D.rotate(secondAngle);
  g2D.draw(secondHand);

  g2D.setPaint(Color.white);
  g2D.draw(center);



 };


   public void paintNow(){
    Graphics GD=getGraphics();    //get current graphics context
    GD.setClip(this.getBounds());
//    GD.setClip(new Rectangle(getXPos(1),getYPos(1),(COL_SPACER*gameStatus.getGridCols()),(ROW_SPACER*gameStatus.getGridRows()))); //get area to repaint
    paint(GD);                    //repaint clipped area
    GD.dispose();
  }


}

