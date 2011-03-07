package VirtualGraffiti;

import processing.core.PVector;

public interface CanTracker
{
  boolean hasCan();
  void setup();
  void update();
  PVector getXY();
  boolean implementsDistance();
  int getDistance();
  boolean needsTransformation();
  void stop();
}