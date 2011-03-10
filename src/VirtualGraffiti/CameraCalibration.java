package VirtualGraffiti;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import processing.core.PApplet;
import processing.core.PVector;

public class CameraCalibration {

	//TODO
	String calibrationFile = "/tmp/calib.prop"; 
	int correctX, correctY;
	float a1,b1,c1,a3,b3,a2,b2,c2;
	boolean calibrated = false;
	int calPoints = 0;

	//the calibration pattern
	PVector [] dots = new PVector[4];
	//where we put the wiimote spots
	PVector [] cal = new PVector[4];
	PApplet parent;

	CameraCalibration( PApplet parent )
	{
		this.parent = parent;
		int calibInset = 40;
		dots[0] = new PVector( calibInset, calibInset ); //top left
		dots[1] = new PVector( parent.width -calibInset, calibInset ); //top right
		dots[2] = new PVector( calibInset, parent.height -calibInset ); //bot left
		dots[3] = new PVector( parent.width -calibInset, parent.height -calibInset); //bot right

	}

  void wipeCalibration()
  {
      calibrated = false;
      calPoints = 0;
  }
  void storeCalibration()
  {
	P5Properties props=new P5Properties();
	// load a configuration from a file inside the data folder
	  if( calibrated )
	  {
		props.put( "a1", String.valueOf( a1 ) );
		props.put( "a2", String.valueOf( a2 ) );
		props.put( "a3", String.valueOf( a3 ) );
		props.put( "b1", String.valueOf( b1 ) );
		props.put( "b2", String.valueOf( b2 ) );
		props.put( "b3", String.valueOf( b3 ) );
		props.put( "c1", String.valueOf( c1 ) );
		props.put( "c2", String.valueOf( c2 ) );
	  }
	try
	{
		props.store(new FileOutputStream( calibrationFile ), null);
		System.out.println( "wrote calibration data to:" + calibrationFile );
	}
	catch( IOException e )
	{
		System.out.println( "couldn't write calibration data to file: " + calibrationFile + " : " + e );
	}
  }
  
  void loadCalibration()
  {
	P5Properties props=new P5Properties();
	try
	{
		props.load(new FileInputStream( calibrationFile ) );
		a1 = props.getFloatProperty( "a1", 0 );
		a2 = props.getFloatProperty( "a2", 0 );
		a3 = props.getFloatProperty( "a3", 0 );
		b1 = props.getFloatProperty( "b1", 0 );
		b2 = props.getFloatProperty( "b2", 0 );
		b3 = props.getFloatProperty( "b3", 0 );
		c1 = props.getFloatProperty( "c1", 0 );
		c2 = props.getFloatProperty( "c2", 0 );
		
		calibrated = true; 
		calPoints = 0;
		System.out.println( "read calibration from: " + calibrationFile );
	}
	catch( IOException e )
	{
		System.out.println( "couldn't read calibration data from file: " + calibrationFile + " : " + e );
	}
	  
  }
	boolean calibrated()
	{
		return calibrated;
	}
	void drawCalibrationImage()
	{
		int spotSize = 40;
		int lineLength = spotSize / 2;
		// System.out.println( calPoints );
		parent.background( 0 );
		parent.stroke( 255 );
		parent.strokeWeight( 1 );
		parent.fill( 0 );

		parent.ellipse( dots[calPoints].x, dots[calPoints].y, lineLength * 2, lineLength * 2 );

		parent.line( dots[calPoints].x - lineLength, dots[calPoints].y, dots[calPoints].x + lineLength, dots[calPoints].y );
		parent.line( dots[calPoints].x, dots[calPoints].y - lineLength, dots[calPoints].x, dots[calPoints].y + lineLength );

		//draw the points already got
		parent.fill( 255 );
		for( int i = 0; i < calPoints ; i ++ )
		{
			parent.ellipse( cal[i].x, cal[i].y, spotSize, spotSize );
		}
	}

	PVector translate( PVector xy )
	{
		float corrX = (a1 * xy.x + b1 * xy.y + c1 ) / (a3 * xy.x + b3 * xy.y + 1 );
		float corrY = (a2 * xy.x + b2 * xy.y + c2 ) / (a3 * xy.x + b3 * xy.y + 1 );

		return new PVector( (int)corrX, (int)corrY );
	}

