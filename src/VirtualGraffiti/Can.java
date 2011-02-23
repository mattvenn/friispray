package VirtualGraffiti;

 interface Can
{
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
}
