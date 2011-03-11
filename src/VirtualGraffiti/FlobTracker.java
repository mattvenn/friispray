package VirtualGraffiti;

import java.util.ArrayList;

import codeanticode.gsvideo.GSCapture;
import hypermedia.video.Blob;
import processing.core.PApplet;
import processing.core.PVector;
import s373.flob.ABlob;
import s373.flob.Flob;

public class FlobTracker implements CanTracker {

	Flob flob;       
	ArrayList blobs;

	int tresh = 10;
	int fade = 15;
	int om = 1;
	int videoresw=320; //640; //320;
	int videoresh=240; //480; //240;

	int [] pointsX = new int[4];
	int [] pointsY = new int[4];
	int [] canX = new int[2];
	int [] canY = new int[2];

	boolean hasCan = false;
	PApplet parent;
	int w,h;
	int distance = 0;
	PVector xy;
	int videotex = 3;
	GSCapture video;
	FlobTracker(PApplet parent)
	{
		System.out.println( "flobtracker starting");

		xy = new PVector(0,0);
		this.parent = parent;
		video = new GSCapture(parent, videoresw, videoresh, "/dev/video0", VirtualGraffiti.fps);  
		flob = new Flob(videoresw, videoresh, parent.width, parent.height);

		flob.setMinNumPixels( 50 );
		flob.setMaxNumPixels( 150);
		flob.setSrcImage(videotex);
		flob.setImage(videotex);
		flob.setBackground(video);

		flob.setMirror(true,false);
		flob.setOm(0);
		flob.setBlur( 2 );
	}
	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return distance;
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
		return true;
	}

	@Override
	public boolean needsTransformation() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		try{
			if(video.available()) {
				video.read();
				blobs = flob.calc(flob.binarize(video));

				//			     println( "fps: " + 1000 / ( frameTime - oldFrameTime ) );
			}
			else
			{
				//println( "video not available" );
			}
			//			  PApplet.image(flob.getSrcImage(), 0, 0, width, height);
			int numblobs = blobs.size();
			//			  for(int i = 0; i < numblobs; i++) {
			//		    ABlob ab = (ABlob)flob.getABlob(i); 

			// ellipse( ab.cx, ab.cy, 20, 20 );
			/*   fill(0,0,255,100);
			    rect(ab.cx,ab.cy,ab.dimx,ab.dimy);
			    fill(0,255,0,200);
			    rect(ab.cx,ab.cy, 5, 5);
			    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
			    text(info,ab.cx,ab.cy+20);*/
			//	  }
			// println( numblobs );

			if( numblobs == 2 )
			{
				canX[0] = (int)flob.getABlob(0).cx;
				canY[0] = (int)flob.getABlob(0).cy;
				canX[1] = (int)flob.getABlob(1).cx;
				canY[1] = (int)flob.getABlob(1).cy;


				xy.x = canX[0];
				xy.y = canY[0];
				hasCan = true;
				float spotDistance = PApplet.sqrt(PApplet.sq(canX[0]-canX[1]) + PApplet.sq( canY[0]-canY[1] ));
				if( VirtualGraffiti.debug )
					System.out.println( "spot distance: " + spotDistance );
				//needs calibration TODO
				distance = (int)(PApplet.map( spotDistance,65 ,30, Thing.minBrushSize, Thing.maxBrushSize ));
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
			System.out.println( "flob track exception: " + e );
			System.out.println( "check camera isn't already in use" );
			System.exit(1);

		}
	}
}
