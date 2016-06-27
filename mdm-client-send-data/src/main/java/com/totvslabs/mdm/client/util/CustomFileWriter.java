package com.totvslabs.mdm.client.util;

import java.io.FileWriter;
import java.io.IOException;

public class CustomFileWriter extends FileWriter {
	private boolean isClosed = true;

	public CustomFileWriter(String fileName, boolean append) throws IOException {
		super(fileName, append);
		this.isClosed = false;
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.isClosed = true;
	}

	public boolean getIsClosed() {
		return this.isClosed;
	}
}
