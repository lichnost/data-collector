package org.touchy.datacoll;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.TimerAction;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class DataCollector extends GameApplication {

	enum Direction {
		TOP, RIGHT, BOTTOM, LEFT
	}

	enum Corner {
		TOP_LEFT, TOP_RIGHT, TOP_
	}

	private int speed = 60;
	private int deviceWidth;
	private int deviceHeight;
	
	private boolean stopped = true;
	private Entity spot;
	private Entity arrow;

	private Mover mover;

	private double screenshotInterval = 30;
	private long screenshotCount = 0;
	private int showOnlyNdScreen = 5;
	private TimerAction screenshotTimer;
	private Webcam[] webcams;
	private ExecutorService executorService = Executors.newWorkStealingPool();

	private long sessionTime;
	private FileSystem filesystem = FileSystems.getDefault();
	private Path saveFolder;

	@Override
	protected void initSettings(GameSettings settings) {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		deviceWidth = device.getDisplayMode().getWidth();
		deviceHeight = device.getDisplayMode().getHeight();
		settings.setWidth(deviceWidth);
		settings.setHeight(deviceHeight);
		settings.setTitle("Data collector");
		settings.setVersion("0.1");
		settings.setFullScreenAllowed(true);
	}

	@Override
	protected void initInput() {
		Input input = getInput();
		input.addAction(new UserAction("Start/Stop") {
			@Override
			protected void onActionBegin() {
				setStopped(!stopped);
			}
		}, KeyCode.SPACE);
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initGame() {
		spot = Entities.builder().viewFromTextureWithBBox("round.png").buildAndAttach(getGameWorld());
		arrow = Entities.builder().viewFromTextureWithBBox("arrow.png").buildAndAttach(getGameWorld());

		mover = new LinearMover(speed, spot.getWidth(), spot.getHeight(), true);
		mover.init(getWidth(), getHeight());
		updateSpotPosition();
		updateArrawPosition();

		List<Webcam> allCams = new ArrayList<>();
		for (Webcam webcam : Webcam.getWebcams()) {
			webcam = Webcam.getDefault();
			webcam.setCustomViewSizes(new Dimension[] { WebcamResolution.HD720.getSize() });
			webcam.setViewSize(WebcamResolution.HD720.getSize());
			webcam.open();
			allCams.add(webcam);
		}

		webcams = allCams.toArray(new Webcam[0]);

		sessionTime = System.currentTimeMillis();
		screenshotTimer =

				getMasterTimer().runAtInterval(this::sreenshot, Duration.millis(screenshotInterval));
		screenshotTimer.pause();

		saveFolder = filesystem.getPath("screenshots", String.valueOf(sessionTime));
		if (!Files.exists(saveFolder)) {
			try {
				Files.createDirectories(saveFolder);
			} catch (IOException e) {
				e.printStackTrace();
				exit();
			}
		}
	}

	@Override
	protected void onUpdate(double tpf) {
		if (!stopped) {
			mover.update(tpf, getWidth(), getHeight());
			updateSpotPosition();
			updateArrawPosition();
			if (mover.stop()) {
				setStopped(true);
			}
		}
	}

	private void updateSpotPosition() {
		spot.setX(mover.getSpotX());
		spot.setY(mover.getSpotY());
	}

	private void updateArrawPosition() {
		arrow.setX(mover.getArrowX());
		arrow.setY(mover.getArrowY());
		arrow.setRotation(mover.getArrowAngle());
	}

	private void sreenshot() {
		screenshotCount++;
		for (int i = 0; i < webcams.length; i++) {
			final int webcamIndex = i;
			final BufferedImage image = webcams[i].getImage();
			Point2D center = spot.getCenter();
			executorService.execute(() -> saveScreenshot(webcamIndex, image, (int) center.getX(), (int) center.getY()));
			showScreenshot(i, image);
		}
	}

	private void showScreenshot(int i, BufferedImage image) {
		if (screenshotCount % showOnlyNdScreen == 0) {
			//TODO
		}
	}

	private void saveScreenshot(int webcamIndex, BufferedImage image, int x, int y) {
		try {
			
			Path file = saveFolder.resolve(String.valueOf(System.currentTimeMillis() - sessionTime) + "_" + webcamIndex
					+ "_" + x + "_" + y + "_"  + String.valueOf(deviceWidth) + "_" + String.valueOf(deviceHeight) + ".png");
			ImageIO.write(image, "PNG", file.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setStopped(boolean stop) {
		stopped = stop;
		if (stopped) {
			screenshotTimer.pause();
		} else {
			screenshotTimer.resume();
		}
	}

}
