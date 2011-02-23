package VirtualGraffiti;
import processing.core.PApplet;
import processing.core.PVector;

public class MouseTracker implements CanTracker
{
	PApplet parent;
	PVector xy;
	MouseTracker( PApplet parent )
	{
		this.parent = parent;
		xy = new PVector( 0, 0 );
	}
   public boolean hasCan()
   {
	   return true;
   
   }
   public boolean needsTransformation()
   {
	   return false;
   }
   public void update()
  {
	   xy.x = parent.mouseX;
	   xy.y = parent.mouseY;
  }
   public PVector getXY()
  {
    return xy;
    //new PVector( parent.mouseX, parent.mouseY );
  }
  
   public boolean implementsDistance()
  {
    return false;
  }
  
   public int getDistance()
  {
    return 0;
  }
  
}