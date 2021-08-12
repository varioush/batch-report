package varioush.batch.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import varioush.batch.constant.Constants;

public class WriterUtils {

	private static final String TEMP_DIR = Constants.CHAR_DOT + File.separator + Constants.DIR_TEMP;

	public void write(String filename, boolean isAppend, String content) throws Exception {
		FileWriter fw = new FileWriter(filename, isAppend);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.newLine();
		bw.close();

	}

	public void writeNew(String filename, String content) {

		File file = new File(TEMP_DIR);

		if (!file.isDirectory()) {
			file.mkdirs();
		}

		String path = path(filename);

		Path confFile = Paths.get(path);

		try {
			if (Files.notExists(confFile)) {
				try {
					Files.createFile(confFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			write(path, false, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	public void write(String filename, String content) {
		try {
			String path = path(filename);
			write(path, true, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	public File getFile(String filename) {

		return new File(path(filename));
	}

	private String path(String filename) {

		return String.join(File.separator, Constants.DIR_TEMP, filename);
	}

}
