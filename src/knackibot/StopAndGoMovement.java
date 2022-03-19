/**
 * TODO: This class needs complete rework, as it seems buggy
 * 
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

import java.awt.geom.Point2D;

import robocode.util.Utils;

public class StopAndGoMovement implements MovementStrategy {
  private Point2D.Double movingPoint;

  public StopAndGoMovement() {
    // TODO change initial moving point, because if bearing is small, I am kanonenfutter
    this.movingPoint = new Point2D.Double(200, 200);
  }

  @Override
  public void move(Enemy enemy, KnackOnOne me) {
    boolean move = enemy.getNrBulletsFiredThisRound() % 2 == 0;
    this.movingPoint = calcMovingPoint(me, enemy.getBearingRadians(), this.movingPoint,
        enemy.getPosLogAt(enemy.getPosLogSize() - 1));
    simpleStopAndGo(me, move, this.movingPoint);
  }

  private Point2D.Double calcMovingPoint(KnackOnOne me, double bearing,
      Point2D.Double lastMovingPoint, Point2D.Double enemyPos) {
    // angle to enemy is to direct, so change direction
    /*
     * if(Math.abs(bearing)<Math.PI/6 || Math.abs(bearing)<5*Math.PI/6){ // TODO: this line is
     * wrong. Use correct angle Point2D.Double result = MyUtils.calcPoint(me.ownPos,
     * Math.random()*300, Math.PI+bearing); if(result.x<0){ result.setLocation(100, result.y); }
     * if(result.y<0){ result.setLocation(result.x, 100); } if(result.x>me.getBattleFieldWidth()){
     * result.setLocation(me.getBattleFieldWidth()-100, result.y); }
     * if(result.y>me.getBattleFieldHeight()){ result.setLocation(result.x,
     * me.getBattleFieldHeight() - 100); } return result; } else{ //dont change moving point return
     * lastMovingPoint; }
     */
    return (new Point2D.Double(Math.random() * 700, Math.random() * 500));
  }

  private void simpleStopAndGo(KnackOnOne me, boolean move, Point2D.Double movingPoint) {
    if (move) {
      moveToPoint(me, movingPoint);
    } else {
      me.setStop();
    }
  }


  private void moveToPoint(KnackOnOne me, Point2D.Double p) {
    me.setTurnRightRadians(
        MyUtils.calcAbsBearing(me.ownPos, p) - Utils.normalRelativeAngle(me.getHeadingRadians()));
    me.setAhead(me.ownPos.distance(p));
  }
}
