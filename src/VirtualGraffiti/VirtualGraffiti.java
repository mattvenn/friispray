package VirtualGraffiti;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import processing.core.PApplet;
import processing.core.PFont;
import processing.opengl.*;
import javax.media.opengl.GL;
import sun.misc.Signal;
import sun.misc.SignalHandler;

//public class test extends PApplet {
public class VirtualGraffiti extends PApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//import ddf.minim.*;

	//	import fullscreen.*; 
	//import codeanticode.gsvideo.*;
	//import s373.flob.*;


	//P5Properties props;


	/*FullScreen fs; 
	AudioSample rattle;
	AudioPlayer spray;
	Minim minim;
	 */



	//where the config file is
	static String confFile = "/home/matthew/work/eclipseworkspace/virtualGraffiti/config/virtualPainting.properties";

	static P5Properties props;
	static boolean debug = false;
	double frameTime, oldFrameTime;

	PFont fontA;
	Thing thing;

	public static void main(String args[]) {
		OptionParser parser = new OptionParser() {
			{
				accepts( "config" ).withOptionalArg()
				.describedAs( "file" );
			}
		};

		OptionSet options = parser.parse( args );
		if( options.has( "config" ) )
		{
			confFile = (String)options.valueOf( "config" );
		}
		PApplet.main(new String[] {"VirtualGraffiti.VirtualGraffiti" });
	}


	public void setup() 
	{
		String imagePath, backgroundsPath, canType, trackerType;
		int w=0,h=0;

		try {
			props=new P5Properties();
			// load a configuration from a file inside the data folder
				props.load(new FileInputStream(confFile));

			w=props.getIntProperty("width",1024);
			h=props.getIntProperty("height",768);
			
			imagePath = props.getStringProperty( "imagePath", "./images/" ); 
			backgroundsPath = props.getStringProperty( "backgroundsPath",sketchPath + "/data/" ); 
			canType = props.getStringProperty( "canType", "Mouse" );
			trackerType = props.getStringProperty( "trackerType","Mouse" );

			System.out.println( "can type:'" + canType + "'");
			System.out.println( "tracker type:" + trackerType );
			System.out.println( "image save path:" + imagePath );
			System.out.println( "image load path:" + backgroundsPath );
			
			frameRate( 80 );
			size(w,h);
			thing = new Thing( this, canType, trackerType );
			thing.setup();

			} catch (FileNotFoundException e) {
				System.out.println("couldn't read config file:" + confFile + " : " + e );
				System.exit(1);
			} catch (IOException e) {
				System.out.println("couldn't read config file:" + confFile + " : " + e );
				System.exit(1);
			}
		catch(Exception e) {
				System.out.println("problem starting up: " + e );
				e.printStackTrace();
				System.exit(1);
			
		}
		

		Signal.handle(new Signal("INT"), new SignalHandler ()
		{
			public void handle(Signal sig) 
			{
				stop();
			}
		});
		


		//fontA = loadFont( rootPath + "/data/Ziggurat-HTF-Black-32.vlw");

		// Create the fullscreen object
		//	  fs = new FullScreen(this); 

		// enter fullscreen mode
		//  fs.enter(); 
		//load the sounds 

		/*
			  minim = new Minim(this);
		  rattle = minim.loadSample("rattle.wav", 2048);
		  if ( rattle == null ) println("Didn't get rattle!");
		  spray = minim.loadFile("spraylong.wav", 2048);
		  if( spray == null ) println( "didn't get spray!" );
		 */

		//can types
	}

	public void stop()
	{
		// always close Minim audio classes when you are done with them
		/*
		rattle.close();
	  spray.close();
	  minim.stop();
		 */
		System.out.println( "shutting down nicely");
		thing.stop();
		System.exit(0);
	}

	public void draw()
	{
		double count = 0;
		try
		{

			//first update the can tracker
			thing.update();
			if( thing.calibrated() )
			{
				oldFrameTime = frameTime;
				thing.paint();
				frameTime = millis();
				if( debug )
				{
					int fpsInt = (int)( 1000 / ( frameTime - oldFrameTime ));
					if( count ++ % 1000 == 0 )
						System.out.println( "fps: " + fpsInt );
				}
			}
			else
			{
				thing.calibrate();
			}
		}
		catch( Exception e )
		{
			System.out.println( "caught unhandled exception! : " + e );
			stop();
		}
	}	

	public void keyPressed()
	{
		if( key == 'c' )
		{
			thing.wipeCalibration();
		}
		if( key == 'l' )
		{
			thing.loadCalibration();
		}
		if( key == 's' )
		{
			thing.storeCalibration();
		}
		if( key == 'd' )
		{
			if( debug ) 
				debug = false;
			else
				debug = true;
		}
	}

}

