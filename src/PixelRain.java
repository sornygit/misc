
// applet "Waves an Image"

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource ;
import java.io.IOException;

public class PixelRain extends Applet implements Runnable{
  Thread traden = null;
  private Image base_image;
  int	degree=0;
  int	sin_values[];

  long firstFrame, frames, fps;

  private Image offScreenImage;
  private Image AnimImage;

  private Graphics offScreenGraphics;
  private Dimension offScreenSize;

  MediaTracker tracker;

  int	delay=50;
  int          image_width=0, image_height=0, new_image_w=0, new_image_h=0;
  int          image_pixels[];
  int	to_pixels[];
  int	amplitud=20;
// 1 degree = x radians
  float       inc=(float)0.0174;
  int	degree_inc=1;
  int	deg_inc=1;
  int	deg_inc2=1;
  int	freq=1;
  int	freq2=1;
  int	deg_inc3=1;
  int	max=360;
  boolean	started=false;
		
  int	posx=1;
  int	posy=1;

  public void init() {
    tracker = new MediaTracker(this);
    PixelGrabber pixel_grabber;

// Fix variables
    amplitud=Integer.valueOf(getParameter("amplitud")).intValue();
    inc=(float)(Integer.valueOf(getParameter("inc")).intValue()*inc);
    degree_inc=Integer.valueOf(getParameter("degree")).intValue();
    deg_inc=Integer.valueOf(getParameter("inc")).intValue();
    deg_inc2=Integer.valueOf(getParameter("inc2")).intValue();
    deg_inc3=Integer.valueOf(getParameter("inc3")).intValue();
    freq2=Integer.valueOf(getParameter("freq2")).intValue();
    freq=Integer.valueOf(getParameter("freq")).intValue();
    posx=Integer.valueOf(getParameter("x")).intValue();
    posy=Integer.valueOf(getParameter("y")).intValue();
    delay=Integer.valueOf(getParameter("delay")).intValue();
    max = 360000;

    // base_image
      try {
          base_image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(getParameter("img")));
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      tracker.addImage(base_image, 0);
    // load it all before continuing
    try { tracker.waitForAll(); } catch (InterruptedException e){} ;

    // so to see
    prepareImage(base_image, this);

    image_width = base_image.getWidth(this);
    image_height = base_image.getHeight(this);
    new_image_w = image_width+amplitud*8;
    new_image_h = image_height+amplitud*8;

// Create array where we grab the pixels
     image_pixels = new int[image_width * image_height];
     to_pixels = new int[new_image_w*new_image_h];

//# Create a PixelGrabber to Get the Pixels of the image and store
//# them into the image_pixels array
     pixel_grabber = new PixelGrabber(base_image.getSource(), 0, 0,
              image_width, image_height, image_pixels, 0, image_width);

     try {
      pixel_grabber.grabPixels();
     } catch (InterruptedException e) {;}

     pixel_grabber = null;

    degree=0;
   }


   public void start()
   {
	//Create a thread and start it
	if(traden == null)
	{
		traden = new Thread(this);
		firstFrame=System.currentTimeMillis();
 		frames = 0;
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

  public final synchronized void update (Graphics theG)
    {
      Dimension d = size();

// Double-buffering routines (offscreen image creation)
      if((offScreenImage == null) || (d.width != offScreenSize.width) ||
	 (d.height != offScreenSize.height)) 
	{
	  offScreenImage = createImage(d.width, d.height);
	  offScreenSize = d;
	  offScreenGraphics = offScreenImage.getGraphics();
	  offScreenGraphics.setFont(getFont());
	}
      theG.setColor(Color.black);
      offScreenGraphics.fillRect(0,0,d.width, d.height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

  public final void paint(Graphics g) {
    Dimension d = size();

// Clear screen
    g.setColor(Color.black);
    g.fillRect(0,0,d.width, d.height);

// SineWave in x direction

     degree+=degree_inc;

     if (degree>=max || degree<=0)
	degree_inc=-degree_inc;

// Create array with room for fun effects
     for (int i=0; i<(new_image_w*new_image_h); i++) {
	     to_pixels[i]=0;
     }


     for(int j=0; j<image_height; j++) {
	int amplituda = (int)(Math.abs((int)(image_height/2-j))/2);
	int sign = 1;
	if (j<(image_height/2))
		sign=-1;
//	double sintemp = Math.sin(((j+degree)*inc*deg_inc)/freq);
	double sintemp = Math.cos((degree*inc*deg_inc)/freq);
//	double sintemp2 = Math.sin(((j+degree)*inc*deg_inc)/freq2);
	int row = j*image_width;
     	for(int i=0; i<image_width; i++) {
//		int sinval = (int)(amplitud*(sintemp+Math.sin(((i+degree)*inc*deg_inc2)/freq2)));
//		int sinval2 = (int)(amplitud*(Math.sin(((degree+i)*deg_inc3*inc)/freq)+sintemp2));
		int sinval=(int)(amplituda*sintemp);
		int sinval2=0;
		int pos = row+i;	
//		int pos2 = ((j+amplitud*2+sinval)*new_image_w)+i+amplitud*3+sinval2;
		int pos2 = ((j-sign*sinval+image_height)*new_image_w)+i;
		int value = image_pixels[pos];	
		if (value!=image_pixels[0]) {
			to_pixels[pos2] = value;
		}
	}
     }
        	
      // make it
     AnimImage = createImage(new MemoryImageSource(new_image_w,new_image_h,to_pixels,0,new_image_w));

     // make it real
     prepareImage(AnimImage, this);
   
    g.drawImage(AnimImage, posx, posy, this);

    frames++;
    fps = (frames*10000) / (System.currentTimeMillis()-firstFrame);
    g.setColor(Color.yellow);
    g.drawString( "Applet by magnus@netsolutions.se", 10, d.height-20);
    g.drawString(fps/10 + "." + fps%10 + " fps", d.width-100, d.height - 20);

    }
} // end wave
