/*
	ScrollText class by Magnus Mickelsson 970422.

	 (C) 1997 Magnus Mickelsson. All rights reserved!

 */

import java.awt.*;
import java.applet.*;


public class ScrollText extends Applet implements Runnable{
   private Image offScreenImage;
   private Graphics offScreenGraphics;
   private Dimension offScreenSize;
   String paramstr1;
   String paramstr2;
   String paramstr3;
   String paramstr4;
   Integer slask=null;
   int speed=2;
   int x=0;
   int y=50;
   int direction=1;
   int delay=50;
   Thread runner;
   Font fonten;

    public void init() {

// Get parameters for the applet

        fonten=new Font("Helvetica", Font.ITALIC + Font.BOLD, 22);
        paramstr1=getParameter("text");
        paramstr2=getParameter("speed");
        paramstr3=getParameter("height");
        paramstr4=getParameter("delay");


// Convert parameters from strings to integers
        if (paramstr2 != null)
        {
            slask=Integer.valueOf(paramstr2);
            speed=slask.intValue();
            if (speed<1)
				speed=2;
        }

        if (paramstr4 != null)
        {
            slask=Integer.valueOf(paramstr4);
            delay=slask.intValue();
            if (delay<40)
				delay=40;
        }

        if (paramstr3 != null)
        {
            slask=Integer.valueOf(paramstr3);
            y=slask.intValue()/2;
            if (y<0)
				y=50;
        }

        if (paramstr1 == null)
	paramstr1="No text input!";

    }

  public void start()
	{
		runner=new Thread(this);
		runner.start();
	}

  public void stop()
	{
		runner.stop();
		runner=null;
	}

  public void run()
	{
		long t=System.currentTimeMillis();
		while(Thread.currentThread()==runner)
		{
			repaint();
			try
			{
				t+=delay;
				Thread.sleep(Math.max(0, t-System.currentTimeMillis()));
			}
			catch (InterruptedException e)
			{
				break;
			}

		}
	}


  public final synchronized void update (Graphics theG)
    {
      Dimension d = size();
      if((offScreenImage == null) || (d.width != offScreenSize.width) ||
	 (d.height != offScreenSize.height))
	{
	  offScreenImage = createImage(d.width, d.height);
	  offScreenSize = d;
	  offScreenGraphics = offScreenImage.getGraphics();
	  offScreenGraphics.setFont(fonten);
	}
      offScreenGraphics.fillRect(0,0,d.width, d.height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

    public void paint(Graphics g)
    {

// The scrolling!

	Dimension d = size();
	g.setColor(Color.black);
	FontMetrics fm=g.getFontMetrics(getFont());
	g.drawString(paramstr1,d.width-x , y);

// Scroll horizontally
	x=x+speed;

	g.setColor(Color.white);

// Check if either scrolling needs to be resetted (or vertically: if we should bounce)

	if (x>(2*d.width + fm.stringWidth(paramstr1)))
		x=0;

// Paint the next step
//	repaint();

       return;

    }

}

