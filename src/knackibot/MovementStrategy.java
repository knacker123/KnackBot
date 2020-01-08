package knackibot;

import java.awt.geom.Point2D;

public interface MovementStrategy {
	void move(Enemy enemy, KnackOnOne me);
}
