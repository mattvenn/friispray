package VirtualGraffiti;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Interpolator
{
	Point2D position = new Point2D.Double( 0, 0 );
	Point2D velocity = new Point2D.Double( 0, 0 );
	Point2D acceleration = new Point2D.Double( 0, 0 );
	double accelFactor = 0.15;
	double friction = 0.55;
	Point2D target = new Point2D.Double(0, 0);
	boolean isTracking = false;
	
	public void startLine( double[] pos )
	{
		isTracking = true;
		acceleration.setLocation( 0, 0 );
		velocity.setLocation( 0, 0 );
		position.setLocation( pos[0], pos[1] );
		target.setLocation( pos[0], pos[1] );
	}
	public void updateLine( double[] pos )
	{
		target.setLocation( pos[0], pos[1] );
	}
	
	public void endLine()
	{
		isTracking = false;
	}
	
	public void updatePosition()
	{
		if( ! isTracking ) return;
		//Update acceleration based on position of input
		acceleration.setLocation( accelFactor * (target.getX() -position.getX()),
				accelFactor * (target.getY() -position.getY() ));
		//Update velocity
		double velScale = 1-friction;
		velocity.setLocation( velScale * velocity.getX() + acceleration.getX(), velScale * velocity.getY() + acceleration.getY() );
		position.setLocation( velocity.getX() + position.getX(), velocity.getY() + position.getY() );
		
	}
	
	public double[] getPosition()
	{
		if( position == null ) return new double[] { 0, 0 };
		return new double[] { position.getX(), position.getY() };
	}
	
	public Point2D getVelocity() {return velocity;}
	public Point2D getAcceleration() {return acceleration;}
	public Point2D getTarget() {return acceleration;}
	public boolean isTracking()
	{
		return isTracking;
	}
	public void setAccelFactor( double accelFactor )
	{
		this.accelFactor = accelFactor;
	}
	public void setFriction( double friction )
	{
		this.friction = friction;
	}

}
