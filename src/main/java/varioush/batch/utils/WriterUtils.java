package varioush.batch.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import varioush.batch.constant.Constants;

public class WriterUtils {

	public void write(String filename, boolean isAppend, String content) throws Exception {
		FileWriter fw = new FileWriter(filename, isAppend);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.newLine();
		bw.close();

	}

	public void writeNew(String filename, String content) {

		String path = getTempPath();

		File file = new File(path);

		if (!file.isDirectory()) {
			file.mkdir();
		}

		path = getPath(filename);

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

	private String getTempPath() {

		File file = new File(Constants.CHAR_DOT);

		return String.join(File.separator, file.getAbsolutePath(), Constants.DIR_TEMP);

	}

	public void write(String filename, String content) {
		try {

			write(getPath(filename), true, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	public String getPath(String filename) {
		
		File file = new File(Constants.CHAR_DOT);

		return String.join(File.separator, file.getAbsolutePath(), Constants.DIR_TEMP,filename);

	}

	public File getFile(String filename) {

		return new File(getPath(filename));
	}

}
