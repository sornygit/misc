import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;

// Important! When adding points to a ThreeDPolygon the points must be in the oriented order
// or else calculation of the normal and such will be a pain...

public final class ThreeDPolygon extends Object
{
	Vector	Object;
	Point	PRP;
	Point	CW;
	double	dPRP;
	double	dCW;
	int	centre_x;
	int	centre_y;
	int	NumberOfPoints = 0;
	int	col_r = 0;
	int	col_g = 0;
	int	col_b = 0;
	double	scale = 1.0;
	int	mincolor = 20;

	// Constructor

	ThreeDPolygon()
	{
		Object = new Vector();
		centre_x = 300;
		centre_y = 200;
	}

	// Insert a Point object at index in the object vector. The point is initialized.

	public void insertPoint(int index, double x, double y, double z)
	{
		Object.insertElementAt(new Point(x,y,z), index);
		NumberOfPoints++;
	}

	// Remove a Point object at index in the object vector.

	public void removePoint(int index)
	{
		Object.removeElementAt(index);
		NumberOfPoints--;
	}

	// Add a Point object at the end of the object vector. The point is initialized.

	public void addPoint(double x, double y, double z)
	{
		Object.addElement(new Point(x,y,z));
		NumberOfPoints++;
	}

	// Set point of perspective.

	public void setPRP(int x, int y, int z)
	{

		PRP = new Point(x, y, z);
		dPRP =  PRP.getZ();
	}


	// Set centre of view window.

	public void setCW(int x, int y, int z)
	{

		CW = new Point(x, y, z);
		dCW = CW.getZ();
	}

	// Set centre of output window.

	public void setCentre(int x, int y)
	{
		centre_x = x;
		centre_y = y;
	}

	// Set minimum color percentage for illumination

	public void setMinColor(int col)
	{

		mincolor = col;

	}

	// Get 2D representation of a point from perspective and stuff. This is x position.

	public int get2DX(Point p)
	{

		return (int)(centre_x + Math.round(scale * (p.getX()*(dPRP+dCW))/(dPRP+p.getZ())));

	}

	// Get 2D y position

	public int get2DY(Point p)
	{

		return (int)(centre_y + Math.round(scale * (p.getY()*(dPRP+dCW))/(dPRP+p.getZ())));

	}

	public double getNormal()
	{
		Point a;
		Point b;
		Point c;

		// Calculate vectors to take normal of (vectors span a plane)
		a = (Point)Object.firstElement();
		b = (Point)Object.elementAt(1);
		c = (Point)Object.lastElement();

		double dab_x = c.getX() - a.getX();  
		double dab_y = c.getY() - a.getY();  
		double dac_x = b.getX() - a.getX();  
		double dac_y = b.getY() - a.getY();  
	
		return  (dac_x*dab_y-dab_x*dac_y);

	}

	public double maxNormal()
	{
		Point a;
		Point b;
		Point c;

		// Calculate vectors to take normal of (vectors span a plane)
		a = (Point)Object.firstElement();
		b = (Point)Object.elementAt(1);
		c = (Point)Object.lastElement();

		double dab_x = Math.abs(c.getX() - a.getX());  
		double dab_y = Math.abs(c.getY() - a.getY());  
		double dab_z = Math.abs(c.getY() - a.getZ());  
		double dac_x = Math.abs(b.getX() - a.getX());  
		double dac_y = Math.abs(b.getY() - a.getY());  
		double dac_z = Math.abs(b.getZ() - a.getZ());  
	
		double dx = dab_x+dac_x;
		dx*=dx;
		double dy = dab_y+dac_y;
		dy*=dy;
		double dz = dab_z+dac_z;
		dz*=dz;

		double norm = Math.sqrt(dx+dy+dz);

		return  (norm);

	}

	// Get the Z-value (average) for the polygon
	public double getZvalue()
	{
		double sum = 0.0;
		Point p; 

		for (Enumeration e = Object.elements(); e.hasMoreElements();)
		{
			// Get next point
			p = (Point)e.nextElement();
			// Add the point's z-value to the sum
			sum += p.getZ();
		}

		// Return average z-value

		return (sum/NumberOfPoints);
	}

