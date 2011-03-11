package VirtualGraffiti;

 interface Can
{
 public boolean resetSerial = false;
  void update();
  boolean isCalibrated();
  void wipeCalibration();
  void calibrate();
  boolean implementsDistance();
  int getDistance();
  boolean implementsNozzlePressure();
  int getNozzlePressure();
  boolean isNozzlePressed();
  boolean implementsButton(); 
  boolean getButton();
  void stop();
}
