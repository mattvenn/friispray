package VirtualGraffiti;

import java.io.File;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Thing
{
	//	String backgroundsPath = "/home/matthew/work/processingSketches/virtualGraffiti.old/data";
	//String imagePath = "/tmp/";
	PApplet parent;
	Can can;
	CanTracker canTracker;
	CameraCalibration calibration;
	ColorPickerBox colorPicker;
	LineDrawer line;
	PVector xy;
	
	boolean isSpraying = false;


	int savedImageCount = 0;
	Drips drips;

	//get setup from config file
	boolean useDrips;
	static int minOpacity;
	static int maxOpacity;
	static int maxBrushSize;
	static int minBrushSize;
	int brushSize;
	int opacity;
	int currentColor;

	Thing( PApplet parent, String canType, String trackerType )
	{
		this.parent = parent;
		xy = new PVector(0,0);

		//config
		minOpacity = VirtualGraffiti.props.getIntProperty( "brush.minOpacity", 70 );
		maxOpacity = VirtualGraffiti.props.getIntProperty( "brush.maxOpacity", 255 );
		minBrushSize = VirtualGraffiti.props.getIntProperty( "brush.minBrushSize", 10 );
		maxBrushSize = VirtualGraffiti.props.getIntProperty( "brush.maxBrushSize", 80 );
		String lineType = VirtualGraffiti.props.getStringProperty( "lineType", "Simple" );

		//defaults
		opacity = VirtualGraffiti.props.getIntProperty( "brush.defaultOpacity", 255 );
		brushSize = VirtualGraffiti.props.getIntProperty( "brush.defaultBrushSize", 50 );

		useDrips = VirtualGraffiti.props.getBooleanProperty( "drips", false );

		//create helpers
		drips = new Drips( parent, minBrushSize, maxBrushSize, minOpacity, maxOpacity);
		colorPicker = new ColorPickerBox( parent, 5, 5, 100, 400, 12, 5 );
		calibration = new CameraCalibration( parent );
		
		if( lineType.contentEquals( "Simple" ))
		{
			line = new LineDrawer(parent);
		}
		else if( lineType.contentEquals( "Bitmap" ))
		{
			line = new BitmapDrawer(parent);
		}
		else
		{
			System.out.println( "no such linetype: '" + lineType + "'" );
			System.exit( 1 );
		}
		
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
		else if( trackerType.contentEquals("LaserWiimote" ))
		{
			canTracker = new LaserWiimoteTracker( parent );   
		}
		else if( trackerType.contentEquals("Video" ))
		{
			//Thread canTracker = new Thread(VideoTracker);
			//  canTracker.start();
		}
		else if( trackerType.contentEquals( "Flob" ) )
		{
			canTracker = new FlobTracker(parent);
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

	void setup()
	{
		canTracker.setup();
	}
	void stop()
	{
		canTracker.stop();
		can.stop();
	}
	boolean isSpraying()
	{
		return isSpraying;
	}	

	void update()
	{
		canTracker.update();
		can.update();

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
			brushSize = can.getDistance( ); 
		}
		if( canTracker.implementsDistance() )
		{
			brushSize = canTracker.getDistance();
		}
		if( can.implementsNozzlePressure() )
		{
			opacity = getOpacity( can.getNozzlePressure());
		}

		if( VirtualGraffiti.debug )
		{
			System.out.println( "has can: " + canTracker.hasCan() );
			System.out.println( "isspray: " + isSpraying);
			System.out.println( "distance: " + canTracker.getDistance());
			System.out.println( "color: " + colorPicker.getCurrentColor() );
			System.out.println( "nozzlePressure: " + can.getNozzlePressure() );
			System.out.println( "opacity: " + opacity );
			System.out.println( "brushSize: " + brushSize );
			System.out.println( "xy: " + xy.x + "," + xy.y );
			if( can.implementsButton() && can.getButton() )
				System.out.println( "button" );
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
		parent.stroke( colorPicker.getCurrentColor(), opacity );
		if( colorPicker.getCurrentColor() != currentColor )
		{
			currentColor = colorPicker.getCurrentColor();
			line.setColor(currentColor);
		}
		if( isSpraying )
		{
			line.addPoint(xy, brushSize, opacity );
						
		//	 System.out.println( "spraying right now: " + xy.x+ ":" + xy.y + " bs: " + brushSize );
			//if( ! spray.isPlaying() ) spray.loop();
			if( useDrips )
				drips.addDrip( xy, brushSize, opacity, colorPicker.getCurrentColor( ) ); 
		}
		else
		{
			//   if( spray.isPlaying() ) spray.pause();
			line.stopDraw(xy);
		}

		//draw the drips
		if( useDrips )
			drips.drawDrips();
		
		if( colorPicker.getWipeButton() )
		{
			System.out.println( "wiping background" );
			loadRandomBackground();
			parent.delay( 200 );
			//parent.background(0);
		}

	}


	int getOpacity( int nozzle )
	{
		return (int)PApplet.map( nozzle, 0, 255, minOpacity, maxOpacity );
	}

	void loadRandomBackground()
	{
		savedImageCount ++;
		//save the image first
		String saveDir = VirtualGraffiti.props.getProperty("imagePath", "./images" );
		String saveImagePath = saveDir + "image" + savedImageCount + ".jpg";
		File saveDirFile = new File(saveDir);
		if (saveDirFile.exists())
		{
			System.out.println( "saving image: " + saveImagePath);
			parent.save( saveImagePath );
		}
		else
		{
			System.out.println( "not saving image because imagePath doesn't exist:" + saveDir );
		}

		//load random image
		String backgroundsPath = VirtualGraffiti.props.getProperty("backgroundsPath", "./backgrounds" );
		File file = new File(backgroundsPath);
		if( file.exists() )
		{
			ArrayList<String> images = new ArrayList<String>();
			File[] files = file.listFiles();
			if( files.length > 0)
			{
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
				String imagePath = backgroundsPath + "/" + (String)images.get( randomImageNumber );
				PImage bg = parent.loadImage( imagePath );
				bg.resize( parent.width, parent.height);
				parent.background(bg);
			}
		}
		else
		{
			System.out.println( "not loading image because no images found in:" + backgroundsPath );
			parent.background( 0 );
		}


	}
}
