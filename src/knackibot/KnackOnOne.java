package knackibot;

import java.awt.Color;

import robocode.*;
import robocode.util.Utils;
import knackibot.Enemy;


public class KnackOnOne extends AdvancedRobot {

	/**
	 * run: Test's default behavior
	 */
	
	Enemy enemy;
	Strategy strategy;
	
	public void run() {
		// Initialization of the robot should be put here test
		setBodyColor(Color.pink);
		setGunColor(Color.black);
		setRadarColor(Color.pink);

		enemy = new Enemy();
		strategy = new NaiveStrategy();
		
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		
	    turnRadarRight(Double.POSITIVE_INFINITY);	
	    
		// Robot main loop
		while(true) {
			if( getRadarTurnRemainingRadians() == 0)
			{
	            setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
			}
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		enemy.processOnScannedRobot(e);

		System.out.println("Event: bearing=" +  e.getBearingRadians());
	    // Absolute angle towards target
	    double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
	 
	    // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
	    double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );	    
	    // Distance we want to scan from middle of enemy to either side
	    // The 36.0 is how many units from the center of the enemy robot it scans.
	    double extraTurn = Math.min( Math.atan( 36.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
	 
	    // Adjust the radar turn so it goes that much further in the direction it is going to turn
	    // Basically if we were going to turn it left, turn it even more left, if right, turn more right.
	    // This allows us to overshoot our enemy so that we get a good sweep that will not slip.
	    if (radarTurn < 0)
	        radarTurn -= extraTurn;
	    else
	        radarTurn += extraTurn;
	 
	    //Turn the radar
	    setTurnRadarRightRadians(radarTurn);
		
	    
	    // Movement
	    strategy.move(enemy, this);
	    
	    // GUN
	    strategy.shoot(enemy, this);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}	
	
}
