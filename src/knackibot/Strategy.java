/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

/**
 * @author Robert Guder
 */
public class Strategy {

	TargetStrategy targeting;
	MovementStrategy moving;
	
	public Strategy()
	{
		System.out.println("Generated default strategies");
		this.targeting = new PatternMatchingTargeting();
		this.moving = new StopAndGoMovement();
	}
	
	void setTargetStrategy(TargetStrategy targetStrategy)
	{
		this.targeting = targetStrategy;
	}
	
	TargetStrategy getTargetStrategy()
	{
		return this.targeting;
	}
	
	void setMovementStrategy(MovementStrategy moveStrat)
	{
		this.moving = moveStrat;
	}

	void move(Enemy enemy, KnackOnOne me)
	{
		this.moving.move(enemy, me);
	}
	void shoot(Enemy enemy, KnackOnOne me)
	{
		this.targeting.shoot(enemy, me);
	}
}
