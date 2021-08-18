package varioush.batch.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Writer {
	private Path path;
	private String content;

	public Writer file(String filename) {
		this.path = Paths.get(filename);
		return this;
	}

	public Writer content(String content) {
		this.content = content;
		return this;
	}

	private void write() throws Exception {
		Files.write(this.path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

	}

	public void build() {

		try {
			if (!Files.exists(path)) {
				FileFunctions.createFileAndDirectory(path);
			}
			write();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + path);
		}

	}


}