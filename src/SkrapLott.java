/*
	SkrapLott class by Magnus Mickelsson 990202.

	 (C) 1999 Magnus Mickelsson. All rights reserved!

 */

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.net.URL;
import java.net.MalformedURLException;
import java.applet.Applet;
import java.applet.AppletContext;

public class SkrapLott extends Applet implements Runnable {
// The thread of the applet
	Thread traden = null;
    	private Image offScreenImage;
    	private Graphics offScreenGraphics;
    	private Dimension offScreenSize;
    	String paramstr;
    	boolean painter=false;
    	boolean loadedImage=false;
    	boolean showskrap = true;
    	boolean skrapat = false;
    	boolean skrapat2 = false;
	FontMetrics fm=null;
    	private Image logga=null;
    	private Image bakgrund=null;
    	PixelGrabber pixel_grabber;
	int slump1, slump2, slump;
	int mousex, mousey;
	int startx = 46;
	int starty = 43;
	int endx = 119;
	int endy = 53;
	int startxx = 146;
	int endxx = 225;
	int buttonx = 308;
	int textcenter = 50;
	boolean mouse = false;
    	int image_width, image_height;
	int image_pixels[];
	int to_pixels[];
    
	private Image skrapad=null;
    	MediaTracker track = null;

    public void init() {

        paramstr="skrapet.gif";
        paramstr=getParameter("image");
        logga = getImage(getDocumentBase(), paramstr);

        paramstr="skraplott.gif";
        paramstr=getParameter("backimage");
        bakgrund = getImage(getDocumentBase(), paramstr);

	  track = new MediaTracker(this);
        track.addImage(logga, 0);
        track.addImage(bakgrund, 1);

        paramstr=new String(getParameter("url"));

		slump = (int)Math.round(Math.random()*53)+1;

		switch (slump) {
			case 1:
				slump1 = 16280;
				slump2 = 16791;
				break;
			case 2:
				slump1 = 28056;
				slump2 = 28567;
				break;
			case 3:
				slump1 = 7576;
				slump2 = 8087;
				break;
			case 4:
				slump1 = 14232;
				slump2 = 14743;
				break;
			case 5:
				slump1 = 17304;
				slump2 = 17815;
				break;
			case 6:
				slump1 = 28568;
				slump2 = 29079;
				break;
			case 7:
				slump1 = 29592;
				slump2 = 30103;
				break;
			case 8:
				slump1 = 24472;
				slump2 = 24983;
				break;
			case 9:
				slump1 = 26520;
				slump2 = 27031;
				break;
			case 10:
				slump1 = 19352;
				slump2 = 19863;
				break;
			case 11:
				slump1 = 2968;
				slump2 = 3479;
				break;
			case 12:
				slump1 = 8088;
				slump2 = 8599;
				break;
			case 13:
				slump1 = 15768;
				slump2 = 16279;
				break;
			case 14:
				slump1 = 27032;
				slump2 = 27543;
				break;
			case 15:
				slump1 = 11672;
				slump2 = 12183;
				break;
			case 16:
				slump1 = 22936;
				slump2 = 23447;
				break;
			case 17:
				slump1 = 5528;
				slump2 = 6039;
				break;
			case 18:
				slump1 = 9624;
				slump2 = 10135;
				break;
			case 19:
				slump1 = 3992;
				slump2 = 4503;
				break;
			case 20:
				slump1 = 16792;
				slump2 = 17303;
				break;
			case 21:
				slump1 = 26008;
				slump2 = 26519;
				break;
			case 22:
				slump1 = 18840;
				slump2 = 19351;
				break;
			case 23:
				slump1 = 22424;
				slump2 = 22935;
				break;
			case 24:
				slump1 = 7064;
				slump2 = 7575;
				break;
			case 25:
				slump1 = 27544;
				slump2 = 28055;
				break;
			case 26:
				slump1 = 5016;
				slump2 = 5527;
				break;
			case 27:
				slump1 = 21400;
				slump2 = 21911;
				break;
			case 28:
				slump1 = 13208;
				slump2 = 13719;
				break;
			case 29:
				slump1 = 4504;
				slump2 = 5015;
				break;
			case 30:
				slump1 = 6040;
				slump2 = 6551;
				break;
			case 31:
				slump1 = 9112;
				slump2 = 9623;
				break;
			case 32:
				slump1 = 11160;
				slump2 = 11671;
				break;
			case 33:
				slump1 = 30104;
				slump2 = 30314;
				break;
			case 34:
				slump1 = 6552;
				slump2 = 7063;
				break;
			case 35:
				slump1 = 15256;
				slump2 = 15767;
				break;
			case 36:
				slump1 = 3480;
				slump2 = 3991;
				break;
			case 37:
				slump1 = 19864;
				slump2 = 20375;
				break;
			case 38:
				slump1 = 24984;
				slump2 = 25495;
				break;
			case 39:
				slump1 = 23960;
				slump2 = 24471;
				break;
			case 40:
				slump1 = 20376;
				slump2 = 20887;
				break;
			case 41:
				slump1 = 12696;
				slump2 = 13207;
				break;
			case 42:
				slump1 = 23448;
				slump2 = 23959;
				break;
			case 43:
				slump1 = 17816;
				slump2 = 18327;
				break;
			case 44:
				slump1 = 21912;
				slump2 = 22423;
				break;
			case 45:
				slump1 = 8600;
				slump2 = 9111;
				break;
			case 46:
				slump1 = 10648;
				slump2 = 11159;
				break;
			case 47:
				slump1 = 14744;
				slump2 = 15255;
				break;
			case 48:
				slump1 = 20888;
				slump2 = 21399;
				break;
			case 49:
				slump1 = 29080;
				slump2 = 29591;
				break;
			case 50:
				slump1 = 13720;
				slump2 = 14231;
				break;
			case 51:
				slump1 = 18328;
				slump2 = 18839;
				break;
			case 52:
				slump1 = 25496;
				slump2 = 26007;
				break;
			case 53:
				slump1 = 10136;
				slump2 = 10647;
				break;
			case 54:
				slump1 = 12184;
				slump2 = 12695;
				break;
			
		}

		repaint();
	}

