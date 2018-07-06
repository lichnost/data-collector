package org.touchy;

import java.io.Closeable;

public interface Writer extends Closeable {

	void writeDescription(String description);
	
	void writeInputShape(int[] shape);
	
	void writeOutputShape(int[] shape);
	
	void write(Result result);
	
}
