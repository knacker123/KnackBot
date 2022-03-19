/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

public interface MovementStrategy {
  void move(Enemy enemy, KnackOnOne me);
}
