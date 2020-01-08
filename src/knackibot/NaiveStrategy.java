package knackibot;

import robocode.*;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import knackibot.Enemy;


/**
 * @author Robert Guder
 */
public class NaiveStrategy implements Strategy{

	private Point2D.Double movingPoint;

	public NaiveStrategy() {
		//TODO change initial moving point, because if bearing is small, I am kanonenfutter
		this.movingPoint = new Point2D.Double(200,200);

	}
	
	@Override
	public void move(Enemy enemy, KnackOnOne me) {
		int movingStrategy = 2;
		switch(movingStrategy) {
		case 1:
		{
			if(enemy.getDistance() > 120)
			{
				//follow enemy
			    me.setTurnRightRadians(Utils.normalRelativeAngle(enemy.getBearingRadians()));
			    me.setAhead(10);
			}
		}
		//Stop and Go Strategy
		case 2:
		default:
		{
			boolean move = enemy.getNrBulletsFiredThisRound() % 2 == 0;
			this.movingPoint = calcMovingPoint(me, enemy.getBearingRadians(), this.movingPoint, enemy.getPosLogAt(enemy.getPosLogSize()-1));
			simpleStopAndGo(me, move, this.movingPoint);		
		}
		}
	}

	public List<Point2D.Double> posPrediction = new ArrayList<Point2D.Double>();  //a list, which contains the predicted positions for the enemyBot until the bullet should hit the target
	public List<Point2D.Double> debug_RealPosToPosPrediction = new ArrayList<Point2D.Double>();  
	
	public List<Point2D.Double> getPosPrediction()
	{
		return posPrediction;
	}
	
	public List<Point2D.Double> getDebug_PosPrediction()
	{
		return debug_RealPosToPosPrediction;
	}
	

	
	@Override
	public void shoot(Enemy enemy, KnackOnOne me) {
		int strategy = 2;
		
		switch(strategy)
		{
		// naive strategy: fire with high power if close to robot - targeting on last known position of EnemyBot
		case 1:
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
		case 2:
		default:
			/*****************************************************************
			 * Implementation of Pattern Matching 
			 *****************************************************************/
			final int patternLength = 10;
			posPrediction.clear();
			debug_RealPosToPosPrediction.clear();
			double diffDistance;
			double diffTurnRate;
			double sumHeuristicMin = Double.POSITIVE_INFINITY;

			int ptMinDiffStart = 0; //index of the start of the Pattern which is connected with minDIff
			int ptMinDiffEnd = 7;
			
			//only execute if log > patternLength because else, there would be no data to compare
			if(enemy.getPosLogSize() > patternLength)
			{
				for(int i=1; i<enemy.getPosLogSize()-patternLength; i++)
				{
					diffDistance = 0;
					diffTurnRate = 0;
					for(int j=0; j<(patternLength-2); j++)
					{
						diffDistance += Math.abs( 
										( enemy.getPosLogAt(i+j).distance(enemy.getPosLogAt(i+j+1))) - 
										( enemy.getPosLogAt(enemy.getPosLogSize()-(patternLength)+j).distance(enemy.getPosLogAt(enemy.getPosLogSize()-(patternLength-1)+j)) )
										);
						
						diffTurnRate += Math.abs( 
										MyUtils.angleBetween(enemy.getPosLogAt(i+j), 
												enemy.getPosLogAt(i+j+1), 
												enemy.getPosLogAt(i+j-1)) - 
										( MyUtils.angleBetween(enemy.getPosLogAt(enemy.getPosLogSize()-patternLength+j), 
												enemy.getPosLogAt(enemy.getPosLogSize()-(patternLength-1)+j), 
												enemy.getPosLogAt(enemy.getPosLogSize()-(patternLength+1)+j)))
										);
					}
					if(diffDistance+diffTurnRate < sumHeuristicMin)
					{
						sumHeuristicMin = diffDistance+diffTurnRate;
						ptMinDiffStart = i;
						ptMinDiffEnd = i+(patternLength-2);
					}
				}
				System.out.println("Heuristik: " + sumHeuristicMin);
				double firepower = calcFirepower(enemy, sumHeuristicMin);
				int i=1;
				boolean fire = true;
				Point2D.Double targetPos = enemy.getPosLogAt(enemy.getPosLogSize()-1); 
				Point2D.Double targetPosPrev = enemy.getPosLogAt(enemy.getPosLogSize()-2);

				posPrediction.add(targetPos);
				Point2D.Double targetPosHelp = null;
				if(enemy.getDistance() < 80)
				{
					fireAt(me, firepower, targetPos);
				}
				else {
					try{
						while((me.ownPos.distance(targetPos) >(20-3*firepower)*i) && ((ptMinDiffEnd+1+i) < enemy.getPosLogSize()))
						{
							targetPosHelp = targetPos;
							targetPos = calcTargetPositionWithPatternMatching(me, targetPosHelp,	targetPosPrev, 	
													enemy.getPosLogAt(ptMinDiffEnd-1+i),enemy.getPosLogAt(ptMinDiffEnd+i), enemy.getPosLogAt(ptMinDiffEnd+1+i));
						
							
							targetPosPrev = targetPosHelp;
							
							posPrediction.add(targetPos);
							debug_RealPosToPosPrediction.add(enemy.getPosLogAt(ptMinDiffEnd+i));
							
							i++;
							if(!(me.ownPos.distance(targetPos) >(20-3*firepower)*i))
							{
								System.out.println("Bullet target reached");
								fire = true;
							}
							else if(!((ptMinDiffEnd+1+i) < enemy.getPosLogSize()))
							{
								fire = false; //don't shoot, because calculation is crap
								System.out.println("Targetprediction cancelled because of stackoverflow");
							}
						}
						}catch(Exception e){
								System.out.println("Index out of bound during patternMatching, targetPosition prediction");
								//fire = false;
						}
						
						/*** Firing ***/
						//only fire if predicted location is inside battlefield or enemy is close to me
						if(MyUtils.isInsideBattleField(targetPos) && fire)
						{
							fireAt(me, firepower, targetPos);
						}
				}
			}
		}
	}
	
