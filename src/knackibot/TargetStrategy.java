/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

public interface TargetStrategy {
	void shoot(Enemy enemy, KnackOnOne me);
	String getName();
}