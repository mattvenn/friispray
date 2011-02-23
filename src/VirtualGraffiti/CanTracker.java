package VirtualGraffiti;

import processing.core.PVector;

public interface CanTracker
{
  boolean hasCan();
  void update();
  PVector getXY();
  boolean implementsDistance();
  int getDistance();
  boolean needsTransformation();
}