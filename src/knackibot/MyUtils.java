package knackibot;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Robert Guder
 */
public class MyUtils
{
	/**
	 * calculates the angle between 3 points
	 * Note: In Robocode angles are clockwise!
	 * 
	 * prev   next
	 *  \     /
	 *   \   /
	 *    \ /
	 *     x
	 *   center
	 */
	public static double angleBetween(Point2D.Double center, Point2D.Double next, Point2D.Double  previous) {
		double angle = (Math.atan2(next.getX() - center.getX(), next.getY() - center.getY())-
	                        Math.atan2(previous.getX() - center.getX(), previous.getY() - center.getY()));
		
		
		return angle;
	}

	/*
	 * Calculates a point p2 on the 2D coordinate system based on
	 * - point p1
	 * - distance between p1 and p2
	 * - absolute angle between 12 o'clock and p2
	 */
	public static Point2D.Double calcPoint(Point2D.Double p, double dist, double ang) {
		return new Point2D.Double(p.x + dist*Math.sin(ang), p.y + dist*Math.cos(ang));
	}

	/*
	 * Calculated absolute angle between line of p1 to p2 and 12 o'clock
	 * Note: In Robocode angles are clockwise!
	 * 
	 *     x p2
	 * |  /
	 * | /
	 * |/
	 * x p1
	 */
	public static double calcAngle(Point2D.Double p2,Point2D.Double p1){
		return Math.atan2(p2.x - p1.x, p2.y - p1.y);
	}
	
	/*
	 * This function rounds value to #places after decimal values
	 */
	public static double roundDouble(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static boolean isInsideBattleField(Point2D.Double p)
	{
		if(p.x > 0 && p.x < 800 )
		{
			if(p.y >0 && p.y < 600)
			{
				return true;
			}
		}
		return false;
		
	}
}