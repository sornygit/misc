/*

	ImageEffects class by Magnus Mickelsson 990224.
	 (C) 1999 Magnus Mickelsson. All rights reserved!

 */

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.DirectColorModel;
import java.awt.Image;
import java.awt.Event;
import java.awt.image.PixelGrabber;
//import java.net.URL;
//import java.net.MalformedURLException;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.MediaTracker;
import java.awt.Dimension;
import java.lang.System;
import java.lang.Runtime;

public class ImageEffects extends Applet implements Runnable{

	// Major global variables
	private Thread				trThread;
	private Image				imOffScreen;
	private Graphics			grOffScreen, grG;
	private Dimension			dOffScreen;
	private MediaTracker			mtTrack;
	private Image				imSource;
	private MemoryImageSource		misDest;
	private static final DirectColorModel 	dcmModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

	private int[]				arSource, arDest;
	private double[]			arRender;
	private int				intPixels;

	private int				intDestW, intDestH;
	private double				dblRadian = (Math.PI/180);
	private double				deg = Math.PI / 180;

	// Variables for image dimensions
	private int				intW;
	private int				intH;
	
	// RGB values for pixel being treated
	private int				intR;
	private int				intG;
	private int				intB;
	
	// Applet width / height
	private int				intAppletW;
	private int				intAppletH;
	private int				intAppletD;

	// status: 0 = init, 1 = running
	private int				intStatus = 0;

	// Special fx variable, step to fade
	private int				intAdd = 4;

	// Variables for keeping track of frame speed
	private long 				firstFrame, frames, fps;
	private int				intFrameRate = 10;

	// Constants for rotation if static rotation angles = 0
	// rot_factors determine the sinus and cosinus of the rotations

	int					rot_factor1 = 30;
	int					rot_factor2 = 30;
	int					rot_factor3 = 30;
	int					rot_max = 6;
	int					degree = 0;
	int					scale = 1;
	int					centre_x = 0;
	int					centre_y = 0;
	int					rotxy = 0;
	int					rotyz = 0;
	int					rotxz = 0;
	int					pixeldepth = 1;
	int					maxz;
	int					imagez;
	int					shading = 0;
	int					interpolate = 0;
	int					bgcolor = 0;
	int					mouse = 0;
	int					mousex=0, mousey=0;
	int					dx=0, dy=0;
	int					mouseon = 0;
	int					firstframe = 0;
	int					shadecol = 0;

	// Distance to point of perspective from x=0, y=0, z=0
	private int				DPRP = 140;

	// Distance to view plane from same point
	private int				DVPL = 200;

	// Distance to Centre of Window
	private int				DCW = 0;

	// Typical applet function

	public void init()
	{
		// Get parameters for the applet and
		// convert parameters from strings to integers
		intFrameRate = GetIntParameter("delay");
		intAdd = GetIntParameter("fadespeed");
		DPRP = GetIntParameter("distance");
		DCW = GetIntParameter("cw");

		scale = GetIntParameter("scale");
		mouse = GetIntParameter("mouse");
		
		shading = GetIntParameter("shading");
		bgcolor = GetIntParameter("bgcolor");
		interpolate = GetIntParameter("interpolate");

		imagez = GetIntParameter("z");

		rotxy = GetIntParameter("xy");
		rotyz = GetIntParameter("yz");
		rotxz = GetIntParameter("xz");

		pixeldepth = GetIntParameter("pixeldepth");

		// Set background color
		setBackground(Color.black);

		// Get applet size
		intAppletW = size().width;
		intAppletH = size().height;

		centre_x = (int)Math.round(intAppletW / 2);
		centre_y = (int)Math.round(intAppletH / 2);
		
	 }

	public void start()
	{
		//Create a thread and start it

		if (trThread == null)
		{
			trThread = new Thread(this);
			trThread.start();
			firstFrame = System.currentTimeMillis();
			frames = 0;

			// Make sure thread has low priority
			trThread.setPriority(Thread.MIN_PRIORITY);

		}


	}

	public void stop()
	{
		//Stop animation thread
		if (trThread != null)
			trThread = null;

	}

