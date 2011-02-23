package VirtualGraffiti;

import java.io.File;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Thing
{
	String backgroundsPath = "/home/matthew/work/processingSketches/virtualGraffiti.old/data";
	String imagePath = "/tmp/";
	PApplet parent;
	Can can;
	CanTracker canTracker;
	CameraCalibration calibration;
	ColorPickerBox colorPicker;
	PVector xy;
	boolean isSpraying = false;
	//TODO
	boolean useDrips = true;
	int brushSize = 50;
	int opacity = 255;
	int savedImageCount = 0;
	Drips drips;
	
	Thing( PApplet parent, String canType, String trackerType )
	{
		this.parent = parent;
		xy = new PVector(0,0);
		
		//TODO
		int minOpacity = 70;
		int maxOpacity = 255;
		int maxBrushSize = 80;
		int minBrushSize = 10;
	    drips = new Drips( parent, minBrushSize, maxBrushSize, minOpacity, maxOpacity);
		colorPicker = new ColorPickerBox( parent, 5, 5, 100, 400, 12, 5 );
		calibration = new CameraCalibration( parent );

		if( canType == "Laser" )
		{
			//can = new LaserCan( minBrushSize, maxBrushSize );
		}
		else if( canType.contentEquals( "Matt" ) )
		{
			can = new MattCan( parent );
		}
		else if( canType.contentEquals( "Mouse" ))
		{
			can = new MouseCan( parent );
		}
		else if( canType.contentEquals( "LED"))
		{
			can = new LEDCan( parent );
		}
		else if( canType.contentEquals ("Nunchuck"))
		{
			can = new NunchuckCan( parent );
		}
		else
		{
			System.out.println( "no such can: '" + canType + "'" );
			System.exit( 1 );

		}

		//tracker types
		if( trackerType.contentEquals("Wiimote" ))
		{
			canTracker = new WiimoteTracker( parent );   
		}
		else if( trackerType.contentEquals("Video" ))
		{
			//Thread canTracker = new Thread(VideoTracker);
			//  canTracker.start();
		}
		else if( trackerType.contentEquals( "OpenCV" ) )
		{
			canTracker = new OpenCVTracker(parent);
		}
		else if( trackerType.contentEquals( "Mouse" ) )
		{
			canTracker = new MouseTracker(parent);
		}
		else
		{
			System.out.println( "no such tracker: '" + trackerType + "'" );
			System.exit( 1 );
		}
	}

	boolean isSpraying()
	{
		return isSpraying;
	}	
	
	void update()
	{
		canTracker.update();
		can.update();
		
		if( can.implementsButton() && can.getButton() )
		{
			System.out.println( "button" );
		}
		if( canTracker.hasCan() )
		{	
			xy =  canTracker.getXY();
			
			if( canTracker.needsTransformation() )
				xy = calibration.translate( xy );
			
			if( can.implementsNozzlePressure() )
			{
				if( can.isNozzlePressed() )
				{
					isSpraying = true;
				}
				else
				{
					isSpraying = false;
				}
			}
			else
			{
				isSpraying = true;
			}
		}
		else
		{
			isSpraying = false;
		}
		
		//color
		if( isSpraying )
					colorPicker.update( xy );
		//can stuff
		if( can.implementsDistance() )
		{
			brushSize = can.getDistance(); 
		}
		if( can.implementsNozzlePressure() )
		{
			opacity = getOpacity( can.getNozzlePressure());
		}
		
	
	}
	/* calibrations stuff
	 * 
	 */
	void calibrate()
	{
		if( canTracker.needsTransformation() && ! calibration.calibrated() )
		{
			//we have to do calibration
			if( isSpraying )
				calibration.addCalibrationPoint( canTracker.getXY() );
			calibration.drawCalibrationImage();
		}
	}

	void wipeCalibration()
	{
		if( canTracker.needsTransformation() )
			calibration.wipeCalibration();
	}
	void storeCalibration()
	{
		if( canTracker.needsTransformation() )
			calibration.storeCalibration();
	}
	void loadCalibration()
	{
		if( canTracker.needsTransformation() )
			calibration.loadCalibration();
	}
	
	boolean calibrated()
	{
		if( canTracker.needsTransformation() && ! calibration.calibrated() )
			return false;
		return true;
	}
	
	void paint()
	{
		colorPicker.display();
		parent.noStroke();
		parent.fill( colorPicker.getCurrentColor(), opacity );

		if( isSpraying )
		{
			parent.ellipse( xy.x, xy.y , brushSize, brushSize );
			//  System.out.println( "got can" );
			//if( ! spray.isPlaying() ) spray.loop();
			if( useDrips )
				drips.addDrip( xy, brushSize, opacity, colorPicker.getCurrentColor( ) ); 
		}
		else
		{
			//   if( spray.isPlaying() ) spray.pause();
		}

		//draw the drips
		if( useDrips )
			 drips.drawDrips();

		if( colorPicker.getWipeButton() )
		{
			System.out.println( "wiping background" );
			loadRandomBackground();
			//parent.background(0);
		}

	}
	

	int getOpacity( int avgNoz )
	{
		//TODO
		int minOpacity = 0;
		int maxOpacity = 255;
		int nozzleKnee = 150; 
		int opacityKnee = 50; 

		int opacity;
		if( avgNoz < nozzleKnee )
		{
			opacity = (int)PApplet.map( avgNoz, 0, nozzleKnee, 0, opacityKnee );
		}
		else
		{
			opacity = (int)PApplet.map( avgNoz, nozzleKnee, 255, opacityKnee, 255 );

		}
		//System.out.println( opacity );
		return opacity;
	}
	void loadRandomBackground()
	{
		savedImageCount ++;

		//save the image first
		 String saveName = imagePath + "image" + savedImageCount + ".jpg";
		 parent.save( saveName );

		//load random image
		File file = new File(backgroundsPath);
		ArrayList images = new ArrayList();
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];    
			String fileName = f.getName();
			if( fileName.endsWith( ".jpg" ) )
			{
				images.add( fileName );
			}
		}
		int randomImageNumber = (int)parent.random( 0, images.size() );
		System.out.println( "loading: " + backgroundsPath + "/" + (String)images.get( randomImageNumber ));
		PImage bg = parent.loadImage( backgroundsPath + "/" + (String)images.get( randomImageNumber ) );
		
		bg.resize( parent.width, parent.height);
		parent.background(bg);

	}
}
