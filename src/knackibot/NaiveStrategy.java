package knackibot;

import robocode.*;
import robocode.util.Utils;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
		Point2D.Double movingPoint = new Point2D.Double(200,200);

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
	
	public List<Point2D.Double> getPosPrediction()
	{
		return posPrediction;
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
		    	me.fire(Rules.MAX_BULLET_POWER);
		    else if(enemy.getDistance() < 400)
		    	me.fire(1);
		case 2:
		default:
			/*****************************************************************
			 * Implementation of Pattern Matching 
			 *****************************************************************/
			posPrediction.clear();
			double diffDistance;
			double diffTurnRate;
			double sumHeuristicMin = Double.POSITIVE_INFINITY;

			int ptMinDiffStart = 0; //index of the start of the Pattern which is connected with minDIff
			int ptMinDiffEnd = 7;
			
			//only execute if log > 8 because else, there would be no data to compare
			if(enemy.getPosLogSize() > 8){
				for(int i=1; i<enemy.getPosLogSize()-8; i++){
					diffDistance = 0;
					diffTurnRate = 0;
					for(int j=0; j<6; j++){
						diffDistance += Math.abs( 
										( enemy.getPosLogAt(i+j+1).distance(enemy.getPosLogAt(i+j))) - 
										( enemy.getPosLogAt(enemy.getPosLogSize()-7+j).distance(enemy.getPosLogAt(enemy.getPosLogSize()-8+j)) )
										) ;
						
						diffTurnRate += Math.abs( 
										/*Math.PI -*/ MyUtils.angleBetween(enemy.getPosLogAt(i+j), enemy.getPosLogAt(i+j+1), enemy.getPosLogAt(i+j-1)) - 
										( /*Math.PI -*/ MyUtils.angleBetween(enemy.getPosLogAt(enemy.getPosLogSize()-8+j), enemy.getPosLogAt(enemy.getPosLogSize()-7+j), enemy.getPosLogAt(enemy.getPosLogSize()-9+j)))
										) ;
					
				//		System.out.println("x-1: " + enemy.getPosLogAt(i+j-1).getX() + "x: " + enemy.getPosLogAt(i+j).getX() + "x+1: " + enemy.getPosLogAt(i+j-1).getX() + "y-1: " + enemy.getPosLogAt(i+j-1).getY() + "y: " + enemy.getPosLogAt(i+j).getY() + "y+1: " + enemy.getPosLogAt(i+j-1).getY());
				//		System.out.println("x-1: " + enemy.getPosLogAt(enemy.getPosLogSize()-9+j).getX() + "x: " + enemy.getPosLogAt(enemy.getPosLogSize()-8+j).getX() + "x+1: " + enemy.getPosLogAt(enemy.getPosLogSize()-7+j).getX() + "y-1: " + enemy.getPosLogAt(enemy.getPosLogSize()-9+j).getY() + "y: " + enemy.getPosLogAt(enemy.getPosLogSize()-8+j).getY() + "y+1: " + enemy.getPosLogAt(enemy.getPosLogSize()-7+j).getY());
				//		System.out.println("diffDis: " + diffDistance + " diffTurnRate: " + diffTurnRate);
				//		System.out.println("TurnRate a: " + MyUtils.angleBetween(enemy.getPosLogAt(i+j), enemy.getPosLogAt(i+j+1), enemy.getPosLogAt(i+j-1)) );
				//		System.out.println("TurnRate b: " + MyUtils.angleBetween(enemy.getPosLogAt(enemy.getPosLogSize()-8+j), enemy.getPosLogAt(enemy.getPosLogSize()-7+j), enemy.getPosLogAt(enemy.getPosLogSize()-9+j)));
					}
					if(diffDistance+diffTurnRate < sumHeuristicMin){
						sumHeuristicMin = diffDistance+diffTurnRate;
						ptMinDiffStart = i;
						ptMinDiffEnd = i+6;
					}
				}
				System.out.println("Heuristik: " + sumHeuristicMin);
				double firepower = calcFirepower(enemy, sumHeuristicMin);
				int i=1;
				//boolean fire = true;
				Point2D.Double targetPos = enemy.getPosLogAt(enemy.getPosLogSize()-1); 
				Point2D.Double targetPosPrev = enemy.getPosLogAt(enemy.getPosLogSize()-2);

				posPrediction.add(targetPos);
				Point2D.Double targetPosHelp = null;
				//enemyBot.posLog.get(ptMinDiffStart+6+i)
				try{
				while(me.ownPos.distance(targetPos) >(20-3*firepower)*i){
//					System.out.println("own: " + me.toString() + "   enemy: " + targetPos.toString() + "    distance: " + me.ownPos.distance(targetPos));
//					System.out.println(enemy.getPosLogAt(enemy.getPosLogSize()-1));
//					System.out.println(i);
					targetPosHelp = targetPos;
					targetPos = calcTargetPositionWithPatternMatching(me, targetPosHelp,	targetPosPrev, 	
											enemy.getPosLogAt(ptMinDiffEnd-2+i),enemy.getPosLogAt(ptMinDiffEnd-1+i), enemy.getPosLogAt(ptMinDiffEnd+i));
				
					targetPosPrev = targetPosHelp;
					
					posPrediction.add(targetPos);
					
					i++;
				}
				}catch(Exception e){
					// TODO: this out of bound sometimes occurs --> fix
						System.out.println("Index out of bound during patternMatching, targetPosition prediction");
						//fire = false;
				}
				
				//Firing
				fireAt(me, firepower, targetPos);
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
				else if(heuristic < 0.1)
				{
					firepower = 1.5;
				}
				else {
					if(enemy.getDistance() < 100)
					{
						firepower = 3;
					}
					else if(enemy.getDistance() < 400)
					{
						firepower = 0.2;
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
		double relativeAngle; //relative angle, the bot move left, from the prev position
		double absoluteAngle; //absolute angle, measured in relation to the coordinate system
		
		relativeAngle = MyUtils.angleBetween(logPosCenter, logPosNext, logPosPrev);
		Point2D.Double x0 = new Point2D.Double(currentEnemyPos.getX(), me.getBattleFieldHeight());  //angle between last position and the poin on to of the battlefield (0 degree line)pt

		absoluteAngle = relativeAngle - MyUtils.angleBetween(currentEnemyPos, x0, prevEnemyPos);
		if(absoluteAngle>=2*Math.PI){
			absoluteAngle-= 2*Math.PI;
		}
		
		resultPos = MyUtils.calcPoint(currentEnemyPos, distance, absoluteAngle);

		return resultPos;
	}
	
	/**
	 * Fires on a specific 2D-Point on the mapS
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
