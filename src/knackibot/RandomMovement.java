/**
 * This class implements random movement. Basis of the code and basic ideas
 * were taken from http://robowiki.net/wiki/RandomMovementBot/Code. Thanks to bot author PEZ.
 * 
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import robocode.util.Utils;

public class RandomMovement implements MovementStrategy{
	static final double MAX_VELOCITY = 8;
    static final double WALL_MARGIN = 25;
    KnackOnOne me;
    Enemy enemy;
    double movementLateralAngle = 0.2;
 
    public RandomMovement(KnackOnOne bot, Enemy enemy) {
		this.me = bot;
		this.enemy = enemy;
	}
 
    void considerChangingDirection() {
		// Change lateral direction at random
		// Tweak this to go for flat movement
		double flattenerFactor = 0.05;
		if (Math.random() < flattenerFactor)
		{
		    movementLateralAngle *= -1;
		}
    }
 
    RoundRectangle2D fieldRectangle(double margin) {
        return new RoundRectangle2D.Double(margin, margin,
	    me.getBattleFieldWidth() - margin * 2, me.getBattleFieldHeight() - margin * 2, 75, 75);
    }
 
    //TODO: same implementation as "StopAndGo.moveToPoint". Think about refactoring
    void goTo(Point2D.Double destination) {
        double angle = Utils.normalRelativeAngle(MyUtils.calcAbsBearing(me.ownPos, destination) - me.getHeadingRadians());
        double turnAngle = Math.atan(Math.tan(angle));
        me.setTurnRightRadians(turnAngle);
        me.setAhead(me.ownPos.distance(destination) * (angle == turnAngle ? 1 : -1));
        // Hit the brake pedal hard if we need to turn sharply
        me.setMaxVelocity(Math.abs(me.getTurnRemaining()) > 33 ? 0 : MAX_VELOCITY);
    }
    
	@Override
    // Always try to move a bit further away from the enemy.
    // Only when a wall forces us we will close in on the enemy. We never bounce off walls.
	public void move(Enemy enemy, KnackOnOne me) 
	{
		considerChangingDirection();
		double localMovementLateralAngle = this.movementLateralAngle;
		Point2D.Double robotDestination = null;
		double tries = 0;
		
		//try until destination is inside of battlefield or 100 tries are exceeded
		do {
		    robotDestination = MyUtils.calcPoint(enemy.getCurrentPosition(), 
		    		enemy.getDistance() * (1.1 - (tries % 100) / 100.0), 
		    		MyUtils.calcAbsBearing(enemy.getCurrentPosition(), me.ownPos) + localMovementLateralAngle);	    
		    tries++;
		} while (tries < 100 && !fieldRectangle(WALL_MARGIN).contains(robotDestination));
		goTo(robotDestination);		
	}

}
