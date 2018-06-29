package org.touchy.datagen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.almasb.fxgl.core.math.FXGLMath;

public class DataGenerator2D {

	private int batchSize;
	private int deviceWidth;
	private int deviceHeight;
	private int imageWidth;
	private int imageHeight;

	private long sessionTime = System.currentTimeMillis();
	private FileSystem filesystem = FileSystems.getDefault();

	public DataGenerator2D(int batchSize, int deviceWidth, int deviceHeight, int imageWidth, int imageHeight) {
		this.batchSize = batchSize;
		this.deviceWidth = deviceHeight;
		this.deviceHeight = deviceHeight;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	private void generate() {
		Path saveFolder = filesystem.getPath("..").resolve("generated").resolve(String.valueOf(sessionTime));

		if (!Files.exists(saveFolder)) {
			try {
				Files.createDirectories(saveFolder);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		int spotWidth = 3;
		int spotHeight = 3;

		for (int i = 0; i < batchSize; i++) {
			BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();

			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, imageWidth, imageHeight);

			int deviceX = FXGLMath.random(imageWidth - deviceWidth);
			int deviceY = FXGLMath.random(imageHeight - deviceHeight);
			
			graphics.setColor(Color.BLACK);
			graphics.drawRect(deviceX, deviceY, deviceWidth, deviceHeight);

			graphics.setColor(Color.WHITE);
			graphics.drawRect(deviceX + 1, deviceY + 1, deviceWidth - 2, deviceHeight - 2);

			graphics.setColor(Color.BLACK);
			int x = FXGLMath.random(deviceX, deviceX + deviceWidth - spotWidth);
			int y = FXGLMath.random(deviceY, deviceY + deviceHeight - spotHeight);
			graphics.fillOval(x, y, spotWidth, spotHeight);
			graphics.dispose();
			
			try {
				Path file = saveFolder
						.resolve(String.valueOf(System.currentTimeMillis() - sessionTime) + "_0_" + "_" + x + "_" + y
								+ "_" + String.valueOf(deviceWidth) + "_" + String.valueOf(deviceHeight) + ".png");
				ImageIO.write(image, "PNG", file.toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DataGenerator2D generator = new DataGenerator2D(60000, 24, 24, 30, 30);
		generator.generate();
	}

}
