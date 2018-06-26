package org.touchy.datacoll;

import org.touchy.datacoll.DataCollector.Corner;
import org.touchy.datacoll.DataCollector.Direction;

import com.almasb.fxgl.core.math.FXGLMath;

public class LinearMover extends RandomMover {

	private boolean stop = false;
	private boolean fromBottom = false;

	public LinearMover(int speed, double spotWidth, double spotHeigth, boolean fromBottom) {
		super(speed, spotWidth, spotHeigth);
		this.fromBottom = fromBottom;
	}

	@Override
	protected void initInternal(int gameWidth, int gameHeight) {
		spotX = 0;
		spotY = fromBottom ? gameHeight - spotWidth : 0;
		futureDirectionAngle = changeDirection(gameWidth, gameHeight, fromBottom ? Direction.BOTTOM : Direction.TOP,
				null);
	}

	@Override
	protected double changeDirection(int gameWidth, int gameHeight, Direction nearSide, Corner nearCorner) {
		double result = futureDirectionAngle;
		double randomAngle = FXGLMath.getRandom().nextDouble() * FXGLMath.PI / 6;
		if (nearSide != null) {
			switch (nearSide) {
			case TOP:
				result = FXGLMath.PI / 2 - randomAngle;
				break;
			case BOTTOM:
				result = (3 * FXGLMath.PI / 2) + randomAngle;
				break;
			case RIGHT:
				if (isOnRightSide(spotX + spotWidth, spotY + spotHeight, spotWidth, spotHeight, gameWidth,
						gameHeight)) {

				}
				break;
			}
		}
		return result;
	}

	@Override
	public boolean stop() {
		return stop;
	}

}
