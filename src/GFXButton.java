/*
	GFXButton class by Magnus Mickelsson 970417.

	Made for Netsolutions AB, url: http://www.netsolutions.se ...

	 (C) 1997 Netsolutions AB & Magnus Mickelsson. All rights reserved!

 */

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.applet.Applet;
import java.applet.AppletContext;

public class GFXButton extends Applet{
   private Image offScreenImage;
   private Graphics offScreenGraphics;
   private Dimension offScreenSize;
    String paramstr1;
    Integer xsize;
    String paramstr2;
    Integer ysize;
    String target;
    String im1;
    String im2;
    String url;
    Image enter = null;
    Image exit = null;
    MediaTracker track = null;
    boolean over=false;
    boolean first=true;

    public void init() {

// Get parameters for the applet

        paramstr1=getParameter("xsize");
        paramstr2=getParameter("ysize");
        im1=getParameter("image1");
        im2=getParameter("image2");
        url=getParameter("url");
        target=getParameter("target");

// Convert parameters from strings to integers
        if (paramstr1 != null)
        {
            xsize=Integer.valueOf(paramstr1);
        }
        else
            xsize=new Integer(50);

        if (target == null)
        {
            target="_self";
        }

        if (paramstr2 != null)
        {
            ysize=Integer.valueOf(paramstr2);
        }
        else
            ysize=new Integer(50);

        track = new MediaTracker(this);

        enter = getImage(getDocumentBase(), im1);
        track.addImage(enter, 0);
        exit = getImage(getDocumentBase(), im2);
        track.addImage(exit, 0);
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
	  offScreenGraphics.setFont(getFont());
	}
      offScreenGraphics.setColor(Color.white);
      offScreenGraphics.fillRect(0,0,d.width, d.height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

    public void paint(Graphics g)
    {
        if (track.checkAll(true))
        {
            g.setColor(Color.white);
            g.fillRect(0,0,xsize.intValue(), ysize.intValue());
            if (over)
                g.drawImage(enter, 0, 0, this);
            else
                g.drawImage(exit, 0, 0, this);

    	return;
        }
        else if (track.isErrorAny())
        {
            g.setColor(Color.red);
            g.drawString("Image Error!", 10, 10);

            return;
        }
        else
        {
            g.setColor(Color.black);
            g.drawString("Laddar bilder..", 10,10);
            g.setColor(Color.white);
            repaint(100);
        }

       return;

    }



    public boolean mouseDown(Event evt, int x, int y) {
        if (url!=null){
          try {
            AppletContext ac = getAppletContext();
            if(url == null) {
                showStatus("strange URL...");
            }
            else
            {
                    ac.showDocument(new URL(this.getDocumentBase(),url), target);
            }
          }
          catch (Exception ex) {
            System.out.println("Exception...");
                showStatus("strange URL...");
          }

        }
        return true;
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        over=true;
        repaint();

        if (url!=null)
            showStatus(url);
        else
            showStatus("no URL");

        return true;
    }

    public boolean mouseExit(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        over=false;
        repaint();

        showStatus("");

        return true;
    }


}

