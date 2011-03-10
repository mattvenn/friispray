package VirtualGraffiti;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Line {


	ArrayList <PVector> mouse;

	int size = 20;
	int divider = 7;
	float iterations = 50;
	float alphaScale = (float) 0.05;
	PVector oldVec, cur;
	PApplet parent;
	public Line(PApplet parent)
	{
		this.parent = parent;
	   
	  mouse = new ArrayList();
//	  smooth();
	  oldVec = new PVector();
	  cur = new PVector();
	}
	void drawLine(PVector point1, PVector point2)
	{
	  PVector p = new PVector();
	  
	  for( float t = 0; t < 1 ; t += 1/iterations )
	  {
	    float  a = t;
	    float  b = 1 - t;
	    p.x = (point1.x * b) + (point2.x * a);
	    p.y = (point1.y * b) + (point2.y * a);

	    parent.ellipse( p.x, p.y, size,size );
	  }
	}

	void drawBezier(PVector A, PVector v1, PVector v2, PVector D )
	{ 
	 // println( "v1 = " + v1.x + "," + v1.y );
	  //println( "v2 = " + v2.x + "," + v2.y );

	  v1.div(divider);
	  v2.div(divider) ;
	  PVector B = PVector.add( A,v1 );
	  PVector C = PVector.add( D,v2);
	  PVector p = new PVector();
	  for( float t = 0; t < 1; t += 1/iterations )
	  {

	    float a = t;
	    float b = 1 - t;

	    p.x = A.x*b*b*b + 3*B.x*b*b*a + 3*C.x*b*a*a + D.x*a*a*a; 
	    p.y = A.y*b*b*b + 3*B.y*b*b*a + 3*C.y*b*a*a + D.y*a*a*a; 
	   
	    parent.ellipse( p.x,p.y,size,size);
	  }
	}

	void stopDraw(PVector xy)
	{
		  drawLine(xy);
		  mouse.clear();
	}
	void addPoint( PVector xy, int color, int size, int opacity )
	{
		parent.noStroke();
		this.size = size;
		parent.fill( color, opacity * alphaScale );
		mouse.add( xy.get() ) ;
	    drawLine(xy);
	}

	void drawLine(PVector xy)
	{
	 
	  if( mouse.size() == 1 )
	  {
	     parent.ellipse( xy.x,xy.y, size,size );
	  }
	  if( mouse.size() == 2 )
	  {
	    drawLine( mouse.get(1), mouse.get(0) );
//	    mouse.remove( 0  );
	  }
	  if( mouse.size() > 3 )
	  {
	    PVector entVec = PVector.sub( mouse.get(1), mouse.get(0) ); //new PVector(0,0);
	    PVector exVec = PVector.sub( mouse.get(2), mouse.get(3) );
	    drawBezier( mouse.get(1), entVec, exVec, mouse.get(2) );
	    mouse.remove( 0  );
	  }
	}
}
