package VirtualGraffiti;

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.*;
import java.util.Timer;

import javax.swing.*;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;



public class BitmapDrawer extends LineDrawer
{
	private static final double LINE_SCALE = 1.1;
	private static final double BLOB_POS_NOISE = 5;
	Interpolator tracker;
	long updateTime = 10;
	long paintTime = 30;
	private boolean drawOvals = false;
	private boolean drawLines = false;
	private boolean drawContLines = false;
	private boolean drawBlobs = true;
	double VEL_SCALE = 0.0003;
	double STROKE_SIZE = 30;
	double ALPHA_SCALE = 0.6;
	double SIZE_SCALE = 0.4;
	double MIN_SIZE=0.05;
	int[] oldPos = new int[2];
	//config for low opacity
	int blobSizeSlow = 40;
	double blobFadeSlow = 0.02; //power law on distance
	double blobMultSlow = 0.1;  //multiplyer on alpha value
	double blobMaxSlow = 0.01;  //max alpha value
	//config for high opacity
	int blobSizeFast = 40;
	double blobFadeFast = .7;
	double blobMultFast = 0.2;
	double blobMaxFast = 0.2;
	
	int numBlobs = 60;
	SprayBlob[] blobs = new SprayBlob[numBlobs];
	PApplet parent;
	
	
	public BitmapDrawer( PApplet parent )
	{
		super( parent );
		this.parent = parent;
		
		for( int i = 0; i < blobs.length; i++ )
			blobs[i] = createBlob( i );
	}
	
	public void drawPoint(PVector xy, int opacity, int size, int color )
	{
		//parent.ellipse( xy.x, xy.y, size,size );
		SprayBlob blob = randBlob( opacity/255.0 ); //1-spd );
		parent.imageMode(PApplet.CENTER);
		parent.image( blob, (float)noiseP( xy.x, BLOB_POS_NOISE ), (float)noiseP( xy.y, BLOB_POS_NOISE ), size, size);
	}
	
	public void setColor( int color )
	{
		for( SprayBlob blob : blobs ) blob.setColor(color);
	}
	private SprayBlob createBlob( int i )
	{
		double prop = (double)i/numBlobs;
		int size = (int)noise( interp( blobSizeSlow, blobSizeFast, prop ), 0.5 );
		double fade = noise( interp( blobFadeSlow, blobFadeFast, prop ), 0.5 ); 
		double mult = noise( interp( blobMultSlow, blobMultFast, prop ), 0.5);
		double max = noise( interp( blobMaxSlow, blobMultFast, prop ), 0.5 );
		SprayBlob blob = new SprayBlob( parent, size, fade, mult, max );
		return blob;
	}
	
	static double noiseP( double val, double amount )
	{
		double add = amount * (random()-0.5);
		return val + add;
	}
	static double noise( double val, double proportion )
	{
		double range = proportion / 2;
		double mult = (1+(random() * range * 2 )-range );
		return val * mult;
	}
	
	static double interp( double start, double end, double amount )
	{
		return start + amount * (end-start);
	}
	
	SprayBlob randBlob()
	{
		return blobs[(int)((double)blobs.length*random())];
	}
	SprayBlob randBlob( double vel )
	{
		int index = (int) noiseP(blobs.length*vel, 0.3*blobs.length+1 );
		index = max( 0, min( blobs.length-1, index ) );
		return blobs[index];
	}

	
	/**
	 * Returns a double from 0..1 to scale things according to speed. 1 is slow, 0 is fast
	 * @param vel
	 * @return
	 */
	double speedScale( Point2D vel )
	{
		double v = vel.distance( 0, 0 );
		double size = 0;
		if( v <= 0 ) return 1;
		double nat = VEL_SCALE/(v/(double)updateTime) ;
		size = max( 0, min( 1, nat ) );
		return size;
	}
}