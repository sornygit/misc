
// applet "Waves an Image"

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

public class ImageWave extends Applet implements Runnable {
    private Thread traden = null;
    private Image base_image;
    private int degree = 0;
    private long firstFrame, frames, fps;
    private Image offScreenImage;
    private Image animationImage;
    private Graphics offScreenGraphics;
    private Dimension offScreenSize;
    private MediaTracker tracker;
    int delay = 50;
    private int image_width = 0, image_height = 0, new_image_w = 0, new_image_h = 0;
    private int image_pixels[];
    private int to_pixels[];
    private int amplitud = 20;
    // 1 degree = x radians
    private float inc = (float) 0.0174;
    private int degree_inc = 1;
    private int deg_inc = 1;
    private int deg_inc2 = 1;
    private int freq = 1;
    private int freq2 = 1;
    private int deg_inc3 = 1;
    int max = 360;
    private int posx = 1;
    private int posy = 1;
    Dimension dimension;

    public void init() {
        createMediaTracker();

        getInputParameters();

        loadAndPrepareImage();

        setUpImageData();

        grabPixelsToImageData();

        degree = 0;
        dimension = size();
    }

    public final synchronized void update(Graphics theG) {
        // Double-buffering routines (offscreen image creation)
        prepareOffscreenGraphics(theG, dimension);
        paint(offScreenGraphics);
        theG.drawImage(offScreenImage, 0, 0, null);
    }

    public final void paint(Graphics g) {
        clearScreen(g);

        // Increase degree in sinus wave
        degree += degree_inc;

        if (degree >= max || degree <= 0)
            degree_inc = -degree_inc;

        createImageEffectArray();

        calculateEffectValues();

        animationImage = createAnimationImage();

        prepareImage(animationImage, this);

        drawEffectImage(g);

        frames++;
        fps = calculateFramesPerSecond();
        drawExtraTexts(g);
    }

    public void start() {
        //Create a thread and start it
        if (traden == null) {
            traden = new Thread(this);
            firstFrame = System.currentTimeMillis();
            frames = 0;
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

    // --------------- Private methods

    private void createMediaTracker() {
        tracker = new MediaTracker(this);
    }

    private void grabPixelsToImageData() {
        PixelGrabber pixel_grabber;
        pixel_grabber = new PixelGrabber(base_image.getSource(), 0, 0,
                image_width, image_height, image_pixels, 0, image_width);

        try {
            pixel_grabber.grabPixels();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpImageData() {
        image_width = base_image.getWidth(this);
        image_height = base_image.getHeight(this);
        new_image_w = image_width + amplitud * 8;
        new_image_h = image_height + amplitud * 8;

        // Create array where we grab the pixels
        image_pixels = new int[image_width * image_height];
        to_pixels = new int[new_image_w * new_image_h];
    }

    private void getInputParameters() {
        amplitud = Integer.valueOf(getParameter("amplitud"));
        inc = Integer.valueOf(getParameter("inc")) * inc;
        degree_inc = Integer.valueOf(getParameter("degree"));
        deg_inc = Integer.valueOf(getParameter("inc"));
        deg_inc2 = Integer.valueOf(getParameter("inc2"));
        deg_inc3 = Integer.valueOf(getParameter("inc3"));
        freq2 = Integer.valueOf(getParameter("freq2"));
        freq = Integer.valueOf(getParameter("freq"));
        posx = Integer.valueOf(getParameter("x"));
        posy = Integer.valueOf(getParameter("y"));
        delay = Integer.valueOf(getParameter("delay"));
        max = 360000;
    }

    private void loadAndPrepareImage() {
        // base_image
        try {
            base_image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(getParameter("img")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tracker.addImage(base_image, 0);
        // load it all before continuing
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // so to see
        prepareImage(base_image, this);
    }

    private void prepareOffscreenGraphics(Graphics theG, Dimension d) {
        if ((offScreenImage == null) || (d.width != offScreenSize.width) ||
                (d.height != offScreenSize.height)) {
            offScreenImage = createImage(d.width, d.height);
            offScreenSize = d;
            offScreenGraphics = offScreenImage.getGraphics();
            offScreenGraphics.setFont(getFont());
        }
        theG.setColor(Color.black);
        offScreenGraphics.fillRect(0, 0, d.width, d.height);
    }

    private long calculateFramesPerSecond() {
        return (frames * 10000) / (System.currentTimeMillis() - firstFrame);
    }

    private void drawExtraTexts(Graphics g) {
        g.setColor(Color.yellow);
        g.drawString("Applet by magnus@netsolutions.se", 10, dimension.height - 20);
        g.drawString(fps / 10 + "." + fps % 10 + " fps", dimension.width - 100, dimension.height - 20);
    }

    private void drawEffectImage(Graphics g) {
        g.drawImage(animationImage, posx + 1, posy, this);
        g.drawImage(animationImage, posx - 1, posy, this);
        g.drawImage(animationImage, posx, posy - 1, this);
        g.drawImage(animationImage, posx, posy + 1, this);
        g.drawImage(animationImage, posx, posy, this);
    }

    private Image createAnimationImage() {
        return createImage(new MemoryImageSource(new_image_w, new_image_h, to_pixels, 0, new_image_w));
    }

    private void calculateEffectValues() {
        for (int j = 0; j < image_height; j++) {
            double sintemp = Math.sin(((j + degree) * inc * deg_inc) / freq);
            double sintemp2 = Math.sin(((j + degree) * inc * deg_inc) / freq2);
            int row = j * image_width;
            for (int i = 0; i < image_width; i++) {
                int sinval = (int) (amplitud * (sintemp + Math.sin(((i + degree) * inc * deg_inc2) / freq2)));
                int sinval2 = (int) (amplitud * (Math.sin(((degree + i) * deg_inc3 * inc) / freq) + sintemp2));
                int pos = row + i;
                int pos2 = ((j + amplitud * 2 + sinval) * new_image_w) + i + amplitud * 3 + sinval2;
                int value = image_pixels[pos];
                if (value != image_pixels[0]) {
                    to_pixels[pos2] = value;
                }
            }
        }
    }

    private void createImageEffectArray() {
        for (int i = 0; i < (new_image_w * new_image_h); i++) {
            to_pixels[i] = 0;
        }
    }

    private void clearScreen(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, dimension.width, dimension.height);
    }
}
