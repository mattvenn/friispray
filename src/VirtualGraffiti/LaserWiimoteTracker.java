package VirtualGraffiti;

import lll.Loc.Loc;
import lll.wrj4P5.Wrj4P5;
import processing.core.PApplet;
import processing.core.PVector;

public class LaserWiimoteTracker extends WiimoteTracker {

	//Wrj4P5 wii;
	//PVector xy;
	//PApplet parent;
	int distance = 0;
	//boolean hasCan = false;
	
	int [] pointsX = new int[4];
	int [] pointsY = new int[4];
	int [] canX = new int[2];
	int [] canY = new int[2];

	LaserWiimoteTracker(PApplet parent)
	{
		super(parent);

		System.out.println( "laser wiimote tracker starting");
	//	xy = neddddw PVector( 0, 0 );
	}
	public boolean implementsDistance() {
		// TODO Auto-generated method stub
		return true;
	}	
	public int getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}
	public void update()
	{
		
		int pointsFound = 0;
		try
		{
			for( int i = 0 ; i < 2 ; i ++ )
		    {
				
		    Loc p=wii.rimokon.irLights[i];
		      if (p.x>-1) {
		        pointsFound ++;

		        pointsX[i] = (int)((1-p.x)*parent.width);
		        pointsY[i] = (int)((1-p.y)*parent.height);
		      }
		      
		    }
		    if( pointsFound == 2 )
		    {
		      // order the points by height
		      if( pointsY[0] < pointsY[1] )    
		      {
		        canX[0] = pointsX[0];
		        canY[0] = pointsY[0];
		        canX[1] = pointsX[1];
		        canY[1] = pointsY[1];
		      }
		      else
		      {
		        canX[0] = pointsX[1];
		        canY[0] = pointsY[1];
		        canX[1] = pointsX[0];
		        canY[1] = pointsY[0];
		      } 
		      xy.x = canX[0];
		      xy.y = canY[0];
		      hasCan = true;
		      float spotDistance = PApplet.sqrt(PApplet.sq(canX[0]-canX[1]) + PApplet.sq( canY[0]-canY[1] ));
		      distance = (int)(PApplet.map( spotDistance, 80, 30, Thing.minBrushSize, Thing.maxBrushSize ));
		      if( distance > Thing.maxBrushSize )
		    	  distance = Thing.maxBrushSize;
		      if( distance < Thing.minBrushSize )
		    	  distance = Thing.minBrushSize;
		    }
		    else
		    {
		    	hasCan = false;
		    }
		   
		}
		catch( Exception e )
		{
			System.out.println( "exception in laser can: " + e );
		}
	}
}
