package org.touchy;

import java.awt.image.BufferedImage;
import java.util.List;

public class Result {

	private BufferedImage image;
	private Integer[] out;
	
	public Result(BufferedImage image, Integer[] out) {
		this.image = image;
		this.out = out;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Integer[] getOut() {
		return out;
	}
	
}
