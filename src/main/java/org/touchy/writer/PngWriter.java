package org.touchy.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.touchy.AbstractWriter;
import org.touchy.Result;

public class PngWriter extends AbstractWriter {

	private Path saveFolder;

	public PngWriter(String directory) {
		super(directory);
		saveFolder = Paths.get(directory);
	}

	@Override
	public void writeInputShape(int[] shape) {
		try {
			Path p =  Paths.get(path).resolve("input-shape.txt");
			Files.write(p, Arrays.toString(shape).getBytes("UTF8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void writeOutputShape(int[] shape) {
		try {
			Path p =  Paths.get(path).resolve("output-shape.txt");
			Files.write(p, Arrays.toString(shape).getBytes("UTF8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(Result result) {
		try {
			String fileName = Arrays.stream(result.getOut()).map(v -> String.valueOf(v))
					.collect(Collectors.joining("_")) + ".png";
			Path file = saveFolder.resolve(fileName);
			ImageIO.write(result.getImage(), "PNG", file.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {

	}

}
