package knackibot;

import java.util.*;
import java.awt.geom.Point2D;

import robocode.*;

public class Enemy{

	private double energy;
	private double velocity;
	private double bearingRadian;
	private double distance;
	private double heading;
	
	private int nrBulletsHitMeThisRound;
	private int nrBulletsFiredThisRound;
	
	private int tick;
	
	private double historyHeadingChange[];
	private double historyVelocity[];
	
	private List<Point2D.Double> posLog;
	
	public Enemy()
	{
		this.energy = 100;
		this.distance = 0;
		this.bearingRadian = 0;
		this.velocity = 0;
		this.heading = 0;
		
		this.nrBulletsFiredThisRound = 0;
		this.nrBulletsHitMeThisRound = 0;
		

		posLog = new ArrayList<Point2D.Double>();
		
	}
	
	public void processOnScannedRobot(ScannedRobotEvent e) {
		//cache information from last turn
		double energyLastTurn = this.energy;
		double headingLastTurn = this.heading;
		
		//update
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
	public void addPosLog(KnackOnOne me)
	{
		this.posLog.add(MyUtils.calcPoint(me.ownPos, this.distance, me.getHeadingRadians() + this.bearingRadian));
	}
	
	public int getPosLogSize()
	{
		return this.posLog.size();
	}
	
	public Point2D.Double getPosLogAt(int i)
	{
		if(this.posLog.size()>i)
		{
			return this.posLog.get(i);
		}
		else {
			throw new IndexOutOfBoundsException("Index " + i + " is out of bounds!");
		}
		
	}
	
}
