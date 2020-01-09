package knackibot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Logger {
	private final boolean pmt_logging = true;
	
	public List<Point2D.Double> pmt_posPrediction = new ArrayList<Point2D.Double>();  //a list, which contains the predicted positions for the enemyBot until the bullet should hit the target
	public List<Point2D.Double> pmt_debug_RealPosToPosPrediction = new ArrayList<Point2D.Double>();  
	
	public void setPosPrediction(List<Point2D.Double> posPred)
	{
		if(pmt_logging)
		{
			this.pmt_posPrediction = posPred;
		}
	}
	
	public List<Point2D.Double> getPosPrediction()
	{
		return this.pmt_posPrediction;
	}
	
	public void setDebugRealPosToPosPrediction(List<Point2D.Double> d_posPred)
	{
		if(pmt_logging)
		{
			this.pmt_debug_RealPosToPosPrediction = d_posPred;
		}
	}
	
	public List<Point2D.Double> getDebug_PosPrediction()
	{
		return this.pmt_debug_RealPosToPosPrediction;
	}
	
	
}
