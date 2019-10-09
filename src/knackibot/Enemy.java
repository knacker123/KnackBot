package knackibot;

import robocode.*;

public class Enemy{

	private double energy;
	private double velocity;
	private double bearingRadian;
	private double distance;
	
	public Enemy()
	{
		this.energy = 100;
		this.distance = 0;
		this.bearingRadian = 0;
		this.velocity = 0;
	}
	
	public void processOnScannedRobot(ScannedRobotEvent e) {
		this.energy = e.getEnergy();
		this.velocity = e.getVelocity();
		this.bearingRadian = e.getBearingRadians();
		this.distance = e.getDistance();
		System.out.println("Enemy: bearing=" +  bearingRadian);

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
}
