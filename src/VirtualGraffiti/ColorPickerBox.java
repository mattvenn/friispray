package VirtualGraffiti;

import processing.core.PApplet;
import processing.core.PVector;

class ColorPickerBox
{
	int x;
	int y;
	int segs;
	int steps;
	int boxWidth;
	int boxHeight;
	int cellHeight;
	int cellWidth;
	int borderWidth = 1;
	int currentColor, oldColor;
	boolean wipeButton = false;
	int brushSize = 1;
//       AudioSample rattle;
	int clearButtonStart;
	int brushBoxStart;
	int currentColorBoxHeight = 50;
//	PFont fontA;
	PApplet parent;
	
	ColorPickerBox( PApplet parent, int tx, int ty, int tboxWidth, int tboxHeight, int tsegs, int tsteps )
	{
    //            rattle = sample;
		this.parent = parent;
		x = tx;
		y = ty;
		segs = tsegs;
		steps = tsteps;
		boxWidth = tboxWidth;
		boxHeight = tboxHeight;
		cellWidth = boxWidth / steps;
		cellHeight = boxHeight / ( segs + 1 );
		brushBoxStart =  y + borderWidth + boxHeight + currentColorBoxHeight + 25;
		clearButtonStart =  brushBoxStart + currentColorBoxHeight + 25;
                currentColor = parent.color( 255 );
	//	fontA = loadFont("Ziggurat-HTF-Black-32.vlw");
	//	textFont(fontA, 30);
	}

void update( PVector xy )
{
	int X = (int)xy.x;
	int Y = (int)xy.y;
	if( X > x + borderWidth && X < boxWidth + x - borderWidth
	&& Y > y + borderWidth && Y < boxHeight + y - borderWidth )
	{
		currentColor = parent.get( X, Y );
                 if( currentColor != oldColor )
                 {
                   oldColor = currentColor;
                   //println( "new color" );
      //             rattle.trigger();
                 }
		drawColorBox();
	}

	if( X > x + borderWidth && X < boxWidth + x - borderWidth
	&&  Y > y + clearButtonStart && Y < clearButtonStart + currentColorBoxHeight )
	{
		wipeButton = true;
	}
	else
	{
		wipeButton = false;
	}

	if( X > x + borderWidth && X < boxWidth + x - 10
	&& Y > y + brushBoxStart && Y < y + brushBoxStart + currentColorBoxHeight )
	{
		brushSize =  X - x + 5; 
	}
}

int getCurrentColor()
{

  	return currentColor;
}
int getBrushSize()
{
	return brushSize;
}

boolean getWipeButton()
{
        if( wipeButton )
        {
          wipeButton = false;
          return true;
        }
  	return false;
}

void drawBrushBox()
{
	parent.fill( 0 );
	parent.pushMatrix();
	parent.translate( x + borderWidth, brushBoxStart );
	parent.rect( 0, 0, boxWidth, currentColorBoxHeight );
	parent.strokeWeight( 3 );
	parent.stroke( 255 );
	parent.fill( 255 );
	parent.line( 0, currentColorBoxHeight / 2 , boxWidth, currentColorBoxHeight / 2 );
	parent.rectMode( parent.CENTER );
	parent.rect( brushSize, currentColorBoxHeight / 2, 5, brushSize / 4 );
	parent.rectMode( parent.CORNER );
	parent.popMatrix();
}

public void drawColorBox()
{
	parent.stroke( 255 );
	parent.strokeWeight( 0 );
	parent.fill( currentColor );  
	parent.rect(  x + borderWidth, y + borderWidth + boxHeight + 10, boxWidth , currentColorBoxHeight - borderWidth );
  
}
 void display()
 {
	//draw where the current color is
	drawColorBox();
//	drawBrushBox();

	parent.fill( 0 );
	parent.stroke( 0 );
	//main box
	parent.rect( x, y, boxWidth + borderWidth * 2, boxHeight + borderWidth * 2 );
  
	//draw the color picker
	parent.pushMatrix();
	parent.translate ( x + borderWidth, y + borderWidth );
	for (int j = 0; j < steps; j++)
	{
		int[] cols = { 
				parent.color(255-(255/steps)*j, 255-(255/steps)*j, 0), 
				parent.color(255-(255/steps)*j, (int)((255/1.5)-((255/1.5)/steps)*j), 0), 
				parent.color(255-(255/steps)*j, (255/2)-((255/2)/steps)*j, 0), 
				parent.color(255-(255/steps)*j, (int)((255/2.5)-((255/2.5)/steps)*j), 0), 
				parent.color(255-(255/steps)*j, 0, 0), 
				parent.color(255-(255/steps)*j, 0, (255/2)-((255/2)/steps)*j), 
				parent.color(255-(255/steps)*j, 0, 255-(255/steps)*j), 
				parent.color((255/2)-((255/2)/steps)*j, 0, 255-(255/steps)*j), 
				parent.color(0, 0, 255-(255/steps)*j),
				parent.color(0, 255-(255/steps)*j, (int)((255/2.5)-((255/2.5)/steps)*j)), 
				parent.color(0, 255-(255/steps)*j, 0), 
				parent.color((255/2)-((255/2)/steps)*j, 255-(255/steps)*j, 0) };

		for (int i = 0; i <segs; i++)
		{
			parent.fill(cols[i]); 
			parent.rect(cellWidth *j , cellHeight * i, cellWidth , cellHeight );
		}
	} 
	//draw the black and white boxes
	parent.fill( 0 );
	parent.rect( 0, cellHeight * segs + 1, cellWidth *2  , cellHeight);
	parent.fill( 255 );
	parent.rect( cellWidth * 2 , cellHeight * segs + 1, cellWidth *2  , cellHeight);

	parent.popMatrix();

	//wipe button
	parent.strokeWeight( 3 );
	parent.stroke( 255 );
	parent.fill( 0 );
	parent.rect(  x + borderWidth, clearButtonStart, boxWidth , currentColorBoxHeight - borderWidth );
	parent.fill( 255 );
	parent.text( "wipe", x + 10, clearButtonStart + currentColorBoxHeight / 2 + 10 );

	}
}