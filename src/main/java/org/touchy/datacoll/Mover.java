package org.touchy.datacoll;

public interface Mover {

	void init(int gameWidth, int gameHeight);
	
	void update(double tpf, int gameWidth, int gameHeight);
	
	double getSpotX();
	
	double getSpotY();
	
	double getArrowX();
	
	double getArrowY();
	
	double getArrowAngle();
	
	boolean stop();
	
}
