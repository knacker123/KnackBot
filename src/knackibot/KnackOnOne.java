/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import robocode.*;
import robocode.util.Utils;
import knackibot.Enemy;

/**
 * @author Robert Guder
 */
public class KnackOnOne extends AdvancedRobot {
	
	Enemy enemy;
	static Strategy strategy = new Strategy();
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
		strategy.setMovementStrategy(new RandomMovement(this, enemy));
		strategy.setTargetStrategy(new GuessFactorTargeting());
		
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
		double myAccuracy = (double)this.nrOfBulletsHitEnemy/(double)this.bulletsFired;
		double enemyAccuracy = (double)enemy.getNrBulletsHitKnackiThisRound()/(double)enemy.getNrBulletsFiredThisRound();
				
		// Strategical decicions
		if(myAccuracy < 0.2)
		{
			// TODO change strategies
			//System.out.println("adapted Startegies strategies");
			//strategy.setTargetStrategy(new NaiveTargetStrategy());
			//strategy.setMovementStrategy(new FollowEnemyMovement());
		}
		
		// Logging TODO maybe move to Logger
		System.out.println("#### Statisctics fot this round #####");
		System.out.println("My own Accuracy: " + myAccuracy);
		System.out.println("Number of Bullets fired: " + this.bulletsFired);
		System.out.println("Number of Bullets hit Enemy: " + this.nrOfBulletsHitEnemy);
		System.out.println("######################################");
		System.out.println("Enemy Accuracy: " + enemyAccuracy);
		System.out.println("#Enemy Bullets fired: " + enemy.getNrBulletsFiredThisRound());
		System.out.println("#Number of Bullets hit me: " + enemy.getNrBulletsHitKnackiThisRound());
		System.out.println("######################################");		
		
		
		/*********** File Logging *******************************/

/*		//if(event.getRound() == 10)
		//{
			String writeContent = "";
			try {
				System.out.println("Try reading");
				BufferedReader reader = null;
				try {
					// Read file "count.dat" which contains 2 lines, a round count, and a battle count
					reader = new BufferedReader(new FileReader(getDataFile("statistics.txt")));

					// Try to get the counts
					//roundCount = Integer.parseInt(reader.readLine());
					//battleCount = Integer.parseInt(reader.readLine());
					while(reader.readLine() != null)
					{
						writeContent += reader.readLine();
						writeContent += "\n";

						System.out.println("New iteration");
						System.out.println(writeContent);
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
				}
			} catch (IOException e) {
				// Something went wrong reading the file, reset to 0.
				//roundCount = 0;
				//battleCount = 0;
				System.out.println("IOException reading statistics.txt");
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException reading statistics.txt");
			}


			PrintStream w = null;
			try {
				System.out.println("Trying to write");
				w = new PrintStream(new RobocodeFileOutputStream(getDataFile("statistics.txt")));

				w.print(writeContent);
				w.println();
				w.println("### " + enemy.getName());
				w.println("## " + strategy.getTargetStrategyName());
				w.println("# ->BulShot " + this.bulletsFired);
				w.println("# ->BulHit" + this.nrOfBulletsHitEnemy);
				w.println();

				// PrintStreams don't throw IOExceptions during prints, they simply set a flag.... so check it here.
				if (w.checkError()) {
					out.println("I could not write the count!");
				}
			} catch (IOException e) {
				out.println("IOException trying to write: ");
				e.printStackTrace(out);
			} finally {
				if (w != null) {
					w.close();
				}
			}
//		} */
		
		
		
		
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
		
		//draw circle on which we increase firepower to maximum
		g.setColor(java.awt.Color.BLACK);
		double radius = 120;
		Shape theCircle = new Ellipse2D.Double(this.getX() - radius, this.getY() - radius, 2.0 * radius, 2.0 * radius);
		g.draw(theCircle);
		
		//drawPosition of Enemy
		g.setColor(java.awt.Color.BLUE);
		g.drawRect((int)enemy.getPosLogAt(enemy.getPosLogSize()-1).x, (int)enemy.getPosLogAt(enemy.getPosLogSize()-1).y, 5, 5);  
		
		//draw position to be shot at
		g.setColor(java.awt.Color.CYAN);
		int predLastIt = logger.getPosPrediction().size()-1;
		g.drawRect((int)logger.getPosPrediction().get(predLastIt).getX(), (int)logger.getPosPrediction().get(predLastIt).getY(), 8, 8);
	}
}
