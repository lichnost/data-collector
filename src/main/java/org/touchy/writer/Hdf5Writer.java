package org.touchy.writer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

import org.touchy.AbstractWriter;
import org.touchy.Result;

import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

public class Hdf5Writer extends AbstractWriter {

	private IHDF5Writer writer;

	public Hdf5Writer(String path) {
		super(path);
		writer = HDF5Factory.open(Paths.get(path, "data.hdf5").toFile());
	}

	@Override
	public void writeDescription(String description) {
		super.writeDescription(description);
		writer.writeString("description", description);
	}

	@Override
	public void writeInputShape(int[] shape) {
		writer.writeIntArray("input-shape", shape);
		writer.int32().createMDArray("input", shape);
	}

	@Override
	public void writeOutputShape(int[] shape) {
		writer.writeIntArray("output-shape", shape);
		writer.int32().createMDArray("output", shape);
	}

	@Override
	public void write(Result result) {
		//TODO Didn't work
		writer.int32().writeMDArrayBlock("input", convertTo2DWithoutUsingGetRGB(result.getImage()), new long[] {0,0,0});
		MDIntArray out = new MDIntArray(new int[] {2});
		writer.int32().writeMDArrayBlock("output", out, new long[] {0});
	}

	private static MDIntArray convertTo2DWithoutUsingGetRGB(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		MDIntArray result = new MDIntArray(new int[] { h, w, 3 });
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int px = image.getRGB(x, y);
				// int alpha = (px >> 24) & 0xFF;
				int red = (px >> 16) & 0xFF;
				int green = (px >> 8) & 0xFF;
				int blue = px & 0xFF;
				result.set(red, x, y, 0);
				result.set(green, x, y, 1);
				result.set(blue, x, y, 2);
			}
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
