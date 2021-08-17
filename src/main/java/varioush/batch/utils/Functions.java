package varioush.batch.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import varioush.batch.constant.Constants;
import varioush.batch.constant.Constants.FOLDER;

public class Functions {

	private static final String TEMP_DIR = Constants.CHAR_DOT + File.separator + Constants.DIR_TEMP;

	public static void write(String filename, boolean isAppend, String content) throws Exception {
		FileWriter fw = new FileWriter(filename, isAppend);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.newLine();
		bw.close();

	}

	public static void createAndWrite(String filename, String content) {



		

		Path confFile = Paths.get(filename);

		try {
			if (Files.notExists(confFile)) {
				try {
					
					Files.createDirectory(confFile.getParent());
					Files.createFile(confFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			write(filename, false, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	public static void write(String filename, String content) {
		try {
			String path = filename;
			write(path, true, content);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to write file, filename:" + filename);
		}

	}

	
	public static String path(String filename) {

		return String.join(File.separator, Constants.DIR_TEMP, filename);
	}
	
	public static String path(FOLDER path) {

		return String.join(File.separator, Constants.DIR_TEMP, path.name());
	}

	public static void deleteFilesOlderThanNdays(int daysBack) {

		File directory = new File(TEMP_DIR);
		if (directory.exists()) {

			File[] listFiles = directory.listFiles();
			long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
			for (File listFile : listFiles) {
				if (listFile.lastModified() < purgeTime) {
					System.out.println(listFile.toPath().toAbsolutePath());
					if (!listFile.delete()) {
						System.err.println("Unable to delete file: " + listFile);
					}
				}
			}
		}
	}

	public static String getPath(FOLDER folder) {
		
		return path(folder.name())+File.separator;
	}
}
