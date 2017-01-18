// Handles shaded text
// Coded and (C) by Magnus Mickelsson 1998.
/* Parameters:
	param1 - dfsdfsfsfsd
	param2 - dsfsdfsfsdf
*/

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.StringTokenizer;

public class ShadowText extends Applet implements Runnable{
  static boolean reallydone=false;
  int	shadeposx_start[] = new int[3];
  int	shadeposx[] = new int[3];
  int	shadespeedx[] = new int[3];
  int	shadeposy_start[] = new int[3];
  int	shadeposy[] = new int[3];
  int	shadespeedy[] = new int[3];

  String stringen = new String("Testing");
  String indata;
  StringTokenizer datastrings;
  private Image offScreenImage;
  private Graphics offScreenGraphics;
  private Dimension offScreenSize;
  Thread traden=null;

  int	delay=100;
  int   width=0, height=0;
  int	centeringx=0;
  int	centeringy=0;
  int	shadespace=0;
  int	fontsize=40;
  Font	fonten;
//  Font	lilla;
  Color	shade[] = new Color[3];
  Color back;
  // 1 degree = x radians
//  float inc=(float)0.0174;

  public void init() {
    Dimension d = size();
	int R = Integer.valueOf(getParameter("R")).intValue();
	int G = Integer.valueOf(getParameter("G")).intValue();
	int B = Integer.valueOf(getParameter("B")).intValue();
	int Text1R = Integer.valueOf(getParameter("Text1R")).intValue();
	int Text1G = Integer.valueOf(getParameter("Text1G")).intValue();
	int Text1B = Integer.valueOf(getParameter("Text1B")).intValue();
	int Text2R = Integer.valueOf(getParameter("Text2R")).intValue();
	int Text2G = Integer.valueOf(getParameter("Text2G")).intValue();
	int Text2B = Integer.valueOf(getParameter("Text2B")).intValue();
	int Text3R = Integer.valueOf(getParameter("Text3R")).intValue();
	int Text3G = Integer.valueOf(getParameter("Text3G")).intValue();
	int Text3B = Integer.valueOf(getParameter("Text3B")).intValue();

// Prepare strings to show
	indata = getParameter("message");

// Avgränsa med \t \n etc.
	datastrings = new StringTokenizer(indata, "*", false);
	indata = datastrings.nextToken();
//	indata = datastrings.nextToken();

	// Create background color
	back = new Color(R, G, B);
	shade[0] = new Color(Text1R, Text1G, Text1B);
	shade[1] = new Color(Text2R, Text2G, Text2B);
	shade[2] = new Color(Text3R, Text3G, Text3B);

	height = d.height;
	width = d.width;

// Fix variables from applet parameters

// Speed of shadows    
	shadespeedx[0]=Integer.valueOf(getParameter("shade1_sp_x")).intValue();
    shadespeedx[1]=Integer.valueOf(getParameter("shade2_sp_x")).intValue();
    shadespeedx[2]=Integer.valueOf(getParameter("shade3_sp_x")).intValue();

	shadespeedy[0]=Integer.valueOf(getParameter("shade1_sp_y")).intValue();
    shadespeedy[1]=Integer.valueOf(getParameter("shade2_sp_y")).intValue();
    shadespeedy[2]=Integer.valueOf(getParameter("shade3_sp_y")).intValue();

// Start positions
	shadeposx_start[0] = Integer.valueOf(getParameter("shade1start_x")).intValue();
	shadeposy_start[0] = Integer.valueOf(getParameter("shade1start_y")).intValue();
	shadeposx_start[1] = Integer.valueOf(getParameter("shade2start_x")).intValue();
	shadeposy_start[1] = Integer.valueOf(getParameter("shade2start_y")).intValue();
	shadeposx_start[2] = Integer.valueOf(getParameter("shade3start_x")).intValue();
	shadeposy_start[2] = Integer.valueOf(getParameter("shade3start_y")).intValue();

// Set positions to startvalues
	shadeposx[0] = shadeposx_start[0];
	shadeposy[0] = shadeposy_start[0];
	shadeposx[1] = shadeposx_start[1];
	shadeposy[1] = shadeposy_start[1];
	shadeposx[2] = shadeposx_start[2];
	shadeposy[2] = shadeposy_start[2];

// Other variables
    delay=Integer.valueOf(getParameter("delay")).intValue();
    shadespace=Integer.valueOf(getParameter("shadespace")).intValue();
    fontsize=Integer.valueOf(getParameter("fontsize")).intValue();
    centeringx=Integer.valueOf(getParameter("centeringx")).intValue();
    centeringy=Integer.valueOf(getParameter("centeringy")).intValue();

	fonten = new Font("Tahoma", Font.BOLD, fontsize);
//	lilla = new Font("Tahoma", Font.BOLD, 12);
//    sin_values = new int[361];
//    sin_values2 = new int[361];

// Precalculate sin-values
/*    for (int i=0; i<361; i++) {
		sin_values[i]=(int)((amplitud-1)*Math.sin(i*inc*2));
		sin_values2[i]=(int)((amplitud-1)*Math.sin(i*inc));
    }
*/

//	System.out.println(indata);
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
      if((offScreenImage == null) || (width != offScreenSize.width) ||
	 (height != offScreenSize.height)) 
		{
		  offScreenImage = createImage(width, height);
	 	  offScreenSize = size();
	 	  offScreenGraphics = offScreenImage.getGraphics();
		  offScreenGraphics.setFont(getFont());
		}
      theG.setColor(Color.black);
      offScreenGraphics.fillRect(0,0,width, height);
      paint(offScreenGraphics);
      theG.drawImage(offScreenImage, 0, 0, null);
    }

