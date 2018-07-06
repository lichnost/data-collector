package org.touchy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractWriter implements Writer {

	protected String path;
	
	public AbstractWriter(String path) {
		this.path = path;
	}
	
	@Override
	public void writeDescription(String description) {
		try {
			Path p =  Paths.get(path).resolve("description.txt");
			Files.write(p, description.getBytes("UTF8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
