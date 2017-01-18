import java.awt.*;
import java.awt.image.*;
import java.applet.Applet;
import java.applet.AppletContext;

public class ImagePointer extends Applet {

    // Declare variables
    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private Dimension offScreenSize;
    Image main=null;
	String password;
	String stat;
	String loadtext;

    // Array of images to display
    Image images[];

    // The image-map image, if you know what I mean? ;)
    Image areas=null;
    String paramstr;

    // Image texts
    String texts[];
    MediaTracker track;
    int antal=0;
    boolean withinarea=false;
    boolean wireframe=false;

    // where to insert the mouseover selected image
    int picposx=0;
    int picposy=0;

    // which area is currently pointed out
    int area=0;

    // Colors of the different areas
    int colors[];

    public void init() {

        // Get parameters for the applet
		password = getParameter("applet_by");
		stat = getParameter("status");
		loadtext = getParameter("loadtext");
		System.out.println(stat);
        paramstr = getParameter("main_image");
        antal = Integer.valueOf(getParameter("antal")).intValue();
        picposx = Integer.valueOf(getParameter("bildx")).intValue();
        picposy = Integer.valueOf(getParameter("bildy")).intValue();

        // Create image-array and the mediatracker to check them
        images = new Image[antal+1];
        track = new MediaTracker(this);

        // Create text array for info texts
        texts=new String[antal+1];

        // Create color array
        colors=new int[antal+1];

        // Get main image and add it to the tracker
        main = getImage(getDocumentBase(), paramstr);
        track.addImage(main, 0);
        paramstr = getParameter("area_image");
        areas = getImage(getDocumentBase(), paramstr);
        track.addImage(areas, 0);

        paramstr = getParameter("wireframe");

        // Check if we should use the test mode, called
        // "wireframe".

        if (paramstr.equals("on"))
            wireframe=true;

        // Get all image parameters and the images themselves
        for (int i=0; i<antal; i++)
        {
            String temp;
            temp = getParameter(("image"+i));
            texts[i] = getParameter(("image"+i+"_txt"));
            colors[i] = Integer.valueOf(getParameter(("image"+i+"_color"))).intValue();

            images[i] = getImage(getDocumentBase(), temp);
            track.addImage(images[i], 0);
        }

    }

// This function creates an offscreen image so there will be no
// ugly flickering while updating..
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

        paint(offScreenGraphics);
        theG.drawImage(offScreenImage, 0, 0, null);
    }


    // This routine paints the images, depending on which
    // area is active and if we have test mode (wireframe).
    public void paint(Graphics g)
    {
        Dimension d = size();


        if (track.checkAll(true) && password.equals("magnus@netsolutions.se"))
        {
            g.setColor(Color.white);
            g.fillRect(0,0,d.width, d.height);
            g.drawImage(main, 0, 0, this);

            if (wireframe)
                g.drawImage(areas,0,0,this);

            g.drawImage(images[area], picposx, picposy, this);


    	    return;
        }
        else if (track.isErrorAny())
        {
            // If there was an image loading error
            g.setColor(Color.red);
            g.drawString("Error! Contact magnus@netsolutions.se!", 100, 120);

            return;
        }
        else
        {
            // Print this while images are loaded
            g.setColor(Color.black);
            if (loadtext!=null)
				g.drawString(loadtext, 50,50);
            g.setColor(Color.white);

            repaint(100);
        }

       return;

    }

    // Here we compare the color of the current mouseover
    // pixel (on the hidden area-image) to the color stated
    // to be a specific area. If we are over an user-defined area,
    // we set the area variable to the new area ID & we repaint the "screen".
    public boolean WithinBoundaries(int color)
    {
        boolean over=false;

        for (int i=0; i<antal; i++)
        {
                if (colors[i]==color)
                {
                    over=true;

                    if (area!=i)
                    {
                        area=i;
                        repaint();
                    }
                }
        }

        return over;

    }

    public boolean mouseEnter(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        showStatus(stat);

        return true;
    }

    public boolean mouseMove(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();
        int col[]={0,0};
        PixelGrabber pg;

        // Grab the pixel we point at, but from the hidden image.
        pg = new PixelGrabber(areas, x, y, 1, 1, col, 0, 1);

        try
        {
            pg.grabPixels();
        }
        catch (InterruptedException e)
        {
        };

// This line prints the color presently being pointed by the
// mouse. May be used to analyze the area colors in wireframe mode.

//        System.out.println("Area with color: "+col[0]);


        // Check what status message to print
        if (col[0]!=-1 && WithinBoundaries(col[0]) && track.checkAll(true))
        {
            if (texts[area]!=null)
                showStatus(texts[area]);
            else
                showStatus("This is section "+area+".");
        }
        else
            showStatus(stat);

        return true;
    }

    public boolean mouseExit(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        showStatus("");

        return true;
    }

}
