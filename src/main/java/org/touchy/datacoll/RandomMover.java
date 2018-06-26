package org.touchy.datacoll;

import org.touchy.datacoll.DataCollector.Corner;
import org.touchy.datacoll.DataCollector.Direction;

import com.almasb.fxgl.core.math.FXGLMath;

public class RandomMover implements Mover {

	protected int speed;

	protected double spotWidth;
	protected double spotHeight;
	protected double spotRadius;

	protected double spotX;
	protected double spotY;

	protected double arrowX;
	protected double arrowY;

	protected double directionAngle;
	protected double futureDirectionAngle;

	public RandomMover(int speed, double spotWidth, double spotHeight) {
		this.speed = speed;
		this.spotWidth = spotWidth;
		this.spotHeight = spotHeight;
		spotRadius = Math.sqrt(Math.pow(spotWidth, 2) + Math.pow(spotHeight, 2));
	}

	protected void initInternal(int gameWidth, int gameHeight) {
		spotX = gameWidth / 2;
		spotY = gameHeight / 2;

		futureDirectionAngle = changeDirection(gameWidth, gameHeight, null, null);
	}

	@Override
	public void init(int gameWidth, int gameHeight) {
		initInternal(gameWidth, gameHeight);
		arrowX = spotX + spotRadius;
		arrowY = spotY + spotRadius;
		directionAngle = futureDirectionAngle;
		updateArrowPosition();
	}

	protected double changeDirection(int gameWidth, int gameHeight, Direction nearSide, Corner nearCorner) {
		double result = FXGLMath.getRandom().nextDouble();
		double bottom = 0;
		double top = FXGLMath.PI2;

		if (nearSide != null) {
			switch (nearSide) {
			case BOTTOM:
				bottom = FXGLMath.PI;
				top = FXGLMath.PI2;
				break;
			case RIGHT:
				bottom = FXGLMath.PI / 2;
				top = (3 * FXGLMath.PI) / 2;
				break;
			case TOP:
				bottom = 0;
				top = FXGLMath.PI;
				break;
			case LEFT:
				if (FXGLMath.randomBoolean()) {
					bottom = 0;
					top = FXGLMath.PI / 2;
				} else {
					bottom = (3 * FXGLMath.PI) / 2;
					top = FXGLMath.PI2;
				}
				break;
			}
		}
		if (nearCorner != null) {
			// TODO доделать если застрял в углу
		}

		double cornersCorrection = 0;
		if (FXGLMath.randomBoolean(0.8)) {
			cornersCorrection = (top - bottom) / 4;
		}
		bottom = bottom + cornersCorrection;
		top = top - cornersCorrection;

		return bottom + result * (result / (top - bottom));
	}

	private void updateFutureDirection(double deltaX, double deltaY, int gameWidth, int gameHeight) {
		if (futureDirectionAngle != directionAngle) {
			return;
		}

		double futureDeltaX = deltaX * 50;
		double futureDeltaY = deltaY * 50;

		double x = spotX + futureDeltaX;
		double y = spotY + futureDeltaY;

		double direction = futureDirectionAngle;
		Direction nearCorner = null;
		if (isOnLeftSide(x, y, spotWidth, spotHeight, gameWidth, gameHeight)) {
			nearCorner = Direction.LEFT;
		} else if (isOnRightSide(x, y, spotWidth, spotHeight, gameWidth, gameHeight)) {
			nearCorner = Direction.RIGHT;
		} else if (isOnTopSide(x, y, spotWidth, spotHeight, gameWidth, gameHeight)) {
			nearCorner = Direction.TOP;
		} else if (isOnBottomSide(x, y, spotWidth, spotHeight, gameWidth, gameHeight)) {
			nearCorner = Direction.BOTTOM;
		}
		direction = changeDirection(gameWidth, gameHeight, nearCorner, null);

		if (direction != futureDirectionAngle) {
			futureDirectionAngle = direction;
		}
	}

	protected boolean isOnTopSide(double x, double y, double elementWidth, double elementHeight, int gameWidth,
			int gameHeight) {
		return y <= 0;
	}

	protected boolean isOnBottomSide(double x, double y, double elementWidth, double elementHeight, int gameWidth,
			int gameHeight) {
		return y + elementHeight >= gameHeight;
	}

	protected boolean isOnLeftSide(double x, double y, double elementWidth, double elementHeight, int gameWidth,
			int gameHeight) {
		return x <= 0;
	}

	protected boolean isOnRightSide(double x, double y, double elementWidth, double elementHeight, int gameWidth,
			int gameHeight) {
		return x + elementWidth >= gameWidth;
	}

	@Override
	public void update(double tpf, int gameWidth, int gameHeight) {
		double deltaX = FXGLMath.cos(directionAngle);
		double deltaY = FXGLMath.sin(directionAngle);
		updateFutureDirection(deltaX, deltaY, gameWidth, gameHeight);
		updateDirection(gameWidth, gameHeight);
		updateSpotPosition(tpf, deltaX, deltaY);

		updateArrowPosition();
	}

	private void updateArrowPosition() {
		double spotCenterX = spotX + (spotWidth / 2);
		double spotCenterY = spotY + (spotHeight / 2);
		double futureDeltaX = FXGLMath.cos(futureDirectionAngle);
		double futureDeltaY = FXGLMath.sin(futureDirectionAngle);
		arrowX = spotCenterX + futureDeltaX * spotWidth - spotWidth / 2;
		arrowY = spotCenterY + futureDeltaY * spotHeight - spotHeight / 2;
	}

	private void updateSpotPosition(double tpf, double deltaX, double deltaY) {
		spotX = spotX + (deltaX * speed * tpf);
		spotY = spotY + (deltaY * speed * tpf);
	}

	private void updateDirection(int gameWidth, int gameHeight) {
		if (spotX < 0 || (spotX + spotWidth) > gameWidth || spotY < 0 || (spotY + spotHeight) > gameHeight) {
			directionAngle = futureDirectionAngle;
		}
	}

	@Override
	public double getSpotX() {
		return spotX;
	}

	@Override
	public double getSpotY() {
		return spotY;
	}

	@Override
	public double getArrowX() {
		return arrowX;
	}

	@Override
	public double getArrowY() {
		return arrowY;
	}

	@Override
	public double getArrowAngle() {
		return FXGLMath.toDegrees(futureDirectionAngle);
	}

	@Override
	public boolean stop() {
		return false;
	}

}