	// Get the nearest point to the view plane for the polygon
	public Point getNearestPoint()
	{
		double d = 0.0;
		double dnear = 0.0;
		Point p; 
		Point nearest = new Point(0.0, 0.0, 0.0);

		// This is stuff for calculating the normal's components. getNormal only returns z-value.
		double nx = 0.0;
		double ny = 0.0;
		double nz = getNormal();
		Point a;
		Point b;
		Point c;

		// Calculate vectors to take normal of (vectors span a plane)
		a = (Point)Object.firstElement();
		b = (Point)Object.elementAt(1);
		c = (Point)Object.lastElement();

		double dab_x = Math.abs(c.getX() - a.getX());  
		double dab_y = Math.abs(c.getY() - a.getY());  
		double dab_z = Math.abs(c.getY() - a.getZ());  
		double dac_x = Math.abs(b.getX() - a.getX());  
		double dac_y = Math.abs(b.getY() - a.getY());  
		double dac_z = Math.abs(b.getZ() - a.getZ());  

		nx = dac_y*dab_z-dac_z*dab_y;
		ny = dac_z*dab_x-dac_x*dab_z;

		for (Enumeration e = Object.elements(); e.hasMoreElements();)
		{
			// Get next point
			p = (Point)e.nextElement();

			// Calculate the distance to the view plane
			d = Math.abs(-nx*(p.getX()) - ny*(p.getY()) + nz*(dCW-p.getZ()))/Math.sqrt(nx*nx+ny*ny+nz*nz);

			if (dnear == 0.0)
				dnear = d;

			if (d<=dnear)
			{
				nearest.putX(p.getX());
				nearest.putY(p.getY());
				nearest.putZ(p.getZ());
				dnear = d;
			}

		}

		// Return average z-value

		return (nearest);
	}

	public void Scale(double factor)
	{
		scale *= factor;
	}

	// Translates the polygon by x, y, z.

	public void Translate(double x, double y, double z)
	{
		Point p;

		for (Enumeration e = Object.elements(); e.hasMoreElements();)
		{

			p = (Point)e.nextElement();

			p.putX(p.getX()+x);
			p.putY(p.getY()+y);
			p.putZ(p.getZ()+z);
		}
	}

	// Rotates the polygon by axises xy, xz, yz. Rotation i degrees.

	public void Rotate(int xy, int xz, int yz)
	{
		Point p;
		double deg = Math.PI / 180;

		for (Enumeration e = Object.elements(); e.hasMoreElements();)
		{

			p = (Point)e.nextElement();

			if (xy != 0)
			{
				double tmp1 = 0.0;
				double tmp2 = 0.0;
				double tmp3 = xy * deg;

				tmp1 = p.getX() * Math.cos(tmp3)  - p.getY() * Math.sin(tmp3);
				tmp2 = p.getY() * Math.cos(tmp3)  + p.getX() * Math.sin(tmp3);
				p.putX(tmp1);
				p.putY(tmp2);
			}		

			if (xz != 0)
			{
				double tmp1 = 0.0;
				double tmp2 = 0.0;
				double tmp3 = xz * deg;

				tmp1 = p.getX() * Math.cos(tmp3)  - p.getZ() * Math.sin(tmp3);
				tmp2 = p.getZ() * Math.cos(tmp3)  + p.getX() * Math.sin(tmp3);
				p.putX(tmp1);
				p.putZ(tmp2);
			}		

			if (yz != 0)
			{
				double tmp1 = 0.0;
				double tmp2 = 0.0;
				double tmp3 = yz * deg;

				tmp1 = p.getY() * Math.cos(tmp3)  - p.getZ() * Math.sin(tmp3);
				tmp2 = p.getZ() * Math.cos(tmp3)  + p.getY() * Math.sin(tmp3);
				p.putY(tmp1);
				p.putZ(tmp2);
			}		
		}
	}

	// Paint the polygon, shading true or false and what type of paint.
	// 0 = wireframe, 1 = points, 2 = filled.

	public void paintPolygon(Graphics g, boolean shading, int type)
	{

		Color col;
		Polygon thePoly = new Polygon();
		Point p;
		double normal = getNormal();

		if (shading) {
//			double maxnormal = maxNormal();
//			double perc = normal/maxnormal;
			double perc = normal/400.0;
			double minperc =  (mincolor/255);

			// Check so the surface always has a minimum of illumination.
			if (perc > minperc)
				col = new Color((int)Math.round(col_r*perc), (int)Math.round(col_g*perc), (int)Math.round(col_b*perc));
			else
				col = new Color((int)Math.round(col_r*minperc), (int)Math.round(col_g*minperc), (int)Math.round(col_b*minperc));
		}
		else
		{
			col = new Color(col_r, col_g, col_b);
		}
	
		g.setColor(col);

		switch (type) {

			case 0:

				for (Enumeration e=Object.elements(); e.hasMoreElements();)
				{
					p = (Point)e.nextElement();
					thePoly.addPoint(get2DX(p), get2DY(p));
				}

				p = (Point)Object.firstElement();

				thePoly.addPoint(get2DX(p), get2DY(p));
				g.drawPolygon(thePoly);

			case 1:

			case 2:
				
				for (Enumeration e=Object.elements(); e.hasMoreElements();)
				{
					p = (Point)e.nextElement();
					thePoly.addPoint(get2DX(p), get2DY(p));
				}

				g.fillPolygon(thePoly);
		}			

		g.setColor(Color.black);

	}

	public void setColor(int R, int G, int B)
	{
		// Set max color value for illuminated surface
		col_r = R;
		col_g = G;
		col_b = B;
	}

}