   public void start()
   {
		if(traden == null)
		{
			traden = new Thread(this);
			traden.start();
		}
   }

   public void stop()
   {
		traden = null;
   }

   public void run()
   {
		long time = System.currentTimeMillis();

		while (traden != null)
		{
			if (track.checkAll(true)) {
			
				if (!painter || !loadedImage) {
					painter = true;
					repaint();
				}
				
				time+=100;
				try
				{
					Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
				}
				catch (InterruptedException e)
				{
				}
			}
		}

    }


    public final synchronized void update (Graphics theG)
    {
	    Dimension d = size();
		theG.setColor(Color.black);
		if((offScreenImage == null) || (d.width != offScreenSize.width) ||
		(d.height != offScreenSize.height)) {
			offScreenImage = createImage(d.width, d.height);
			offScreenSize = d;
			offScreenGraphics = offScreenImage.getGraphics();
			offScreenGraphics.setFont(getFont());
		}
				offScreenGraphics.fillRect(0,0,d.width, d.height);
		paint(offScreenGraphics);
		theG.drawImage(offScreenImage, 0, 0, null);
		theG.setColor(Color.black);
    }

    public void paint(Graphics g)
    {
		Dimension d = size();
		Rectangle r=bounds();
		int maxarray = image_width * image_height;
		if (fm==null)
			fm=g.getFontMetrics(getFont());

	   	g.setColor(Color.black);
		g.fillRect(0, 0, r.width, r.height);
	    if (loadedImage)
		{
			if (mouse) {
						for (int i=-5; i<5; i++) {
					if ((mousex + i) < image_width && (mousex + i) > 0 && mousey < image_height) {
						image_pixels[mousey*image_width+mousex+i] = 0;						
						if ((mousey - 1) > 0 && (mousey - 1) < image_height)							image_pixels[(mousey-1)*image_width+mousex+i] = 0;
						if ((mousey - 2) > 0 && (mousey - 2) < image_height)							image_pixels[(mousey-2)*image_width+mousex+i] = 0;
						if ((mousey - 3) > 0 && (mousey - 3) < image_height)							image_pixels[(mousey-3)*image_width+mousex+i] = 0;
						if ((mousey - 4) > 0 && (mousey - 4) < image_height)							image_pixels[(mousey-4)*image_width+mousex+i] = 0;
						if ((mousey - 5) > 0 && (mousey - 5) < image_height)							image_pixels[(mousey-5)*image_width+mousex+i] = 0;
						if ((mousey + 1) < image_height && (mousey + 1) > 0)							image_pixels[(mousey+1)*image_width+mousex+i] = 0;						if ((mousey + 2) < image_height && (mousey + 2) > 0)							image_pixels[(mousey+2)*image_width+mousex+i] = 0;						if ((mousey + 3) < image_height && (mousey + 3) > 0)							image_pixels[(mousey+3)*image_width+mousex+i] = 0;
						if ((mousey + 4) < image_height && (mousey + 4) > 0)							image_pixels[(mousey+4)*image_width+mousex+i] = 0;						if ((mousey + 5) < image_height && (mousey + 5) > 0)							image_pixels[(mousey+5)*image_width+mousex+i] = 0;
					}
					skrapad = createImage(new MemoryImageSource(image_width, image_height, image_pixels, 0, image_width));
					mouse = false;
				}
			
			}
	        g.drawImage(bakgrund, 0, 0,this);

			prepareImage(skrapad, this);
	        g.setColor(Color.black);
			g.drawString(""+slump1+"", startx+textcenter-30, starty+11);			g.drawString(""+slump2+"", startxx+textcenter-30, starty+11);			if (showskrap)
				g.drawImage(skrapad, 0, 0, this);	        g.setColor(Color.white);
	    } else if (track.checkAll(true)) {
		    prepareImage(logga, this);

		    image_width = logga.getWidth(this);
		    image_height = logga.getHeight(this);

		    image_pixels = new int[image_width * image_height];

		    pixel_grabber = new PixelGrabber(logga.getSource(), 0, 0,
	            	image_width, image_height, image_pixels, 0, image_width);

		    try {
	      		pixel_grabber.grabPixels();
	     	    } catch (InterruptedException e) {
		    }

		    pixel_grabber = null;
			skrapad = createImage(new MemoryImageSource(image_width, image_height, image_pixels, 0, image_width));
			loadedImage=true;			repaint();
		}
		else {
			g.setColor(Color.white);
			g.drawString("Var god vänta.. Laddar bilder..", d.width/2-70, d.height/2-5);
	        showStatus("Java applet av Magnus Mickelsson");
		}		
		g.setColor(Color.black);

		return;
    }

