package VirtualGraffiti;

import processing.core.PApplet;


public class MattCan implements Can{

	PApplet parent;

	
	int val;      // Data received from the serial port
	int i = 0;
	int N = 3; //mv avg ammount
	float brushSize;
	float nozzle;
	//calibration stuff
	float calibrationDistance;
	int calibrationMode = 0;
	boolean calibrated = false;
	boolean readyToCalibrate = true;
	int minD = 0;  //min val back from can's distance sensor 
	int maxD = 255; //max value back...
	int maxNoz = 255;
	int minNoz = 115; //min val back from the can



	String inBuffer;
	float freq; //how often we are receiving an update from the can

	//	  AudioPlayer spray;

	//moving average arrays  
	float[] nozzHist; 
	float[] distHist; 
	float[] freqHist;

	float avgNoz = 0;
	float avgDist = 0;

	int lf = 13;      // ASCII linefeed

	
	int time;
	int oldtime = 0;
	int diff;

	//TODO
    int minBrushSize = 10;
	int maxBrushSize = 200;
	SerialReader serial;
	
	MattCan( PApplet parent )
	{
		this.parent = parent;
		System.out.println( "starting matt can" );		
		
		serial = new SerialReader();

		nozzHist = new float[N];
		distHist = new float[N];
		freqHist = new float[N];

		//    minBrushSize = min;
		//   maxBrushSize = max;  
		//TODO calibrate etc.
		brushSize = 10;


	}
	
	public void update()
	{
		updateCan( serial.getLatestMessage() );
	}
	
	void updateCan(String msg)
	{
		try
		{
			if( msg.startsWith("Got: "))
			{
				//System.out.println( msg );
				String []list = PApplet.split(msg, ' ' );

				int dist = PApplet.unhex( list[2] );
				int nozz = PApplet.unhex( list[1] );

				addToHistory( distHist, dist );
				avgDist = movingAverage( distHist );

				addToHistory( nozzHist, nozz );
				avgNoz = movingAverage( nozzHist );

				//println( "minD: " + minD + " maxD: " + maxD + " maxBrushSize: " + maxBrushSize + " minBrushSize: " + minBrushSize + " brushSize: " + brushSize );    
				brushSize = PApplet.map( avgDist, minD, maxD, maxBrushSize, minBrushSize);

				if( isCalibrated() == false )
				{
					time = parent.millis();
					diff = time - oldtime;
					oldtime = time;
					freq = 1000 / diff;
					addToHistory( freqHist, freq );
				}		
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


	  void addToHistory( float [] hist, float newValue )
	  {
	    for( int i = 0; i < N - 1; i ++ )
	    {
	      hist[i] = hist[i+1];
	    }
	    hist[N - 1] = newValue;
	  }


	  float movingAverage( float [] hist )      
	  {
	    float sum = 0;
	    for (int i = 0; i<N; i++)
	    {
	      sum += hist[i];
	    }
	    return sum / N;
	  }


	@Override
	public void calibrate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return (int)brushSize;
	}

	@Override
	public int getNozzlePressure() {
		// TODO Auto-generated method stub
		return (int)avgNoz;
	}
	public boolean isNozzlePressed()
	{
		//TODO should be calibrated somewhere
		if( avgNoz < 250 )
			return true;
		return false;
	}
	@Override
	public boolean implementsButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean implementsDistance() {
		// TODO Auto-generated method stub
		return true;
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

	
	@Override
	public void wipeCalibration() {
		// TODO Auto-generated method stub

	}

}
