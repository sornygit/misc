/*
This applet creates an animated display by summing four
sine waves into an array. Example FPS rates are at
http://rsb.info.nih.gov/nih-image/java/benchmarks/plasma.html.
It is based on "Sam's Java Plasma Applet"
(http://www.dur.ac.uk/~d405ua/Plasma.html) by Sam Marshall
(t-sammar@microsoft.com). It was modified to use 8-bit images
by Menno van Gangelen (M.vanGangelen@element.nl).
*/

import java.awt.image.MemoryImageSource;
import java.awt.image.IndexColorModel;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;

public class Plasma extends java.applet.Applet implements Runnable {
  Image img;
  Thread runThread;
  long firstFrame, frames, fps;
  int width, height;
  int w,h,size;
  int scale=3;
  boolean showFPS = true;
  IndexColorModel icm;
  int[] waveTable;
  byte[][] paletteTable;
  byte[] pixels;
// Doublebuffring variables
  private Image offScreenImage;
  private Graphics offScreenGraphics;
  private Dimension offScreenSize;

  public void init() {
     width = size().width;
     height = size().height;
     String p = getParameter("scale");
     if (p != null)
        scale = Integer.parseInt(p);
     p = getParameter("showfps");
     if (p != null)
         showFPS = p.equals("true");
     w = width/scale;
     h = w;
     size = (int) ((w+h)/2)*4;
     pixels = new byte[w*h];
     waveTable = new int[size];
     paletteTable = new byte[3][256];
     calculatePaletteTable();
     img=createImage(new MemoryImageSource(w,h,icm,pixels,0,w));
  }

  public void start() {
     if (runThread == null) {
       runThread=new Thread(this);
       runThread.start();
       firstFrame=System.currentTimeMillis();
       frames = 0;
     };
  }

  public void stop() {
    if (runThread != null) {
       runThread.stop();
       runThread=null;
    }
  }



  public final synchronized void update (Graphics theG)
    {
      Dimension scr=size();
      int width=scr.width;
      int height=scr.height;

      if((offScreenImage == null) || (width != offScreenSize.width) ||
	 (height != offScreenSize.height)) 
	{
	  offScreenImage = createImage(width, height);
	  offScreenSize = scr;
	  offScreenGraphics = offScreenImage.getGraphics();
	  offScreenGraphics.setFont(getFont());
	}
      offScreenGraphics.setColor(Color.white);
      offScreenGraphics.fillRect(0,0,width, height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

  public void paint(Graphics g) {
     img.flush();
     g.drawImage(img, 0, 0, width, height, null);
     if (showFPS) {
        frames++;
        fps = (frames*10000) / (System.currentTimeMillis()-firstFrame);
        g.drawString(fps/10 + "." + fps%10 + " fps", 2, height - 2);
     }
  }

  void calculateWaveTable() {
    for(int i=0;i<size;i++)
       waveTable[i]=(int)(16*(1+Math.sin(((double)i*2*Math.PI)/size)));
  }

  int FadeBetween(int start,int end,int proportion) {
    return ((end-start)*proportion)/128+start;
  }

  void calculatePaletteTable() {
     for(int i=0;i<128;i++) {
        paletteTable[1][i]=(byte)(FadeBetween(255,0,i));
        paletteTable[2][i]=(byte)(FadeBetween(255,255,i));
        paletteTable[0][i]=(byte)(FadeBetween(255,255,i));
     }
     for(int i=0;i<128;i++) {
        paletteTable[1][i+128]=(byte)(FadeBetween(255,255,i));
        paletteTable[2][i+128]=(byte)(FadeBetween(255,255,i));
        paletteTable[0][i+128]=(byte)(FadeBetween(0,255,i));
     }
     icm = new IndexColorModel(8, 256, paletteTable[0], paletteTable[1], paletteTable[2]);
  }

  public void run() {
    int x,y;
    int index;
    int tempval,result;
    int spd1=2,spd2=5,spd3=1,spd4=4;
    int pos1=0,pos2=0,pos3=0,pos4=0;
    int tpos1,tpos2,tpos3,tpos4;
    int inc1=6,inc2=3,inc3=3,inc4=9;

    runThread.setPriority(Thread.MIN_PRIORITY);
    calculateWaveTable();
    while(true) {
      index=0;
      tpos1=pos1; tpos2=pos2;
      for(y=0;y<h;y++) {
        tpos3=pos3; tpos4=pos4;
        tpos1%=size; tpos2%=size;
        tempval=waveTable[tpos1] + waveTable[tpos2];
        for(x=0;x<w;x++) {
          tpos3%=size; tpos4%=size;
          result=tempval + waveTable[tpos3] + waveTable[tpos4];
          pixels[index++]=(byte)result;
          tpos3+=inc3; tpos4+=inc4;
        }
        tpos1+=inc1; tpos2+=inc2;
      }
      pos1+=spd1; pos2+=spd2; pos3+=spd3; pos4+=spd4;
      repaint();
      try {Thread.sleep(40);}
      catch (InterruptedException e) { }
    }
  }

}