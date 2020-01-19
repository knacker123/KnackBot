package knackibot;

import java.util.ArrayList;
import java.util.List;

import robocode.util.Utils;

public class GuessFactorTargeting implements TargetStrategy{

	List<WaveBullet> g_waves = new ArrayList<WaveBullet>();
	static int[] stats = new int[31];	// 31 is the number of unique GuessFactors we're using
	  									// Note: this must be odd number so we can get
	  									// GuessFactor 0 at middle.
	int direction = 1;
	


	
	
	@Override
	public void shoot(Enemy enemy, KnackOnOne me) 
	{
		// Enemy absolute bearing, you can use your one if you already declare it.
		double absBearing = me.getHeadingRadians() + enemy.getBearingRadians();
		
		// Let's process the waves now:
		for (int i=0; i < g_waves.size(); i++)
		{
			WaveBullet currentWave = (WaveBullet)g_waves.get(i);
			if (currentWave.checkHit(enemy.getCurrentPosition().x, enemy.getCurrentPosition().y, me.getTime()))
			{
				g_waves.remove(currentWave);
				i--;
			}
		}
		
		double power = Math.min(3, Math.max(.1, 1.5 /* some function */));
		// don't try to figure out the direction they're moving 
		// they're not moving, just use the direction we had before
		if (me.getVelocity() != 0)
		{
			if (Math.sin(me.getHeadingRadians()-absBearing)*me.getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		int[] currentStats = stats; // This seems silly, but I'm using it to
					    // show something else later
		WaveBullet newWave = new WaveBullet(me.getX(), me.getY(), absBearing, enemy.getEnergy(),
                        direction, me.getTime(), currentStats);
		
		
		// fire implementation
		int bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
		for (int i=0; i<31; i++)
			if (currentStats[bestindex] < currentStats[i])
			{
				bestindex = i;
			}
 
		// this should do the opposite of the math in the WaveBullet:
		double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		double angleOffset = direction * guessfactor * MyUtils.maxEscapeAngle(power);
        double gunAdjust = Utils.normalRelativeAngle(
        		absBearing - me.getGunHeadingRadians() + angleOffset);
        me.setTurnGunRightRadians(gunAdjust);
		

        if (me.getGunHeat() == 0 && gunAdjust < Math.atan2(9, enemy.getDistance()) && me.setFireBullet(power) != null)
        {
        	g_waves.add(newWave);
        }
	}
	
	
}