  public void paint(Graphics g) {
	boolean done = true;
	AppletContext ac = getAppletContext();

	ac.showStatus(indata);
	g.setColor(back);
	g.fillRect(0,0, width, height);

	if (reallydone && datastrings.hasMoreElements()) {
		indata = datastrings.nextToken();
//		System.out.println(indata);
		reallydone = false;

		// Set start positions
		shadeposx[0] = shadeposx_start[0];
		shadeposy[0] = shadeposy_start[0];
		shadeposx[1] = shadeposx_start[1];
		shadeposy[1] = shadeposy_start[1];
		shadeposx[2] = shadeposx_start[2];
		shadeposy[2] = shadeposy_start[2];

	}

	if (!reallydone) {

		for (int i=0; i<3; i++) {
			if (shadeposx[i]>(centeringx+shadespace*i)) {
				shadeposx[i]-=shadespeedx[i];
				done=false;
			}	

			if (shadeposy[i]>(centeringy+shadespace*i)) {
				shadeposy[i]-=shadespeedy[i];
				done=false;
			}

			if (shadeposx[i]<(centeringx+shadespace*i)) {
				shadeposx[i]+=shadespeedx[i];
				done=false;
			}

			if (shadeposy[i]<(centeringy+shadespace*i)) {
				shadeposy[i]+=shadespeedy[i];
				done=false;
			}
		

		}

		if (done)
		{
			reallydone=true;
//			System.out.println("Nu kör vi! Reallydone!\n");
		}
	}
//	else
//		System.out.println("Reallydone.\n");

	
	g.setFont(fonten);
// Clear screen

	for (int i=0; i<3; i++) {
		g.setColor(shade[i]);
		g.drawString(indata, shadeposx[i]+shadespace*shadespeedx[0]*(3-i), shadeposy[i]-shadespeedy[0]*shadespace*i+shadespace*3);
	}

//	g.setFont(lilla);
//	g.drawString("Applet by Miffel.", 5, height-15);

//	if (!reallydone)
//		repaint();
  } // end paint

} 
// END APPLET