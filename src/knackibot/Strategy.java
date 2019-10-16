package knackibot;


/**
 * @author Robert Guder
 */
public interface Strategy {

	void move(Enemy enemy, KnackOnOne me);
	void shoot(Enemy enemy, KnackOnOne me);
}
