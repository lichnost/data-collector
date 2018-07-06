package org.touchy.datagen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.touchy.Result;
import org.touchy.Writer;
import org.touchy.writer.Hdf5Writer;
import org.touchy.writer.PngWriter;

import com.almasb.fxgl.core.math.FXGLMath;

public class DataGenerator2D {

	private Writer writer;
	private int batchSize;
	private int imageWidth;
	private int imageHeight;

	public DataGenerator2D(Writer writer, int batchSize, int deviceWidth, int deviceHeight, int imageWidth,
			int imageHeight) {
		this.writer = writer;
		this.batchSize = batchSize;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	private void generate(Callable<Result> call) throws Exception {
		List<Integer> hashes = new ArrayList<>();
		while(hashes.size() < batchSize-1) {
			Result result = call.call();
			
			//check hashes
			int hash = resultHash(result);
			if (!hashes.contains(hash)) {
				writer.write(result);
				hashes.add(hash);
			}
		}
	}
	
	private int resultHash(Result result) throws IOException {
		ByteArrayOutputStream barray = new ByteArrayOutputStream();
		ImageIO.write(result.getImage(), "png", barray);
		barray.flush();
		byte [] ib = barray.toByteArray();
		barray.close();
		return Arrays.hashCode(ib) + Arrays.hashCode(result.getOut());
	}

	private Result generateFramedPoint() {
		int deviceWidth = 24;
		int deviceHeight = 24;

		int spotWidth = 3;
		int spotHeight = 3;

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

		int x = 0;
		int y = 0;
		int draw = 0;
		if (FXGLMath.randomBoolean(0.8)) {
			graphics.setColor(Color.BLACK);
			x = FXGLMath.random(deviceX, deviceX + deviceWidth - spotWidth);
			y = FXGLMath.random(deviceY, deviceY + deviceHeight - spotHeight);
			graphics.fillOval(x, y, spotWidth, spotHeight);
			graphics.dispose();
			draw = 1;
		}

		return new Result(image, new Integer[] {deviceX, deviceY, deviceWidth, deviceHeight, draw, x, y});
	}
	
	private int[] inputShapeFramedPoint() {
		return new int[] {30, 30, 3};
	}
	
	private int[] inputShapePoint() {
		return new int[] {30, 30, 3};
	}

	private int[] outputShapeFramedPoint() {
		return new int[] {7};
	}
	
	private int[] outputShapePoint() {
		return new int[] {3};
	}
	
	private String descriptionFramedPoint() {
		StringBuilder description = new StringBuilder();
				description.append("Input data:\n");
				description.append("Image of shape [batch_size, width, height, depth] = [" + batchSize + ", " + imageWidth + ", " + imageHeight + ", " + "3]\n");
				description.append("\n");
				description.append("Output data:\n");
				description.append("[frame_x, frame_y, frame_width, frame_height, point_exists, point_x, point_y]");		
		return description.toString();
	}

	private Result generatePoint() {

		int spotWidth = 3;
		int spotHeight = 3;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, imageWidth, imageHeight);

		int x = 0;
		int y = 0;
		int pointExists = 1;
		if (!FXGLMath.randomBoolean(0.1)) {
			graphics.setColor(Color.BLACK);
			x = FXGLMath.random(0, imageWidth - spotWidth);
			y = FXGLMath.random(0, imageHeight - spotHeight);
			graphics.fillOval(x, y, spotWidth, spotHeight);
			graphics.dispose();
			pointExists = 0;
		}

		return new Result(image,  new Integer[] {pointExists, x, y});
	}
	
	private String descriptionPoint() {
		StringBuilder description = new StringBuilder();
				description.append("Input data:\n");
				description.append("Image of shape [batch_size, width, height, depth] = [" + batchSize + "," + imageWidth + ", " + imageHeight + ", " + "3]\n");
				description.append("\n");
				description.append("Output data:\n");
				description.append("[point_exists, point_x, point_y]");		
		return description.toString();
	}

	public void generate(String type) {
		Callable<Result> call;
		switch (type) {
		case "Point":
			call = new Callable<Result>() {
				@Override
				public Result call() throws Exception {
					return generatePoint();
				}
			};
			writer.writeDescription(descriptionPoint());
			break;
		case "FramedPoint":
			call = new Callable<Result>() {

				@Override
				public Result call() throws Exception {
					return generateFramedPoint();
				}
			};
			writer.writeDescription(descriptionFramedPoint());
			break;
		default:
			call = new Callable<Result>() {

				@Override
				public Result call() throws Exception {
					return null;
				}
			};
			break;
		}
		try {
			generate(call);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Path directory = FileSystems.getDefault().getPath("..").resolve("generated")
				.resolve(String.valueOf(System.currentTimeMillis()));
		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		DataGenerator2D generator = new DataGenerator2D(new Hdf5Writer(directory.toString()), 100, 24, 24, 30, 30);
		generator.generate("FramedPoint");
	}

}
