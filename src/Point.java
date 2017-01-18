// A Point class, with getX() and putX(double).

public final class Point extends Object
{

	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	public double getX()
	{
		return x;
	}

	public void putX(double newx)
	{
		x = newx;
	}

	public double getY()
	{
		return y;
	}

	public void putY(double newy)
	{
		y = newy;
	}

	public double getZ()
	{
		return z;
	}

	public void putZ(double newz)
	{
		z = newz;
	}

	Point()
	{
	}

	Point(double newx, double newy, double newz)
	{
		putX(newx);
		putY(newy);
		putZ(newz);
	}

}
