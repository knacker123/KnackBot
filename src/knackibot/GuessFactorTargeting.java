package knackibot;

import java.util.ArrayList;
import java.util.List;

import robocode.util.Utils;

public class GuessFactorTargeting implements TargetStrategy {

  List<WaveBullet> g_waves = new ArrayList<>();
  static int[][] stats = new int[3][31]; // 31 is the number of unique GuessFactors we're using
  // Note: this must be odd number so we can get
  // GuessFactor 0 at middle.
  int direction = 1;
  private String name = "GF-Targeting";

  private double calcFirepower(Enemy enemy) {
    double firepower = 0;
    if (enemy.getEnergy() < 16.0) {
      if (enemy.getEnergy() < 4.0) {
        firepower = enemy.getEnergy() / 4;
      } else {
        firepower = enemy.getEnergy() / 6;
      }
    } else {
      firepower = 1.9;
    }

    return firepower;
  }

  // see http://robowiki.net/wiki/SegmentedData/Segments
  private int accelSegment(double deltaBearing, double oldDeltaBearing, double enemyDistance) {
    int delta = (int) (Math
        .round(5 * enemyDistance * (Math.abs(deltaBearing) - Math.abs(oldDeltaBearing))));
    if (delta < 0) {
      return 0;
    } else if (delta > 0) {
      return 2;
    }
    return 1;
  }

  @Override
  public void shoot(Enemy enemy, KnackOnOne me) {
    // Enemy absolute bearing, you can use your one if you already declare it.
    double absBearing = me.getHeadingRadians() + enemy.getBearingRadians();

    // Let's process the waves now:
    for (int i = 0; i < g_waves.size(); i++) {
      WaveBullet currentWave = g_waves.get(i);
      if (currentWave.checkHit(enemy.getCurrentPosition().x, enemy.getCurrentPosition().y,
          me.getTime())) {
        g_waves.remove(currentWave);
        i--;
      }
    }

    double power = Math.min(3, Math.max(.1, calcFirepower(enemy)));
    // don't try to figure out the direction they're moving
    // they're not moving, just use the direction we had before
    if (enemy.getVelocity() != 0) {
      if (Math.sin(me.getHeadingRadians() - absBearing) * enemy.getVelocity() < 0)
        direction = -1;
      else
        direction = 1;
    }

    // use the lateral accelaration of the enemy as three dimensional stats. Alternativ is e.g.
    // distance
    int[] currentStats = stats[accelSegment(enemy.getBearingRadians(),
        enemy.getLastBearingRadians(), enemy.getDistance())];
    WaveBullet newWave = new WaveBullet(me.getX(), me.getY(), absBearing, enemy.getEnergy(),
        direction, me.getTime(), currentStats);


    // fire implementation
    int bestindex = 15; // initialize it to be in the middle, guessfactor 0.
    for (int i = 0; i < 31; i++)
      if (currentStats[bestindex] < currentStats[i]) {
        bestindex = i;
      }

    // this should do the opposite of the math in the WaveBullet:
    double guessfactor =
        (bestindex - ((double)currentStats.length - 1) / 2) / (((double)currentStats.length - 1) / 2);
    double angleOffset = direction * guessfactor * MyUtils.maxEscapeAngle(power);
    double gunAdjust =
        Utils.normalRelativeAngle(absBearing - me.getGunHeadingRadians() + angleOffset);
    me.setTurnGunRightRadians(gunAdjust);


    if (me.getGunHeat() == 0 && gunAdjust < Math.atan2(9, enemy.getDistance())
        && me.setFireBullet(power) != null) {
      g_waves.add(newWave);
    }
  }


  @Override
  public String getName() {
    return this.name;
  }


}
