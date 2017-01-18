
// applet "Blurs an Image"

import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource ;

public class Blur extends Applet implements Runnable{
// The thread of the applet
  Thread traden = null;

// Image to manipulate
  private Image base_image;
// Destination image
  private Image dest_image;

// Background color
  Color back;

// Mediatracker to load image into
  MediaTracker tracker;

// Doublebuffring variables
  private Image offScreenImage;
  private Graphics offScreenGraphics;
  private Dimension offScreenSize;

// Sinus stuff to precalculate
  static int	 degree=0;
// 1 degree = x radians
  double   inc=0.0174;
  double	freq=0.0;
  double	freq2=0.0;
  double	factor=0.0;
  double	factor2=0.0;
  double	factor3=0.0;

// Other variables
  int	delay=50;
  int          image_width=0, image_height=0;
  int          image_pixels[];
  int	to_pixels[];
  int	xamplitud=20;
  int	yamplitud=20;
  int	center_x = 0, center_y = 0;
  int	R=0, G=0, B=0;
  private String back_light;
  static boolean loaded=false;
  int	deg_inc=1;
  int	deg_inc2=1;
  int	deg_inc3=1;
  int	max=360;
  PixelGrabber pixel_grabber;

  public void init() {
    tracker = new MediaTracker(this);

// Fix variables
    xamplitud=Integer.valueOf(getParameter("xamplitud")).intValue();
    yamplitud=Integer.valueOf(getParameter("yamplitud")).intValue();
    deg_inc=Integer.valueOf(getParameter("inc")).intValue();
    delay=Integer.valueOf(getParameter("delay")).intValue();
    freq=Double.valueOf(getParameter("freq")).doubleValue();
    freq2=Double.valueOf(getParameter("freq2")).doubleValue();
    factor=Double.valueOf(getParameter("factor")).doubleValue();
    factor2=Double.valueOf(getParameter("factor2")).doubleValue();
    factor3=Double.valueOf(getParameter("factor3")).doubleValue();
    R=Integer.valueOf(getParameter("R")).intValue();
    G=Integer.valueOf(getParameter("G")).intValue();
    B=Integer.valueOf(getParameter("B")).intValue();
    back_light=getParameter("back_light");
    center_x=Integer.valueOf(getParameter("x")).intValue();
    center_y=Integer.valueOf(getParameter("y")).intValue();

    back = new Color(R, G, B);

    // Get base_image
    base_image = getImage(getDocumentBase(), getParameter("img"));
    tracker.addImage(base_image, 0);

    degree=0;
    repaint();
   }


   public void start()
   {
	//Create a thread and start it
	if(traden == null)
	{
		traden = new Thread(this);
		traden.start();
	}
   }

   public void stop()
   {
	//Stop animation thread
	traden = null;
   }

   public void run()
   {
	long time = System.currentTimeMillis();

	while (traden != null)
	{
		if (tracker.checkAll(true)) {
			try
			{
				time += delay;
				Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
			}
			catch (InterruptedException e)
			{
			}

			repaint();
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
	  offScreenGraphics.setFont(getFont());
	}
      theG.setColor(back);
      offScreenGraphics.fillRect(0,0,d.width, d.height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

  public void paint(Graphics g) {
    Dimension d = size();
    int startx=0, starty=0;
    int radius = yamplitud*yamplitud;
    double sin_1 = 0.0, sin_2 = 0.0, cos_1 = 0.0;

// Clear screen
    g.setColor(back);
    g.fillRect(0,0,d.width, d.height);

   if (loaded==false) {
	g.setColor(new Color(255-R, 255-G, 255-B));
	g.drawString("Loading image ...  Applet by magnus@netsolutions.se", 10, 10);

    // load it all before continuing

    if (tracker.checkAll(true)) {
	    // so to see
	    prepareImage(base_image, this);

	    image_width = base_image.getWidth(this);
	    image_height = base_image.getHeight(this);

// Create array where we grab the pixels
	     image_pixels = new int[image_width * image_height];
	     to_pixels = new int[image_width*(image_height+1)];

// Create a PixelGrabber to Get the Pixels of the image and store
// them into the image_pixels array
	     pixel_grabber = new PixelGrabber(base_image.getSource(), 0, 0,
	              image_width, image_height, image_pixels, 0, image_width);

	     try {
	      pixel_grabber.grabPixels();
	     } catch (InterruptedException e) {
	     }

	     pixel_grabber = null;

	    loaded = true;

    }
    }

     if (loaded) {
	    degree+=deg_inc;

	    if (degree>=1000000)
		degree=0;

	    cos_1 = Math.cos(degree*freq*inc);
	    sin_2 = Math.sin(degree*freq2*inc);
	    startx=(int)((image_width/2)-(xamplitud*cos_1));
	    starty=(int)((image_height/2)+(yamplitud*sin_2));

// Create array with room for fun effects

	     for(int i=0; i<(image_width*image_height); i++)
		to_pixels[i]=0;

	     for(int j=(starty-yamplitud); j<(starty+yamplitud); j++) {
		int row = j*image_width;
		int kolly = Math.abs(starty-j);
		int kolly_2 = kolly * kolly;

		if (j>=0) {
	
			if (j>=image_height)
				break;
	     		for(int i=(startx-xamplitud); i<(startx+xamplitud); i++) {
				int pos = row+i;
				int kollx = Math.abs(startx-i);
				int rad = kolly_2+kollx*kollx;
		
				if (i>=0) {
	
					if (i>=image_width)
						break;

// If the pixel is within the circle, flip pixels, else just copy colors.

					if (rad<radius) {
						to_pixels[pos] = filterRGB(radius-rad, image_pixels[pos]);
					}
					else
						to_pixels[pos] = image_pixels[pos];


				}
			}
		}
     	}
        	
      // make it
 	dest_image = createImage(new MemoryImageSource(image_width, image_height, to_pixels,0,image_width));

     // make it real
	prepareImage(dest_image, this);
     }

     if (tracker.checkAll(true))   
	g.drawImage(base_image, center_x, center_y, this);

     if (loaded)
     {
	g.drawImage(dest_image, center_x, center_y, this);
	dest_image=null;
     }
  }

    public int filterRGB(int intensity, int rgb) {
        int red, green, blue, alpha;
 
        red   = (rgb & 0x00ff0000) >> 16;
        green = (rgb & 0x0000ff00) >> 8;
        blue  = rgb & 0x000000ff;
        alpha = (rgb & 0xff000000) >> 24;

        if (back_light.equals("on")) {
	        if (red==R)
		red=Math.abs(R-10);
	        if (green==G)
		green=Math.abs(G-10);
	        if (blue==B)
		blue=Math.abs(B-10);
        }

        red = (int)(red*(1.0+factor*intensity));
        if (red>255)
	red = 255;
        blue = (int)(blue*(1.0+factor3*intensity));
        if (blue>255)
	blue = 255;
        green = (int)(green*(1.0+factor2*intensity));
        if (green>255)
	green = 255;

        return ((alpha << 24) | (red << 16) | (green << 8) | blue);
    }

} // end wave
