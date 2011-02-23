package VirtualGraffiti;

import lll.Loc.Loc;
import lll.wrj4P5.Wrj4P5;
import processing.core.PApplet;
import processing.core.PVector;



public class WiimoteTracker implements CanTracker {

	Wrj4P5 wii;
	PVector xy;
	PApplet parent;
	boolean hasCan = false;

	WiimoteTracker(PApplet parent)
	{
		this.parent = parent;
		System.out.println( "wiimote tracker starting");
		xy = new PVector( 0, 0 );
		try
		{
			wii=new Wrj4P5(parent);
			wii.connect(Wrj4P5.IR);
			System.out.println("press 1&2 on wiimote..." );  
			int pause = 0;
			while( wii.isConnecting() )
			{
				//background( 0 );
				///fill( 255 );
				if( pause ++ % 500 == 0 )
					System.out.print( "." );
			}
		}
		catch( Exception e )
		{
			System.out.println( "error: " + e );
			System.exit( 1 );
		}
		System.out.println( "connected OK!" );




	}
	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PVector getXY() {
		// TODO Auto-generated method stub
		return xy;
	}

	@Override
	public boolean hasCan() {
		// TODO Auto-generated method stub
		return hasCan;
	}

	@Override
	public boolean implementsDistance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsTransformation() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void update()
	{
		try
		{
			Loc p=wii.rimokon.irLights[0];
			//spot found
			if (p.x>-1) 
			{
				hasCan = true;
				xy.x = (int)( ( 1 - p.x) * parent.width );
				xy.y = (int)( ( 1 - p.y) * parent.height );
			}
			else
			{
				hasCan = false;
			}
		}
		catch( Exception e )
		{
			System.out.println( "exception: " + e );
		}

	}

}
