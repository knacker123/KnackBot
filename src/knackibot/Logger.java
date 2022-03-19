/**
 * This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
 * http://robowiki.net/?RWPCL
 */

package knackibot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

enum KindOfLogging {
  TARGETING
}


public class Logger {
  private final boolean pmt_logging = true;
  private final static boolean targetingLogging = false;

  public List<Point2D.Double> pmt_posPrediction = new ArrayList<Point2D.Double>(); // a list, which
                                                                                   // contains the
                                                                                   // predicted
                                                                                   // positions for
                                                                                   // the enemyBot
                                                                                   // until the
                                                                                   // bullet should
                                                                                   // hit the target
  public List<Point2D.Double> pmt_debug_RealPosToPosPrediction = new ArrayList<Point2D.Double>();

  public static void printLogging(KindOfLogging kol, String logString) {
    switch (kol) {
      case TARGETING:
        if (targetingLogging) {
          System.out.println(logString);
        }
        break;
      default:
        break;
    }
  }

  public void setPosPrediction(List<Point2D.Double> posPred) {
    if (pmt_logging) {
      this.pmt_posPrediction = posPred;
    }
  }

  public List<Point2D.Double> getPosPrediction() {
    return this.pmt_posPrediction;
  }

  public void setDebugRealPosToPosPrediction(List<Point2D.Double> d_posPred) {
    if (pmt_logging) {
      this.pmt_debug_RealPosToPosPrediction = d_posPred;
    }
  }

  public List<Point2D.Double> getDebug_PosPrediction() {
    return this.pmt_debug_RealPosToPosPrediction;
  }


}
