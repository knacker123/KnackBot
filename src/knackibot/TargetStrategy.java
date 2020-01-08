package knackibot;

import robocode.Rules;
import robocode.util.Utils;

public interface TargetStrategy {
	void shoot(Enemy enemy, KnackOnOne me);
}