	void addCalibrationPoint( PVector xy)
	{
		System.out.println( "adding new calibration point " + calPoints + " at " + xy.x + "," + xy.y );
	
		//store a new object, rather than the one from the tracking system.
		cal[calPoints ++] = new PVector( xy.x, xy.y );
		
		if( calPoints == 4 )
		{
			// call the calibration routine
			if( calibrate() )
			{
				calibrated = true; 
				calPoints = 0;
			}
			else
			{
				System.out.println( "calibration failed" );
				//recalibrate
				calPoints = 0;
			}

		}
		parent.delay( 500 );
	}


	boolean calibrate()
	{
		System.out.println( "running calibration" );

		float [][] matrix = { 
				{ 
					-1, -1, -1, -1, 0, 0, 0, 0     }
				,
				{   
					-cal[0].x, -cal[1].x, -cal[2].x, -cal[3].x, 0, 0, 0, 0     }
				,
				{ 
					-cal[0].y, -cal[1].y, -cal[2].y, -cal[3].y, 0,0,0,0     }
				,
				{ 
					0,0,0,0,-1,-1,-1,-1     }
				,
				{ 
					0,0,0,0, -cal[0].x, -cal[1].x, -cal[2].x, -cal[3].x     }
				,
				{ 
					0,0,0,0, -cal[0].y, -cal[1].y, -cal[2].y, -cal[3].y     }
				,
				{ 
					cal[0].x * dots[0].x, cal[1].x * dots[1].x, cal[2].x * dots[2].x, cal[3].x * dots[3].x, cal[0].x * dots[0].y, cal[1].x * dots[1].y, cal[2].x * dots[2].y, cal[3].x * dots[3].y     }
				,
				{ 
					cal[0].y * dots[0].x, cal[1].y * dots[1].x, cal[2].y * dots[2].x, cal[3].y * dots[3].x, cal[0].y * dots[0].y, cal[1].y * dots[1].y, cal[2].y * dots[2].y, cal[3].y * dots[3].y     }
				,
		};

		float [] bb = { 
				-dots[0].x, -dots[1].x, -dots[2].x, -dots[3].x, -dots[0].y, -dots[1].y, -dots[2].y, -dots[3].y   };

		// gauß-elimination
		for( int j = 1; j < 4; j ++ )
		{
			for( int i = 1; i < 8; i ++ )
			{
				matrix[i][j] = - matrix[i][j] + matrix[i][0];
			}
			bb[j] = -bb[j] + bb[0];
			matrix[0][j] = 0;
		}


		for( int i = 2; i < 8; i ++ )
		{
			matrix[i][2] = -matrix[i][2] / matrix[1][2] * matrix[1][1] + matrix[i][1];
		}
		bb[2] = - bb[2] / matrix[1][2] * matrix[1][1] + bb[1];
		matrix[1][2] = 0;


		for( int i = 2; i < 8; i ++ )
		{
			matrix[i][3] = -matrix[i][3] / matrix[1][3] * matrix[1][1] + matrix[i][1];
		}
		bb[3] = - bb[3] / matrix[1][3] * matrix[1][1] + bb[1];
		matrix[1][3] = 0;



		for( int i = 3; i < 8; i ++ )
		{
			matrix[i][3] = -matrix[i][3] / matrix[2][3] * matrix[2][2] + matrix[i][2];
		}
		bb[3] = - bb[3] / matrix[2][3] * matrix[2][2] + bb[2];
		matrix[2][3] = 0;

		for( int j = 5; j < 8; j ++ )
		{
			for( int i = 4; i < 8; i ++ )
			{
				matrix[i][j] = -matrix[i][j] + matrix[i][4];
			}
			bb[j] = -bb[j] + bb[4];
			matrix[3][j] = 0;
		}

		for( int i = 5; i < 8; i ++ )
		{
			matrix[i][6] = -matrix[i][6] / matrix[4][6] * matrix[4][5] + matrix[i][5];
		}

		bb[6] = - bb[6] / matrix[4][6] * matrix[4][5] + bb[5];
		matrix[4][6] = 0;


		for( int i = 5; i < 8; i ++ )
		{
			matrix[i][7] = -matrix[i][7] / matrix[4][7] * matrix[4][5] + matrix[i][5];
		}
		bb[7] = - bb[7] / matrix[4][7] * matrix[4][5] + bb[5];
		matrix[4][7] = 0;


		for( int i = 6; i < 8; i ++ )
		{
			matrix[i][7] = -matrix[i][7] / matrix[5][7] * matrix[5][6] + matrix[i][6];
		}
		bb[7] = - bb[7] / matrix[5][7] * matrix[5][6] + bb[6];
		matrix[5][7] = 0;

		matrix[7][7] = - matrix[7][7]/matrix[6][7]*matrix[6][3] + matrix[7][3];
		bb[7] = -bb[7]/matrix[6][7]*matrix[6][3] + bb[3];
		matrix[6][7] = 0;


		b3 =  bb[7] /matrix[7][7];
		a3 = (bb[3]-(matrix[7][3]*b3))/matrix[6][3];
		b2 = (bb[6]-(matrix[7][6]*b3+matrix[6][6]*a3))/matrix[5][6];
		a2 = (bb[5]-(matrix[7][5]*b3+matrix[6][5]*a3+matrix[5][5]*b2))/matrix[4][5];
		c2 = (bb[4]-(matrix[7][4]*b3+matrix[6][5]*a3+matrix[5][4]*b2+matrix[4][4]*a2))/matrix[3][4];
		b1 = (bb[2]-(matrix[7][2]*b3+matrix[6][2]*a3+matrix[5][2]*b2+matrix[4][2]*a2+matrix[3][2]*c2))/matrix[2][2];
		a1 = (bb[1]-(matrix[7][1]*b3+matrix[6][1]*a3+matrix[5][1]*b2+matrix[4][1]*a2+matrix[3][1]*c2+matrix[2][1]*b1))/matrix[1][1];
		c1 = (bb[0]-(matrix[7][0]*b3+matrix[6][0]*a3+matrix[5][0]*b2+matrix[4][0]*a2+matrix[3][0]*c2+matrix[2][0]*b1+matrix[1][0]*a1))/matrix[0][0];

		if( Float.isNaN( b3 ) ) return false;
		if( Float.isNaN( b2 ) ) return false;
		if( Float.isNaN( a2 ) ) return false;
		if( Float.isNaN( c2 ) ) return false;
		if( Float.isNaN( a3 ) ) return false;
		if( Float.isNaN( b1 ) ) return false;
		if( Float.isNaN( a1 ) ) return false;
		if( Float.isNaN( c1 ) ) return false;
		System.out.println( "calibrated OK" );
		return true;
	}

}
/* some old stuff here:
/*
//spot found
if( canTracker.hasCan() )
{
  //lightDetected = true;
  //don't look for next cal straight away in case led switch causes flickers

//	if( calibrationTimer -- == 0 ) waitingForNextCalibration = true;
//   if( calibrationTimer < 0 ) calibrationTimer = 0;
  rawX = canTracker.getX();
  rawY = canTracker.getY();

}
else
{
  calibrationTimer = calibrationDelay;
  lightDetected = false;
}


if( useWii && calibrated == false )
{
if( waitingForNextCalibration == true && calibrationTimer == 0 )
{
  getCalibrationPoint();
  //wait for light to go on again
  calibrationTimer = calibrationDelay;
  calibrationPhase ++;
  waitingForNextCalibration = false;
  delay( 500 );
}

drawCalibrationImage();  
fill( 255 );
noStroke();
text( "calibration part " + calibrationPhase, 0, height /2 - 80 );
if( calibrationTimer == 0 ) fill( color( 255, 0, 0 ) );

ellipse( wiiX, wiiY, 20,20 );

text( "hold can over target and press nozzle down...", 0, height / 2 - 40 );
text( calibrationTimer, 0, height / 2 );
}
else if( useWii && calibrated == false && justCalibratedWii == true )
{
//just failed to calibrate
delay( 1000 );
}
else if( useWii && calibrated == true && justCalibratedWii == true )
{
background( 0 );
fill( 255 );
text("Wiimote calibrated OK", 0, height /2 - 40 );
if( useCan )
  text("now we calibrate the can...", 0, height /2 );
//  println( calibrationTimer + " > " + calibrationPause );
if( pauseTimer ++ > calibrationPause ) 
{

  justCalibratedWii = false;
  pauseTimer = 0;
}

}
 */