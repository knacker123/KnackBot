package knackibot;

import java.util.*;
import java.awt.geom.Point2D;

import robocode.*;

/**
 * @author Robert Guder
 */
public class Enemy{

	private double energy;
	private double velocity;
	private double bearingRadian;
	private double distance;
	private double heading;
	
	private int nrBulletsHitMeThisRound;
	private int nrBulletsFiredThisRound;
	
	static private List<Point2D.Double> posLog = new ArrayList<Point2D.Double>();
	
	public Enemy()
	{
		this.energy = 100;
		this.distance = 0;
		this.bearingRadian = 0;
		this.velocity = 0;
		this.heading = 0;
		
		this.nrBulletsFiredThisRound = 0;
		this.nrBulletsHitMeThisRound = 0;
		

		//posLog = new ArrayList<Point2D.Double>();
		
	}
	
	public void processOnScannedRobot(ScannedRobotEvent e) {
		//cache information from last turn
		double energyLastTurn = this.energy;
		
		//update EnemyBot
		this.energy = e.getEnergy();
		this.velocity = e.getVelocity();
		this.bearingRadian = e.getBearingRadians();
		this.distance = e.getDistance();
		this.heading = e.getHeadingRadians();

		//detect if Bullet was fired
		double energyDifference = energyLastTurn - this.energy;
		if(energyDifference > 0.1 && energyDifference <3.0)
		{
			nrBulletsFiredThisRound++;
		}
		
		
	}
	
	public void addShotHitMe()
	{
		this.nrBulletsHitMeThisRound++;
	}
	
	public double getEnergy()
	{
		return this.energy;
	}
	
	public double getVelocity()
	{
		return this.velocity;
	}
	
	public double getBearingRadians()
	{
		return this.bearingRadian;
	}
	
	public double getDistance()
	{
		return this.distance;
	}
	
	
	/*************************************************
	 * Position Logging
	 *************************************************/
	
	/*
	 * PosLog is the logging all of positions the EnemyBot.
	 * This function adds the current enemys' position to Enemy.posLog. 
	 */
	public void addPosLog(KnackOnOne me)
	{
		Enemy.posLog.add(MyUtils.calcPoint(me.ownPos, this.distance, me.getHeadingRadians() + this.bearingRadian));
	}
	
	/*
	 * PosLog is the logging all of positions the EnemyBot.
	 * This function returns the size of Enemy.posLog. 
	 */
	public int getPosLogSize()
	{
		return Enemy.posLog.size();
	}
	
	/*
	 * PosLog is the logging all of positions the EnemyBot.
	 * This function returns position in Enemy.posLog on given index
	 */
	public Point2D.Double getPosLogAt(int i)
	{
		if(Enemy.posLog.size()>i)
		{
			return Enemy.posLog.get(i);
		}
		else {
			throw new IndexOutOfBoundsException("Index " + i + " is out of bounds!");
		}
		
	}
	
	/*
	 * PosLog is the logging all of positions the EnemyBot.
	 * In case posLog exceeds maxSize, the first n elements are removed.
	 */
	public void cleanupMemorySizePosLog(int maxSize)
	{
		int curPosLogSize = Enemy.posLog.size();
		if(curPosLogSize > maxSize)
		{
			for(int i = 0; i < (curPosLogSize - maxSize); i++)
			{
				Enemy.posLog.remove(0);
			}
		}
	}
	
}
