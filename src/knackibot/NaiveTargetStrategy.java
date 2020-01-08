package knackibot;

import robocode.Rules;
import robocode.util.Utils;

public class NaiveTargetStrategy implements TargetStrategy
{

	@Override
	public void shoot(Enemy enemy, KnackOnOne me) 
	{
		// naive strategy: fire with high power if close to robot - targeting on last known position of EnemyBot
			  // Absolute angle towards target
		    double angleToEnemy = me.getHeadingRadians() + enemy.getBearingRadians();
		 
		    // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
		    double gunTurn = Utils.normalRelativeAngle(angleToEnemy - me.getGunHeadingRadians() );
		 
		    me.setTurnGunRightRadians(gunTurn);
		    // near distance much firepower
		    if(enemy.getDistance() < 150)
		    {
		    	me.fire(Rules.MAX_BULLET_POWER);
		    }
		    else if(enemy.getDistance() < 400)
		    {
		    	me.fire(1);
		    }
		
	}
	
}
