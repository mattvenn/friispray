package VirtualGraffiti;

import processing.core.PApplet;

public class NunchuckCan implements Can {

	
	SerialReader serial;
	PApplet parent;
	int button1 = 0;
	int nozzlePressure = 0;
	
	NunchuckCan( PApplet parent )
	{
		this.parent = parent;
		System.out.println( "starting nunchuck can" );		
		
		serial = new SerialReader();
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
		// TODO Auto-generated method stub
		return nozzlePressure;
	}

	@Override
	public boolean isNozzlePressed() {
		//TODO fix calib
		if( getNozzlePressure() > 10 )
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
			else
			{
				throw new RuntimeException( "didn't understand msg: " + msg );
			}
			
		}
		catch( RuntimeException e )
		{
			System.out.println( "bad msg: " + e );
		}
	}


	@Override
	public void wipeCalibration() {
		// TODO Auto-generated method stub

	}
}
