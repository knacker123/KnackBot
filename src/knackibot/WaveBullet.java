/**
 * Base of the Code taken from http://robowiki.net/wiki/GuessFactor_Targeting_Tutorial
 * 
 * Author: Robert Guder
 */

package knackibot;

import java.awt.geom.*;
import robocode.util.Utils;

public class WaveBullet {
  private double startX; // Position of knackibot
  private double startY; // Position of knackibot
  private double startBearing; // enemy abs bearing
  private double power; // power of bullet
  private long fireTime; // time we fired
  private int direction; // clockwise (1) or counterclockwise (-1) // TODO: think about using bool
                         // or enum
  private int[] returnSegment;

  public WaveBullet(double x, double y, double bearing, double power, int direction, long time,
      int[] segment) {
    startX = x;
    startY = y;
    startBearing = bearing;
    this.power = power;
    this.direction = direction;
    fireTime = time;
    returnSegment = segment;
  }


  public boolean checkHit(double enemyX, double enemyY, long currentTime) {
    // if the distance from the wave origin to our enemy has passed
    // the distance the bullet would have traveled...
    if (Point2D.distance(startX, startY, enemyX, enemyY) <= (currentTime - this.fireTime)
        * MyUtils.getBulletVelocity(this.power)) {
      double desiredDirection = Math.atan2(enemyX - this.startX, enemyY - this.startY);
      double angleOffset = Utils.normalRelativeAngle(desiredDirection - this.startBearing);
      double guessFactor =
          Math.max(-1, Math.min(1, angleOffset / MyUtils.maxEscapeAngle(this.power))) * direction;
      int index = (int) Math.round(((double)returnSegment.length - 1) / 2 * (guessFactor + 1));
      returnSegment[index]++;
      return true;
    }
    return false;
  }
} // end WaveBullet class
