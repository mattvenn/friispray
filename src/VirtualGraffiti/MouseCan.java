package VirtualGraffiti;

import processing.core.PApplet;
import processing.core.PVector;

	 public class MouseCan implements Can
	{
			PApplet parent;
			PVector xy;
			MouseCan( PApplet parent )
			{
				this.parent = parent;
			}
	  public void update(){}
	  public boolean isCalibrated()
	  {
	    return true;
	  }
	  public void wipeCalibration()  {}
	  public void calibrate() {}
	  public boolean implementsDistance()
	  {
	    return false;
	  }
	  public int getDistance()
	  {
	    return 0;
	  }
	  public boolean implementsNozzlePressure()
	  {
	    return true;
	  }
	  public int getNozzlePressure()
	  {
		  if( isNozzlePressed() )
			  return 255;
		  return 0;
	  
	  
	  }
	  public boolean implementsButton()
	  {
	    return false;
	  }
	  public boolean getButton()
	  {
	
		  return false;
	  }
	@Override
	public boolean isNozzlePressed() {
		// TODO Auto-generated method stub
		if( parent.mousePressed && (parent.mouseButton == parent.LEFT)) return true;
		return false;
	}

}
