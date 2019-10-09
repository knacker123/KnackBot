package knackibot;

import robocode.*;
import robocode.util.Utils;
import knackibot.Enemy;

public class NaiveStrategy implements Strategy{

	public NaiveStrategy() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void move(Enemy enemy, KnackOnOne me) {
		// TODO Auto-generated method stub
		if(enemy.getDistance() > 120)
		{
			//follow enemy
		    me.setTurnRightRadians(Utils.normalRelativeAngle(enemy.getBearingRadians()));
		    me.setAhead(10);
		    System.out.println("follow");
		}
		else {
			//simple random movement
		//	setTurnRightRadians(Utils.getRandom().nextDouble()*30);
		//	setAhead(Utils.getRandom().nextDouble()*20);
		//	System.out.println("random");
		}
	}

	@Override
	public void shoot(Enemy enemy, KnackOnOne me) {
		  // Absolute angle towards target
	    double angleToEnemy = me.getHeadingRadians() + enemy.getBearingRadians();
	 
	    // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
	    double gunTurn = Utils.normalRelativeAngle(angleToEnemy - me.getGunHeadingRadians() );
	 
	    System.out.println("fire executed");
	    me.setTurnGunRightRadians(gunTurn);
	    // near distance much firepower
	    if(enemy.getDistance() < 150)
	    	me.fire(Rules.MAX_BULLET_POWER);
	    else if(enemy.getDistance() < 400)
	    	me.fire(1);	
	}

}