	private double calcFirepower(Enemy enemy, double heuristic){ 
		double firepower;
		if(enemy.getEnergy() < 16.0){
				if(enemy.getEnergy()<4.0){
					firepower = enemy.getEnergy() / 4;
				}
				else{
					firepower = enemy.getEnergy() / 6;
				}
			}
			else{
				// TODO: improve algorithm to adapt firepower
				if(heuristic < 0.01) {
					firepower = 3;
				}
				else if(heuristic < 0.04)
				{
					firepower = 1.7;
				}
				else if(heuristic < 0.1)
				{
					firepower = 0.8;
				}
				else if(heuristic < 1)
				{
					firepower = 0.2;
				}
				else {
					if(enemy.getDistance() < 300)
					{
						firepower = 0.1;
					}
					else {
						firepower = 0.0;
					}
				}
			}
		return firepower;
	}
	
	private Point2D.Double calcTargetPositionWithPatternMatching(KnackOnOne me, Point2D.Double currentEnemyPos, Point2D.Double prevEnemyPos, Point2D.Double logPosPrev, Point2D.Double logPosCenter, Point2D.Double logPosNext){
		Point2D.Double resultPos;
		double distance = logPosCenter.distance(logPosNext); //distance to next Positon
		double relativeAngle; //relative angle, the bot moved right, from the prev position
		double absoluteAngle; //absolute angle, measured in relation to the coordinate system
		
		relativeAngle = MyUtils.angleBetween(logPosCenter, logPosNext, logPosPrev);
		Point2D.Double x0 = new Point2D.Double(currentEnemyPos.getX(), me.getBattleFieldHeight());  //angle between last position and the point on top of the battlefield (0 degree line)

		absoluteAngle = relativeAngle - MyUtils.angleBetween(currentEnemyPos, x0, prevEnemyPos);
		if(absoluteAngle>=2*Math.PI){
			absoluteAngle-= 2*Math.PI;
		}
		
		resultPos = MyUtils.calcPoint(currentEnemyPos, distance, absoluteAngle);

		return resultPos;
	}
	
	/**
	 * Fires on a specific 2D-Point on the map
	 */
	private void fireAt(KnackOnOne me, double firepower, Point2D.Double p){
		me.setTurnGunRightRadians(Utils.normalRelativeAngle(MyUtils.calcAngle(p, me.ownPos) - me.getGunHeadingRadians()));
		me.setFire(firepower);
	}
	
	private Point2D.Double calcMovingPoint(KnackOnOne me, double bearing, Point2D.Double lastMovingPoint, Point2D.Double enemyPos){
		//angle to enemy is to direct, so change direction
		if(Math.abs(bearing)<Math.PI/6 || Math.abs(bearing)<5*Math.PI/6){
			Point2D.Double result = MyUtils.calcPoint(me.ownPos, Math.random()*300, Math.PI+bearing);
			if(result.x<0){
				result.setLocation(100, result.y);
			}
			if(result.y<0){
				result.setLocation(result.x, 100);
			}
			if(result.x>me.getBattleFieldWidth()){
				result.setLocation(me.getBattleFieldWidth()-100, result.y);
			}
			if(result.y>me.getBattleFieldHeight()){
				result.setLocation(result.x, me.getBattleFieldHeight() - 100);
			}
			return result;
		}
		else{
			//dont change moving point
			return lastMovingPoint;
		}
	}
	
	private void simpleStopAndGo(KnackOnOne me, boolean move, Point2D.Double movingPoint){
		if(move){
			moveToPoint(me, movingPoint);
		}
		else{
			me.setStop();
		}
	}
		
	private void moveToPoint(KnackOnOne me, Point2D.Double p){
		me.setTurnRightRadians(Utils.normalRelativeAngle(me.getHeadingRadians() -  MyUtils.calcAngle(me.ownPos, p)));
		me.setAhead(me.ownPos.distance(p));
	}
}
