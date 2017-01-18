/*
	StarField class by Magnus Mickelsson 970512.

	 (C) 1997 Magnus Mickelsson. All rights reserved!

 */

import com.sun.deploy.ui.ImageLoader;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class StarField extends Applet implements Runnable {
    Thread traden = null;
    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private Dimension offScreenSize;
    String paramstr;
    String texton;
    int stars;
    int stardata[][];
    int xspeed_bk = 0;
    int yspeed_bk = 0;
    int zspeed_bk = 0;
    int xspeed = 0;
    int yspeed = 0;
    int zspeed = 2;
    int delay = 10;
    float intohyper_inc = (float) 0.01;
    float intohyper = (float) intohyper_inc;
    double sindelay = 1.8;
    double sindelay2 = 1.56;
    double sincount = 0.0;
    double sininc = 0.001;
    boolean nolink = false;
    boolean over = false;
    boolean loadedImage = false;
    int time = 0;
    FontMetrics fm = null;
    private Image logga = null;
    MediaTracker track = null;

    // Size of the starbox = -zsize -> zsize, etc. in all directions
    int zsize = 250;
    int xsize = 250;
    int ysize = 250;

    // Perspective constant
//    final int p=(int) (1/0.0033);
    final float p = (float) 0.0033;

    public void init() {

// Get parameters for the applet
        paramstr = "300"; //getParameter("stars");

// Convert parameters from strings to integers
        if (paramstr != null)
            stars = Integer.valueOf(paramstr).intValue();
        else
            stars = new Integer(30).intValue();

        paramstr = getParameter("zspeed");

        if (paramstr != null)
            zspeed = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("delay");

        if (paramstr != null)
            delay = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("zsize");

        if (paramstr != null)
            zsize = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("xspeed");

        if (paramstr != null)
            xspeed = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("yspeed");

        if (paramstr != null)
            yspeed = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("ysize");

        if (paramstr != null)
            ysize = Integer.valueOf(paramstr).intValue();

        paramstr = getParameter("xsize");

        if (paramstr != null)
            xsize = Integer.valueOf(paramstr).intValue();

        texton = "on"; //getParameter("on-off");

        track = new MediaTracker(this);

        paramstr = getParameter("image");
        try {
            logga = ImageIO.read(getClass().getClassLoader().getResourceAsStream("NSAB.GIF"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        track.addImage(logga, 0);

        paramstr = "http://www.google.se";//new String(getParameter("url"));

        if (paramstr == null)
            nolink = true;

        stardata = new int[stars + 1][3];
        make_stardata(stardata);
    }

    public void start() {
        //Create a thread and start it
        if (traden == null) {
            traden = new Thread(this);
            traden.start();
        }
    }

    public void stop() {
        //Stop animation thread
        traden = null;
    }

    public void run() {
        long time = System.currentTimeMillis();

        while (traden != null) {
            try {
                time += delay;
                Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
            } catch (InterruptedException e) {
            }

            repaint();
        }
    }

    public final synchronized void update(Graphics theG) {
        Dimension d = size();
        if ((offScreenImage == null) || (d.width != offScreenSize.width) ||
                (d.height != offScreenSize.height)) {
            offScreenImage = createImage(d.width, d.height);
            offScreenSize = d;
            offScreenGraphics = offScreenImage.getGraphics();
            offScreenGraphics.setFont(getFont());
        }
        offScreenGraphics.fillRect(0, 0, d.width, d.height);
        paint(offScreenGraphics);
        theG.drawImage(offScreenImage, 0, 0, null);
    }

    public void paint(Graphics g) {
        Dimension d = size();
        int realx, realy, perspx1, perspx2, perspy1, perspy2;
        Rectangle r = bounds();
        int xx = 0, yy = 0, zz = 0, xxx = 0, yyy = 0, zzz = 0;
        int ant = (int) Math.floor((zsize * 2) / 255);
        int avst1 = 0, avst2 = 0;

        if (fm == null)
            fm = g.getFontMetrics(getFont());


        // Handle the sinus calculations of the speed
        sincount += sininc;
        if (sincount >= 314.0 * 2.0)
            sincount = 0.0;


        g.setColor(Color.black);
        g.fillRect(0, 0, r.width, r.height);

        if (over) {

            if ((intohyper <= 1.0 && intohyper_inc > 0) || (intohyper > 0.0 &&
                    intohyper_inc < 0)) {
                intohyper += intohyper_inc;
                xspeed = Math.round(xspeed_bk * (1 + intohyper * 5));
                yspeed = Math.round(yspeed_bk * (1 + intohyper * 5));
                zspeed = Math.round(zspeed_bk * (1 + intohyper * 5));
            }

            if (intohyper <= 0.0 && intohyper_inc < 0) {
                xspeed = xspeed_bk;
                yspeed = yspeed_bk;
                zspeed = zspeed_bk;
                over = false;
            }

            xx = (int) Math.round(xspeed * Math.sin(sincount * sindelay));
            yy = (int) Math.round(yspeed * Math.sin(sincount * sindelay));
            zz = (int) Math.round(zspeed * Math.sin(sincount));

            xxx = Math.round(((float) xx) * intohyper);
            yyy = Math.round(((float) yy) * intohyper);
            zzz = Math.round(((float) zz) * intohyper);
        } else {
            xx = (int) Math.round(xspeed * Math.sin(sincount * sindelay));
            yy = (int) Math.round(yspeed * Math.sin(sincount * sindelay2));
            zz = (int) Math.round(zspeed * Math.cos(sincount * sindelay2));
        }


        for (int i = 0; i < stars; i++) {
            int converter = stardata[i][2] + zsize;
            int depth = (converter * 255) / (2 * zsize);

            Color col = new Color(depth, depth, depth);
            g.setColor(col);


            if (over) {
                float zdiv1 = (1 - (stardata[i][2] - zzz) * p);
                float zdiv2 = (1 - (stardata[i][2] + zzz) * p);

                perspx1 = Math.round((stardata[i][0] - xxx) / zdiv1) + xsize;
                perspx2 = Math.round((stardata[i][0] + xxx) / zdiv2) + xsize;
//                perspy1 = xsize + Math.round((stardata[i][1] - xxx) / zdiv1);
//                perspy2 = xsize + Math.round((stardata[i][1] + xxx) / zdiv2);


                perspy1 = ysize - Math.round((stardata[i][1] - yyy) / (1 - (stardata[i][2] - zzz) * p));

                perspy2 = ysize - Math.round((stardata[i][1] + yyy) / (1 - (stardata[i][2] + zzz) * p));

                avst1 = perspx1 - perspx2;
                if (avst1 < 0)
                    avst1 *= -1;

                avst2 = perspy1 - perspy2;
                if (avst2 < 0)
                    avst2 *= -1;

                if (avst1 <= xsize && avst2 <= ysize) {
                    g.drawLine(perspx1, perspy1, perspx2, perspy2);
                }
            } else {
                float zdiv = (1 - stardata[i][2] * p);
                realx = Math.round(stardata[i][0] / zdiv) + xsize;
                realy = ysize - Math.round(stardata[i][1] / zdiv);
                g.fillRect(realx, realy, 2, 2);
            }

// Boundary conditions & adding velocities..

            stardata[i][0] += xx;
            if (stardata[i][0] < -xsize)
                stardata[i][0] = xsize;

            if (stardata[i][0] > xsize)
                stardata[i][0] = -xsize;

            stardata[i][1] += yy;

            if (stardata[i][1] < -ysize)
                stardata[i][1] = ysize;

            if (stardata[i][1] > ysize)
                stardata[i][1] = -ysize;

            stardata[i][2] += zz;

            if (stardata[i][2] < -zsize)
                stardata[i][2] = zsize;

            if (stardata[i][2] > zsize)
                stardata[i][2] = -zsize;

        }

        if (!loadedImage) {
            g.drawImage(logga, d.width / 2 - logga.getWidth(this) / 2,
                    d.height / 2 - logga.getHeight(this) / 2, this);
        } else if (track.checkAll(true)) {
            g.drawImage(logga, d.width / 2 - logga.getWidth(this) / 2,
                    d.height / 2 - logga.getHeight(this) / 2, this);
            loadedImage = true;
        }
        if (over && texton.equals("on")) {
            g.setColor(Color.white);
            g.drawString("Applet by magnus@netsolutions.se. Click to enter hyperspace!", 5, d.height - 10);
        }
        g.setColor(Color.black);

        return;
    }

    public void make_stardata(int data[][]) {
        for (int i = 0; i < stars; i++) {
            data[i][0] = (int) Math.round(Math.random() * xsize * 2) - xsize;
            data[i][1] = (int) Math.round(Math.random() * ysize * 2) - ysize;
            data[i][2] = (int) Math.round(Math.random() * zsize * 2) - zsize;
        }
    }

    public boolean mouseDown(Event evt, int x, int y) {
        try {
            AppletContext ac = getAppletContext();
            if (paramstr == null) {
                showStatus("No URL...");
            } else
                ac.showDocument(new URL(paramstr), "_self");
        } catch (Exception ex) {
            System.out.println("Exception...");
            showStatus("strange URL...");
        }

        return true;
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        over = true;

        if (intohyper_inc >= 0) {
            xspeed_bk = xspeed;
            yspeed_bk = yspeed;
            zspeed_bk = zspeed;
        }

        if (intohyper_inc < 0)
            intohyper_inc = (float) -1.0 * intohyper_inc;
        else
            intohyper_inc = (float) 0.01;


        if (paramstr != null)
            showStatus("Click to enter hyperspace, destination: " + paramstr);
        else
            showStatus("Java applet (C) Magnus Mickelsson 1997.");

        return true;
    }

    public boolean mouseExit(Event evt, int x, int y) {
        AppletContext ac = getAppletContext();

        intohyper_inc = (float) -1.0 * intohyper_inc;

        showStatus("All is cool.");

        return true;
    }


}
