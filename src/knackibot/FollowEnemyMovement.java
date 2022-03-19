/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

import robocode.util.Utils;

public class FollowEnemyMovement implements MovementStrategy {

  @Override
  public void move(Enemy enemy, KnackOnOne me) {
    if (enemy.getDistance() > 120) {
      // follow enemy
      me.setTurnRightRadians(Utils.normalRelativeAngle(enemy.getBearingRadians()));
      me.setAhead(10);
    }
  }
}
