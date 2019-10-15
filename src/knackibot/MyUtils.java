package knackibot;

import java.awt.geom.Point2D;

public class MyUtils
{
/**
 * calculates the angle between 3 points
 */
public static double angleBetween(Point2D.Double center, Point2D.Double next, Point2D.Double  previous) {
	double angle = (Math.atan2(next.getX() - center.getX(),next.getY() - center.getY())-
                        Math.atan2(previous.getX() - center.getX(),previous.getY() - center.getY()));
	
	System.out.println("Angle calculated: " + angle);
	if(angle<0){
		angle+= 2*Math.PI;
	}
	
	return angle;
}

public static Point2D.Double calcPoint(Point2D.Double p, double dist, double ang) {
		return new Point2D.Double(p.x + dist*Math.sin(ang), p.y + dist*Math.cos(ang)); //TODO use radiant mathmatical formula instead of conversion
}
	
public static double calcAngle(Point2D.Double p2,Point2D.Double p1){
		System.out.println("Angle calculated function2: " +  Math.atan2(p2.x - p1.x, p2.y - p1.y));
		return Math.atan2(p2.x - p1.x, p2.y - p1.y);
}
}