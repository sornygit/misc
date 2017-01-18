/*
	ThreeDObject class by Magnus Mickelsson 990107.

	 (C) 1999 Magnus Mickelsson. All rights reserved!

 */

import java.applet.Applet;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public class ThreeDObject extends Applet implements Runnable{

	private Thread	trThread = null;
	private Image	imOffScreenImage;
	private Graphics	grOffScreenGraphics;
	private Dimension	dOffScreenSize;
	private FontMetrics fm=null;
	ThreeDPolygon	front;
	private Vector	Object;
	boolean		shade = false;
	private int	fill=2;
	int scale = 1;
	int translate = 10;

	// Mouse control variables
	boolean		mouse = false;
	private int	mousex, mousey;
	private int	mousedragx, mousedragy;
	
	// String for getting parameters
	String		paramstr;

	// Variables for keeping track of frame speed
	long 		firstFrame, frames, fps;
	private int	intFrameRate=10;

	// With which degree the object rotates in yz, xy and xz-axises
	int rotyz = 0;
	int rotxy = 0;
	int rotxz = 0;

	// If they are 0, use varying rotation degrees instead.
	int rotyz_change = 0;
	int rotxy_change = 0;
	int rotxz_change = 0;

	// Constants for rotation if static rotation angles = 0.
	// rot_factors determine the sinus and cosinus of the rotations

	int rot_factor1 = 30;
	int rot_factor2 = 30;
	int rot_factor3 = 30;
	int rot_max = 6;
	int degree = 0;

	// Distance to point of perspective from x=0, y=0, z=0
	private int DPRP = 100;

	// Distance to view plane from same point
	private int DVPL = 150;

	public void init() {

		// Get parameters for the applet and
		// convert parameters from strings to integers

		paramstr=getParameter("fill");
		if (paramstr != null)
			fill=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("prp");
		if (paramstr != null)
			DPRP=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("vpl");
		if (paramstr != null)
			DVPL=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rotyz");
		if (paramstr != null)
			rotyz=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rotxz");
		if (paramstr != null)
			rotxz=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rotxy");
		if (paramstr != null)
			rotxy=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rotmax");
		if (paramstr != null)
			rot_max=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rot1");
		if (paramstr != null)
			rot_factor1=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rot2");
		if (paramstr != null)
			rot_factor2=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("rot3");
		if (paramstr != null)
			rot_factor3=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("scale");
		if (paramstr != null)
			scale=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("delay");
		if (paramstr != null)
			intFrameRate=Integer.valueOf(paramstr).intValue();

		paramstr=getParameter("shade");
		if (paramstr.equals("on"))
			shade = true;

		// Here we make the object out of some polygons

		front = new ThreeDPolygon();
	
// FRONT polygon

		// Upper front left

		front.addPoint(-10.0, 10.0, 10.0);

		// Upper front right corner
		front.addPoint(10.0, 10.0, 10.0);

		// Lower front right
		front.addPoint(10.0, -10.0, 10.0);

		// Lower front left
		front.addPoint(-10.0, -10.0, 10.0);

		// Set color of polygon
		front.setColor(255,255,255);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		Object = new Vector();

		// Add polygon to object
		Object.addElement(front);

// TOP  polygon

		front = new ThreeDPolygon();
	
		// Add another polygon

		// Upper front left

		front.addPoint(-10.0, 10.0, 10.0);

		// Upper back left corner
		front.addPoint(-10.0, 10.0, -10.0);

		// Upper back right
		front.addPoint(10.0, 10.0, -10.0);

		// Upper front right
		front.addPoint(10.0, 10.0, 10.0);

		// Set color of polygon
		front.setColor(255,0,0);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		// Add polygon to object
		Object.addElement(front);

// LEFT polygon

		front = new ThreeDPolygon();

		// Add another polygon

		// Upper front left

		front.addPoint(-10.0, 10.0, 10.0);

		// Lower front left corner
		front.addPoint(-10.0, -10.0, 10.0);

		// Lower back left
		front.addPoint(-10.0, -10.0, -10.0);

		// Upper back left
		front.addPoint(-10.0, 10.0, -10.0);

		// Set color of polygon
		front.setColor(0,255,0);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		// Add polygon to object
		Object.addElement(front);

// RIGHT polygon

		front = new ThreeDPolygon();

		// Add another polygon

		// Upper front right

		front.addPoint(10.0, 10.0, 10.0);

		// Upper back right corner
		front.addPoint(10.0, 10.0, -10.0);

		// Lower back right
		front.addPoint(10.0, -10.0, -10.0);

		// Lower front right
		front.addPoint(10.0, -10.0, 10.0);

		// Set color of polygon
		front.setColor(0,0,255);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		// Add polygon to object
		Object.addElement(front);

// BOTTOM polygon

		front = new ThreeDPolygon();

		// Add another polygon

		// Lower front left

		front.addPoint(-10.0, -10.0, 10.0);

		// Lower front right
		front.addPoint(10.0, -10.0, 10.0);

		// Lower back right
		front.addPoint(10.0, -10.0, -10.0);

		// Lower back left
		front.addPoint(-10.0, -10.0, -10.0);

		// Set color of polygon
		front.setColor(255,255,0);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		// Add polygon to object
		Object.addElement(front);

// BACK polygon

		front = new ThreeDPolygon();
	
		// Add another polygon

		// Upper back left

		front.addPoint(-10.0, 10.0, -10.0);

		// Lower back left corner
		front.addPoint(-10.0, -10.0, -10.0);

		// Lower back right
		front.addPoint(10.0, -10.0, -10.0);

		// Upper back right
		front.addPoint(10.0, 10.0, -10.0);

		// Set color of polygon
		front.setColor(255,0,255);

		// Set scale of object
		front.Scale(scale);
		front.Translate(0,0,translate);

		// Set distance to perspective point and view plane
		front.setPRP(0, 0, DPRP);
		front.setCW(0, 0, DVPL);
		front.setCentre((int)Math.round(size().width/2), (int)Math.round(size().height/2));

		// Add polygon to object
		Object.addElement(front);

// Final result: a CUBE!
               		showStatus("Click and drag your mouse on object to control rotation.");

	 }

	public void start()
	{
		//Create a thread and start it

		if (trThread == null)
		{
			trThread = new Thread(this);
			trThread.start();
			firstFrame=System.currentTimeMillis();
			frames = 0;
		}

	}

	public void stop()
	{
		//Stop animation thread

		trThread = null;

	}

	public void run()
	{
		long time = System.currentTimeMillis();

		while (trThread != null)
		{
			try
			{
				// Set the delay of the drawing

				time += intFrameRate;
				Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
			}
			catch (InterruptedException e)
			{
			}

			// Call a repaint of the object and canvas
			repaint();
		}
	}

	public boolean mouseDown(Event evt, int x, int y)
	{

		if (mouse)
		{
                		showStatus("Rotation restored. Click to control rotation.");
			mouse = false;
		}
		else
		{
                		showStatus("Mouse clicked at x="+x+", y="+y+". User now controls rotation.");
			mouse = true;
			mousedragx = x;
			mousedragy = y;
		}

		return true;

	}

	public boolean mouseDrag(Event evt, int x, int y)
	{

               		showStatus("Mouse dragged from x="+mousedragx+", y="+mousedragy+" to x="+x+", y="+y+".");
		mousex = (int)Math.round(10*(x-mousedragx)/size().width);
		mousey = (int)Math.round(10*(y-mousedragy)/size().height);

		return true;

	}

	public final synchronized void update (Graphics grG)
	{
		// The double-buffering routine, makes sure drawing is made to a hidden image. Then we switch the images.

	     	Dimension d = size();
      		if ((imOffScreenImage == null) || (d.width != dOffScreenSize.width) || (d.height != dOffScreenSize.height))
		{
			imOffScreenImage = createImage(d.width, d.height);
			dOffScreenSize = d;
			grOffScreenGraphics = imOffScreenImage.getGraphics();
			grOffScreenGraphics.setFont(getFont());
		}
    	
		grG.setColor(Color.black);

		grOffScreenGraphics.fillRect(0,0,d.width, d.height);

	    	paint(grOffScreenGraphics);

		grG.drawImage(imOffScreenImage, 0, 0, null);
	}

	public final synchronized void paint(Graphics gr)
	{
		Rectangle	r=bounds();
		Vector		ZSorted;

		ZSorted = ZSort(Object);

		if (fm==null)
			fm=gr.getFontMetrics(getFont());

		gr.setColor(Color.black);
		gr.fillRect(0, 0, r.width, r.height);

		gr.setColor(Color.white);
		gr.drawString("DUH! Magnus was here.", 100, 100);

		degree++;
		if (degree>360000)
			degree = 360000;

		if ((!mouse) && rotxy==0 && rotxz==0 && rotyz==0)
		{ 
			rotxy_change = (int)Math.round(rot_max * Math.sin(0.6*degree*Math.PI/180));
			rotxz_change = (int)Math.round(rot_max * Math.cos((rot_factor2/100)*degree*Math.PI/180));
			rotyz_change = (int)Math.round(rot_max * Math.sin((rot_factor3/100)*degree*Math.PI/180));
		}

		// Go through the vector of Z-sorted polygons and rotate and draw them

		for (Enumeration e = ZSorted.elements(); e.hasMoreElements();)
		{
			ThreeDPolygon p = (ThreeDPolygon)e.nextElement(); 

			if ((!mouse) && rotxy==0 && rotxz==0 && rotyz==0)
			{ 
				// Make rotation
				p.Rotate(rotxy_change, rotxz_change, rotyz_change);
			}
			else{
				if (!mouse)
				{
					// Make rotation
					p.Rotate(rotxy, rotxz, rotyz);
				}
				else
				{
					p.Rotate(0, mousex, mousey);
				}
			}

			// Paint the object, not shaded but filled
			p.paintPolygon(gr, shade, fill);
		}
		
		gr.setColor(Color.white);

		frames++;
		fps = (frames*10000) / (System.currentTimeMillis()-firstFrame);
		gr.drawString(fps/10 + "." + fps%10 + " fps", 2, size().height - 20);

		gr.setColor(Color.black);

		return;
    	}

	// Find the index of the biggest z-valued point in a vector
	private final int FindBiggest(Vector scan)
	{
		double biggest = -10000.0;
		int index = -1;

		for (Enumeration e=scan.elements(); e.hasMoreElements();)
		{
			ThreeDPolygon p = (ThreeDPolygon)e.nextElement();
			double big = p.getZvalue();

			if (big>biggest)
			{
				index = scan.indexOf(p);
				biggest = big;
			}
		}

		return index;
	}

	// Sort the Polygons in Z-Order

	public Vector ZSort(Vector unsorted)
	{
		Vector sorted = new Vector();
		Vector tmp = (Vector)unsorted.clone();
		int ind = 0;		

		while (tmp.size()>0 && ind >-1)
		{
			ind = FindBiggest(tmp);
			sorted.addElement(tmp.elementAt(ind));
			tmp.removeElementAt(ind);
		}

		return sorted;

	}
}
