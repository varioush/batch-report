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

		File file = new File(".");

		String path = file.getAbsolutePath() + File.separator + Constants.DIR_TEMP;

		file = new File(path);

		if (!file.isDirectory()) {
			file.mkdir();
		}

		path = path + File.separator + filename;

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
			
			write(getPath(filename), true, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	public String getPath(String filename) {

		File file = new File(".");

		return  file.getAbsolutePath() + File.separator + Constants.DIR_TEMP + File.separator
				+ filename;

	}

	public File getFile(String filename) {
		// TODO Auto-generated method stub
		return new File(getPath(filename));
	}

}
