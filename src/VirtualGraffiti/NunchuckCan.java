package VirtualGraffiti;

import processing.core.PApplet;

public class NunchuckCan implements Can {

	
	SerialReader serial;
	PApplet parent;
	int button1 = 0;
	int nozzlePressure = 0;
	int nozzleOnPressure;
	
	NunchuckCan( PApplet parent )
	{
		this.parent = parent;
		System.out.println( "starting nunchuck can" );		
		nozzleOnPressure = VirtualGraffiti.props.getIntProperty( "Nunchuck.nozzleOnPressure", 10 );
		
		
		serial = new SerialReader(this);
	}
	
	@Override
	public void calibrate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getButton() {
		// TODO Auto-generated method stub
		if( button1 == 1 )
			return true;
		return false;
	}

	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNozzlePressure() {
			int nozzleKnee = 150; 
			int opacityKnee = 50; 

			int opacity;
			if( nozzlePressure < nozzleKnee )
			{
				opacity = (int)PApplet.map( nozzlePressure, 0, nozzleKnee, 0, opacityKnee );
			}
			else
			{
				opacity = (int)PApplet.map( nozzlePressure, nozzleKnee, 255, opacityKnee, 255 );
			}
			//System.out.println( opacity );
			return opacity;
	}

	@Override
	public boolean isNozzlePressed() {
		//TODO fix calib
		if( getNozzlePressure() > nozzleOnPressure )
			return true;
		return false;
	}


	@Override
	public boolean implementsButton() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean implementsDistance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean implementsNozzlePressure() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCalibrated() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void update()
	{
		if( serial.newSerial() )
			updateCan( serial.getLatestMessage() );
	}
	
	void updateCan(String msg)
	{
		try
		{
		
			if( msg.startsWith("Got: "))
			{
				
				String []list = PApplet.split(msg, ' ' );

				nozzlePressure = PApplet.unhex( list[1] );
				button1 = PApplet.unhex( list[2] );

//				System.out.println( "nozz: " + nozz + " butt: " + button ) ;
				//println( "minD: " + minD + " maxD: " + maxD + " maxBrushSize: " + maxBrushSize + " minBrushSize: " + minBrushSize + " brushSize: " + brushSize );    
			//	brushSize = PApplet.map( avgDist, minD, maxD, maxBrushSize, minBrushSize);

			}
			else if( msg.startsWith( "0x55 data" ))
			{
			 //do nothing
			}
			else if( msg.startsWith( "Start" ))
			{
				System.out.println( "connected to nunchuck OK" );
			}
			else
			{
				System.out.println( "nunchuck bad msg: " + msg );
			}
			
		}
		catch( RuntimeException e )
		{
			System.out.println( "bad msg: " + e );
		}
	}
	public void stop() {
		// TODO Auto-generated method stub
		serial.stop();
	}

	@Override
	public void wipeCalibration() {
		// TODO Auto-generated method stub

	}
}