	public void run()
	{
		long time = System.currentTimeMillis();

		while (trThread != null)
		{
			if (intStatus == 1)
			{
				double[] rottemp;
				
				update(this.getGraphics());

				if (mouse<1 || mouseon>0 || firstframe==0)
				{
					if (mouse>0)
					{
						rotxy = 0;
						rotyz = dx;
						rotxz = dy;
					}
				
					if (firstframe==0)
						firstframe = 1;
					
					// Clear array used as Image Source
					for (int i=0; i < intAppletD; i++)
					{
							arDest[i] = bgcolor;
					}
				
					for (int i=0; i<(intPixels*4); i+=4)
					{
						int pos = -1;
						
						try
						{	
							rottemp = Rotate(rotxy,rotyz,rotxz,arRender[i],arRender[i+1],arRender[i+2]);
						
							arRender[i] = rottemp[0];
							arRender[i+1] = rottemp[1];
							arRender[i+2] = rottemp[2];
						
							pos = (int)Math.round(get2DX(arRender[i], arRender[i+1], arRender[i+2])+get2DY(arRender[i], arRender[i+1], arRender[i+2])*intDestW);
						
							if ((pos >= 0) || (pos < intAppletD))
							{
								if (shading>0)
								{
									shadecol = ShadeColor(rottemp[2], maxz, arRender[i+3]);
								}
								else
								{
									shadecol = (int)arRender[i+3];
								}

								arDest[pos] = shadecol;

//								if (arRender[i+2]<0.0)
//								{
									arDest[pos+1] = shadecol;
									arDest[pos+intAppletW] = shadecol;
									arDest[pos+intAppletW+1] = shadecol;
//								}
							}
						}
						catch (NullPointerException e)
						{
						}
					}
				
					if (interpolate>0)
						InterpolatePixels(arDest, intAppletW, intAppletH);

				}
			
				try
				{
					// Set the delay of the drawing

					time += intFrameRate;

					if (intFrameRate > 0)
					{
						Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
					}
				}
				catch (InterruptedException e)
				{
				}
			}
			else
			{
				prepareApplet();
				intStatus = 1;
			}

		}
	}

	public final synchronized void update (Graphics grG)
	{
		// The double-buffering routine, makes sure drawing is made to a hidden image. Then we switch the images.

		if (intStatus == 1)
		{
			grOffScreen.setColor(Color.black);

			grOffScreen.fillRect(0, 0, intAppletW, intAppletH);

		   	paint(grOffScreen);

			grG.drawImage(imOffScreen, 0, 0, null);
		}
		
		return;
	}


	public final synchronized void paint(Graphics gr)
	{

		if (intStatus == 1)
		{
			gr.setColor(Color.black);
			gr.fillRect(0, 0, intAppletW, intAppletH);

			frames++;
			fps = (frames*10000) / (System.currentTimeMillis()-firstFrame);
			gr.setColor(Color.white);

			gr.drawImage(MemoryImage(misDest), 0, 0, null);

			gr.drawString(fps/10 + "." + fps%10 + " fps", 2, intAppletH - 20);

		}

		return;
    	}

// Initialization function

	public void prepareApplet()
	{
		int intCount = 0;
		loadImages();

		// Get image dimensions
		intW = imSource.getWidth(this);
		intH = imSource.getHeight(this);

		if (intW>intH)
			maxz = (int)Math.round(intW/2);
		else
			maxz = (int)Math.round(intH/2);

		intDestW = intAppletW;
		intDestH = intAppletH;

		intAppletD = intAppletH * intAppletW;

		arSource = getImageArray(imSource);

		intPixels = 0;
		
		for (int i=0; i < (intW * intH); i++)
		{
			if (arSource[i] != bgcolor)
				intPixels++;
		}

		arRender = new double[(intPixels * 4)+4];
	
		// Create 3d array
		for (int i=0; i < intW; i++)
		{
			for (int j=0; j < intH; j++)
			{
				if (arSource[i+intW*j] != bgcolor)
				{
					arRender[intCount] = i-(intW/2);
					arRender[intCount+1] = j-(intH/2);
					arRender[intCount+2] = 1.0 * imagez;
					arRender[intCount+3] = arSource[i+intW*j];
					
					intCount += 4;
				}
			}
		}

		arDest = new int[intDestW * intDestH];

		misDest = new MemoryImageSource(intDestW, intDestH, dcmModel, arDest, 0, intDestW);

		// Double buffer parameters
		imOffScreen = createImage(intAppletW, intAppletH);
		dOffScreen = size();
		grOffScreen = imOffScreen.getGraphics();
		grOffScreen.setFont(getFont());

		System.gc();
	}

// Image and pixel handling functions

