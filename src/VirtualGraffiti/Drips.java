package VirtualGraffiti;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

class Drips
{
	ArrayList drips;
	int dripAmount = 6;
	int mouseHistory = 20;
	int dripSize = 7;
	int minBrushSize;
	int maxBrushSize;
	int minOpacity;
	int maxOpacity;
	PApplet parent;

	PVector [] oldMouse = new PVector[ mouseHistory ];
	Drips( PApplet parent, int tminBrushSize, int tmaxBrushSize, int minOpacity, int maxOpacity)
	{
		this.parent = parent;
		minBrushSize = tminBrushSize;
		maxBrushSize = tmaxBrushSize;
		this.minOpacity = minOpacity;
		this.maxOpacity = maxOpacity;
		newMouseHistory();
		drips = new ArrayList(); 
	} 

	//makes a new blank mouse history array
	void newMouseHistory()
	{
		for( int i = 0; i < mouseHistory; i ++ )
		{
			oldMouse[i] = new PVector( 0, 0 );
		}
	}

	/* the juicy bit!

we go back through the mouse history. The bigger the brushsize, the longer we look back into the history

we look to see if the average of the history is the same as where the mouse is now (i.e. slow moving)

if moving slow enough 

todo: why is history check ever getting down to 1?
	 */
	void addDrip(PVector xy, int brushSize, int brushOpacity, int dripColor )
	{
		addHistory( xy );
		int X = (int)xy.x;
		int Y = (int)xy.y;
		int totalX = 0;
		int totalY = 0;
		// println( "brush: " + brushSize );
		// println( "minOpac: " + minOpacity + ", minBrush: " + minBrushSize );
		// println( "mo: " + (maxOpacity + 90 - brushOpacity ) );
		int historyCheck = (int)PApplet.map( brushSize * (maxOpacity + 90 - brushOpacity), minBrushSize * minOpacity, maxBrushSize * maxOpacity, 3, mouseHistory );    
		if( historyCheck > mouseHistory ) historyCheck = mouseHistory;
		// println( "brush * opac: " + brushSize * (maxOpacity + 90 - brushOpacity) + " min: " + minOpacity * minBrushSize + " max: " + maxBrushSize * maxOpacity + " historyCheck: " + historyCheck );
		for( int i = 0; i < historyCheck ; i ++ )
		{
			totalX += oldMouse[i].x ;
			totalY += oldMouse[i].y ;
		}
		totalX /= historyCheck;
		totalY /= historyCheck;

		//compare the current with the mode
		float dripScore =  PApplet.abs(totalX - X ) + PApplet.abs(totalY - Y );

		// println( dripScore );
		if( dripScore < dripAmount )
		{
			int dripLength = (int)( (dripAmount - dripScore ) * 20 );
			drips.add( new Drip( parent, X, Y, dripLength, dripColor ) ) ;
			//stop too many drips
			newMouseHistory();
		}

	}


	//iterates through the current drips, drawing them. If they are done, then remove them from the list
	void drawDrips()
	{
		//draw the drips
		for( int i = drips.size() - 1; i >= 0; i -- )
		{
			Drip drip = (Drip) drips.get(i);
			drip.drawDrip();
			if( drip.finished() )
			{
				drips.remove(i);
			}
		}
	}

	//shifts the array along one to the right and adds the new mouse position
	void addHistory(PVector xy)
	{

		int X = (int)xy.x;
		int Y = (int)xy.y;
		for( int i = mouseHistory - 1; i >= 1 ; i -- )
		{
			oldMouse[ i ].x = oldMouse[ i - 1].x;
			oldMouse[ i ].y = oldMouse[ i - 1].y;    
		}
		oldMouse[0].x = X;
		oldMouse[0].y = Y;
	}

}

class Drip
{
	int X;
	int Y;
	int dripLength;
	int brushSize;
	int progress;
	int dripColor;
	PApplet parent;
	Drip( PApplet parent, int tX, int tY, int tlength, int tdripColor )
	{
		this.parent = parent;
		X = tX;
		Y = tY;
		dripLength = tlength;
		brushSize = 7;
		progress = 0;
		dripColor = tdripColor;
	}

	//draw the drip
	void drawDrip()
	{
		parent.noStroke();
		parent.fill( dripColor );
		parent.ellipse( X, Y + progress ++, brushSize, brushSize );   
	}

	boolean finished()
	{
		if( progress > dripLength ) return true;
		return false;
	}  
}

