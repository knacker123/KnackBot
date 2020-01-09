package knackibot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.*;
import robocode.util.Utils;
import knackibot.Enemy;

/**
 * @author Robert Guder
 */
public class KnackOnOne extends AdvancedRobot {
	
	Enemy enemy;
	Strategy strategy;
	Point2D.Double ownPos;
	Logger logger = new Logger();
	int bulletsFired = 0;
	int nrOfBulletsHitEnemy = 0;
	
	public void run() {
		// Set design of the Robot
		setBodyColor(Color.pink);
		setGunColor(Color.black);
		setRadarColor(Color.pink);

		// Initialize
		enemy = new Enemy();
		strategy = new Strategy();
		
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		
	    turnRadarRight(Double.POSITIVE_INFINITY);	
	    
		while(true) {
			// Width Lock part I - acc. to http://robowiki.net/wiki/One_on_One_Radar
			// Turn the radar if we have no more turn, starts it if it stops and at the start of round
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
		this.ownPos = new Point2D.Double(getX(), getY());
		
		enemy.processOnScannedRobot(e);
		enemy.addPosLog(this);
		
	    //Turn the radar
	    setTurnRadarRightRadians(applyWidthLock(e));
	    
	    // Movement
	    strategy.move(enemy, this);
	    
	    // GUN
	    strategy.shoot(enemy, this);    
	}

	public void onBulletMissed(BulletMissedEvent e)
	{
		this.bulletsFired++;
	}
	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		enemy.addShotHitMe();
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}	
	
	// One of my bullets has hit an enemy
	public void onBulletHit(BulletHitEvent event)
	{
		this.nrOfBulletsHitEnemy++;
		this.bulletsFired++;
	}

	// One of my bullets hit another bullet
	public void onBulletHitBullet(BulletHitBulletEvent event)
	{
		this.bulletsFired++;
	}
	
	//possibility to safe statistics at the end of a round
	public void onRoundEnded(RoundEndedEvent event)
	{
		System.out.println("#### Statisctics fot this round #####");
		System.out.println("Accuracy: " + (double)this.nrOfBulletsHitEnemy/(double)this.bulletsFired);
		System.out.println("Number of Bullets fired: " + this.bulletsFired);
		System.out.println("Number of Bullets hit Enemy: " + this.nrOfBulletsHitEnemy);
		System.out.println("######################################");
		
		this.nrOfBulletsHitEnemy = 0;
		this.bulletsFired = 0;
		// don't let Enemy.posLog exceed 3000 logs
		enemy.cleanupMemorySizePosLog(3000);
	}

	/*
	 * Width lock implementation acc. to http://robowiki.net/wiki/One_on_One_Radar
	 * 
	 * @return: angle -> how far should the radar be turned in order to achieve optimal lock
	 */
	private double applyWidthLock(ScannedRobotEvent e)
	{
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
	    
	    return radarTurn;
	}
	
	//Debugging -----------------------------------
	public void onPaint(Graphics2D g){	
		g.setColor(java.awt.Color.GREEN);
		//drawing the predicted way for the enemyBot
		try{
			for(int i=0; i<logger.getPosPrediction().size()-1; i++){
			g.drawLine((int)logger.getPosPrediction().get(i).getX(),
					(int)logger.getPosPrediction().get(i).getY(),
					(int)logger.getPosPrediction().get(i+1).getX(),
					(int)logger.getPosPrediction().get(i+1).getY());
			}
		}	
		catch(Exception e){
			System.out.println("Exception in printing PosPrediction");
		}
		System.out.println("Size PosPrediction: " + logger.getPosPrediction().size());
		System.out.println("Size debug_PosPrediction: " + logger.getPosPrediction().size());
		
		g.setColor(java.awt.Color.RED);
		//drawing the predicted way for the enemyBot
		try{
			for(int i=0; i<logger.getDebug_PosPrediction().size()-1; i++){
			g.drawLine((int)logger.getDebug_PosPrediction().get(i).getX(),
					(int)logger.getDebug_PosPrediction().get(i).getY(),
					(int)logger.getDebug_PosPrediction().get(i+1).getX(),
					(int)logger.getDebug_PosPrediction().get(i+1).getY());
			}
		}	
		catch(Exception e){
			System.out.println("Exception in printin debug_PosPrediction");
		}
		
		//drawPosition of Enemy
		g.setColor(java.awt.Color.BLUE);
		g.drawRect((int)enemy.getPosLogAt(enemy.getPosLogSize()-1).x, (int)enemy.getPosLogAt(enemy.getPosLogSize()-1).y, 5, 5);  
		
		//draw position to be shot at
		g.setColor(java.awt.Color.CYAN);
		int predLastIt = logger.getPosPrediction().size()-1;
		g.drawRect((int)logger.getPosPrediction().get(predLastIt).getX(), (int)logger.getPosPrediction().get(predLastIt).getY(), 8, 8);
	}
}