	public void loadImages()
	{
		mtTrack = new MediaTracker(this);
		
		try
		{
			imSource = ImageIO.read(getClass().getClassLoader().getResourceAsStream(GetStrParameter("image")));//getImage(getDocumentBase(), GetStrParameter("image"));
		}
		catch (Exception e)
		{
			System.exit(0);
		}
		
		mtTrack.addImage(imSource, 0);

		try
		{
			mtTrack.waitForID(0);
		}
		catch (InterruptedException e)
		{
			System.exit(0);
		}
		
	}

	public int PixelValue(int r, int g, int b)
	{
		return ((int)((r << 16) | (g << 8) | b));
	}

	public int RValue(int r)
	{
		return ((r & 0xff0000)>>16);
	}

	public int GValue(int r)
	{
		return ((r & 0x00ff00)>>8);
	}

	public int BValue(int r)
	{
		return ((r & 0x0000ff));
	}

 	public Image ArrayImage(int[] pix, int w, int h)
	{
		Image	tmpImage;
		tmpImage = (createImage(new MemoryImageSource(w, h, dcmModel, pix, 0, w)));
		
		return(tmpImage);
	}

 	public Image MemoryImage(MemoryImageSource mis)
	{
		return (createImage(mis));
	}

	public int[] getImageArray(Image im)
	{
		PixelGrabber pxGrab;
		int w = im.getWidth(this);
		int h = im.getHeight(this);
		int[] arTemp = new int[w*h];

		pxGrab = new PixelGrabber(im, 0, 0, w, h, arTemp, 0, w);
	
		try
		{
			pxGrab.grabPixels();
		}
		catch (InterruptedException e)
		{
			System.exit(0);
		}
		
		return (arTemp);
	}


	public int GetAverage(int[] values)
	{
		int thesum = 0;
		
		for (int i=0; i<values.length; i++)
		{
			thesum += values[i];
		}
		
		return Math.round(thesum/(values.length));
	}

	public int GetMostLikelyValue(int[] values)
	{
		int thevalue=0;
		int theaverage=0;

		// Max difference
		int diff=255, currdiff;

		theaverage = GetAverage(values);
		
		for (int i=0; i<values.length; i++)
		{
			currdiff = Math.abs(values[i]-theaverage);
			
			if (currdiff<diff)
			{
				thevalue = values[i];
				diff = currdiff;
			}
		}
		return thevalue;	
		
	}


	public void InterpolatePixels(int[] fpixarray, int fwidth, int fheight)
	{
		int index = 0;
		int r, g, b;
		int[] rr, gg, bb;
		int frame = (pixeldepth*2+1);
		int c;
		
		rr = new int[frame*frame];
		gg = new int[frame*frame];
		bb = new int[frame*frame];
		
		for (int i=pixeldepth; i<(fwidth-pixeldepth); i++)
		{
			for (int j=pixeldepth; j<(fheight-pixeldepth); j++)
			{
				index = (i+j*fwidth);
			
				// If current pixel has bgcolor, and some pixels around it does not, then interpolate..
				if ((fpixarray[index] == bgcolor) && ((fpixarray[index+1] != bgcolor) || (fpixarray[index+2] != bgcolor) || (fpixarray[index+fwidth] != bgcolor) || (fpixarray[index+fwidth+1] != bgcolor) || (fpixarray[index+fwidth+2] != bgcolor)))
				{
					c = 0;

					for (int k=-pixeldepth; k<pixeldepth; k++)
					{
						for (int l=-pixeldepth; l<pixeldepth; l++)
						{
							rr[c] = RValue(fpixarray[index+k+l*fwidth]);
							gg[c] = GValue(fpixarray[index+k+l*fwidth]);
							bb[c] = BValue(fpixarray[index+k+l*fwidth]);
							c++;
						}
					}

					r = GetMostLikelyValue(rr);
					g = GetMostLikelyValue(gg);
					b = GetMostLikelyValue(bb);

					fpixarray[index] = PixelValue(r, g, b);

				}

			}	
		}
	}

// 3D functions

