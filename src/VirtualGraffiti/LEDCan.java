package VirtualGraffiti;

import processing.core.PApplet;

public class LEDCan implements Can {

	PApplet parent;
	LEDCan(PApplet parent)
	{
		this.parent = parent;
	}
	@Override
	public void calibrate() {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean getButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNozzlePressure() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean implementsButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean implementsDistance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean implementsNozzlePressure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCalibrated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void wipeCalibration() {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean isNozzlePressed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
