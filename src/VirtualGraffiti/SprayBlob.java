/**
 * 
 */
package VirtualGraffiti;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import processing.core.PApplet;
import processing.core.PImage;

class SprayBlob extends PImage
{
	/**
	 * 
	 */
	public int size;
	double fade = 0.2;
	double alphaScale = 0.5;
	double alphaMax = 0.1;
	PApplet parent;
	PImage alphaImage;
	
	public SprayBlob( PApplet parent, int size, double fade, double alphaScale, double alphaMax )
	{
		super( size, size, BufferedImage.TYPE_INT_ARGB );
		this.parent = parent;
		this.size=size;
		alphaImage = parent.createImage(size, size, PImage.RGB);
		
		Point2D ctr = new Point2D.Double(size/2.0,size/2.0);
		for( int x = 0; x < size; x++ )
			for( int y = 0; y < size; y++ )
			{
				double alpha = alphaScale * ( 1-pow( min( 1, max( 0, ctr.distance( x, y )/(size/2) ) ), fade ) );
				alpha = min( alpha, alphaMax );
				alpha = BitmapDrawer.noise( alpha, 0.9 );
				int intAlpha = (int)(alpha * 255);
				alphaImage.set( x, y, parent.color( intAlpha, intAlpha, intAlpha ));
			}
	}
	
	public void setColor( int color )
	{
		this.loadPixels();
		for (int i = 0; i < pixels.length; i++) 
			 pixels[i] = color;
		this.updatePixels();
		mask( alphaImage );
	}
	
}