	public int ShadeColor(double zpos, int fmaxz, double color)
	{
		// Enable shading
		double zfactor = (((-zpos+fmaxz)/(fmaxz+fmaxz))*1.5)/2+0.4;
		
		if (zpos>(fmaxz*2))
			zfactor = 0.0;
		
		int r, g, b, intcol;
		
		intcol = (int)color;
							
		r = (intcol & 0xff0000)>>16;
		g = (intcol & 0x00ff00)>>8;
		b = (intcol & 0x0000ff);
							
		// Shade the current color
		r = (int)Math.round(zfactor*r);
		g = (int)Math.round(zfactor*g);
		b = (int)Math.round(zfactor*b);

		if (r>255)
			r = 255;

		if (g>255)
			g = 255;

		if (b>255)
			b = 255;
								
		
		return PixelValue(r, g, b);

	}

// Get 2D representation of a point from perspective and stuff. This is x position.

	public int get2DX(double x, double y, double z)
	{

		return (int)(centre_x + Math.floor(scale * (x*(DPRP+DCW))/(DPRP+z)));

	}

// Get 2D y position

	public int get2DY(double x, double y, double z)
	{

		return (int)(centre_y + Math.floor(scale * (y*(DPRP+DCW))/(DPRP+z)));

	}

	// Rotates the point by axises xy, xz, yz. Rotation i degrees.

	public double[] Rotate(int xy, int xz, int yz, double x, double y, double z)
	{
		double [] temp = new double[3];

		if (xy != 0)
		{
			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = xy * deg;

			tmp1 = x * Math.cos(tmp3)  - y * Math.sin(tmp3);
			tmp2 = y * Math.cos(tmp3)  + x * Math.sin(tmp3);
			
			x = tmp1;
			y = tmp2;
		}		

		if (xz != 0)
		{
			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = xz * deg;

			tmp1 = x * Math.cos(tmp3)  - z * Math.sin(tmp3);
			tmp2 = z * Math.cos(tmp3)  + x * Math.sin(tmp3);
			
			x = tmp1;
			z = tmp2;
		}		

		if (yz != 0)
		{
			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = yz * deg;

			tmp1 = y * Math.cos(tmp3)  - z * Math.sin(tmp3);
			tmp2 = z * Math.cos(tmp3)  + y * Math.sin(tmp3);
			
			y = tmp1;
			z = tmp2;
		}

		temp[0] = x;
		temp[1] = y;
		temp[2] = z;
		
		return (temp);
	}



// Parameter extracting functions

	public int GetIntParameter(String fstrParam)
	{
		String	strTemp;
		
		strTemp = getParameter(fstrParam);
		
		if (strTemp != null)
		{
			return(Integer.valueOf(strTemp).intValue());
		}
		else
		{
			return(0);
		}
	}

	public String GetStrParameter(String fstrParam)
	{
		String	strTemp;
		
		strTemp = getParameter(fstrParam);
		
		if (strTemp != null)
		{
			return(strTemp);
		}
		else
		{
			return("");
		}
	}

// So far unneeded functions


	public boolean mouseDown(Event evt, int x, int y)
	{
		if (mouse>0)
		{
			mousex = x;
			mousey = y;	
			mouseon = 1;
		}

		return true;

	}

	public boolean mouseUp(Event evt, int x, int y)
	{
		if (mouse>0)
		{
			dx = 0;
			dy = 0;	
			mouseon = 0;
		}

		return true;

	}

	public boolean mouseDrag(Event evt, int x, int y)
	{
		if (mouse>0)
		{
			dx = Math.round((x-mousex)/50);
			dy = Math.round((y-mousey)/50);
		}

		return true;

	}

	public boolean imageUpdate(Image i, int flags, int x, int y, int w, int h) {
		return true;
	}


}