    public boolean mouseMove(Event evt, int x, int y) {
		mousex = x;
		mousey = y;		if (loadedImage && mousex>(startx-10) && mousex<(endxx+10) && mousey>(starty-10) && mousey<(endy+20)) {			mouse = true;
						if (mousex>startx && mousex<endx && mousey>starty && mousey<endy) {				skrapat = true;
			}			
			if (mousex>startxx && mousex<endxx && mousey>starty && mousey<endy) {				skrapat2 = true;
			}
						repaint();
		}		
//		showStatus("Mouse moved to ("+x+", "+y+")");
        return true;
    }

    public boolean mouseDown(Event evt, int x, int y) {
		if (loadedImage && x>endxx && x<buttonx && y>starty && y<endy) {
			
			if (skrapat && skrapat2) {				showskrap = false;
				repaint();
							try {
					AppletContext ac = getAppletContext();
					ac.showDocument(new URL(paramstr+"?id="+slump), "_self");
						showStatus("Laddar in ny URL... Var god vänta.");
				}
				catch (Exception ex) {
					System.out.println("Exception...");
					showStatus("Den angivna URL:en var inte korrekt...");
				}
			}
			else
				    showStatus("Var god skrapa fram dina nummer först.");		}
		        return true;
    }
    public boolean mouseEnter(Event evt, int x, int y) {

		repaint();
        showStatus("Skrapa bort de grå fälten på lotten genom att gå med muspekaren över dessa.");
        return true;
    }
}

