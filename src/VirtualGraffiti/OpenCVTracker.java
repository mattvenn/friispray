/*
 * not working!!
 */
package VirtualGraffiti;

import hypermedia.video.Blob;
import hypermedia.video.OpenCV;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PVector;

public class OpenCVTracker implements CanTracker {

	OpenCV opencv;

	PVector xy;
	int threshold = 80;
	boolean find=true;

	boolean hasCan = false;
	PApplet parent;
	int w,h;
	Blob[] blobs; 
	OpenCVTracker(PApplet parent )
	{
		System.out.println( "openCV tracker starting");
		
		xy = new PVector(0,0);
		this.parent = parent;
		opencv = new OpenCV( parent );
		//TODO
		w = 320;
		h = 240;
		opencv.capture(w,h);
		opencv.remember();
	}
	public void update()
	{
		opencv.read();
		//opencv.flip( OpenCV.FLIP_HORIZONTAL );

	//	parent.image( opencv.image(), 10, 10 );	            // RGB image
	//	parent.image( opencv.image(OpenCV.GRAY), 20+w, 10 );   // GRAY image
	//	parent.image( opencv.image(OpenCV.MEMORY), 10, 20+h ); // image in memory

		opencv.absDiff();
		opencv.threshold(threshold);
	//	parent.image( opencv.image(OpenCV.GRAY), 20+w, 20+h ); // absolute difference image


		// working with blobs
		//min, max area, max blobs, find holes
		blobs = opencv.blobs( 100, w*h/3, 20, true );
		Point centroid = new Point();
		if( blobs.length > 0 )
		{
			hasCan = true;
			centroid = blobs[0].centroid;
			System.out.println( centroid.getX() + ":" +  centroid.getY() );
			xy.x = (int)centroid.getX();
			xy.y = (int)centroid.getY();
		}
		else
		{
			hasCan = false;
		}
	}

	public void stop() {
		opencv.stop();
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
	public void setup() {
		// TODO Auto-generated method stub
		
	}


}
