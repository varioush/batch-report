package varioush.batch.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varioush.batch.constant.Constants;
import varioush.batch.constant.Constants.FOLDER;

public class Functions {

	private static final Logger logger = LoggerFactory.getLogger(Functions.class);

	public static void createFileAndDirectory(Path path) {

		if (Files.notExists(path)) {
			try {
				try {
					createDirectory(path.getParent());
				} catch (Exception ex) {

				}
				Files.createFile(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void createDirectory(Path path) {
		try {
			Files.createDirectories(path);
		} catch (Exception ex) {

		}
	}

	public static void deleteFilesOlderThanNdays(long daysBack) {

		for (FOLDER folder : Constants.FOLDER.values()) {
			Path path = getPath(folder.name());
			try {
				boolean delSource = false;
				long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);

				if (Files.getLastModifiedTime(path).toMillis() < purgeTime)
					delSource = true;
				if (Files.isDirectory(path)) {
					try (Stream<Path> files = Files.list(path)) {
						for (Iterator<Path> iterator = files.iterator(); iterator.hasNext();) {
							Path filePath = iterator.next();
							if (Files.getLastModifiedTime(filePath).toMillis() < purgeTime) {
								Files.delete(filePath);
							}
						}
					}

				}
				if (delSource && isEmpty(path)) {
					Files.delete(path);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Path getPath(String name) {

		Path currentDir = Paths.get(Constants.CHAR_BLANK);
		Path one = currentDir.resolve(Constants.DIR_TEMP);
		return one.resolve(name);

	}

	public static boolean isEmpty(Path path) throws Exception {
		if (Files.isDirectory(path)) {
			try (Stream<Path> entries = Files.list(path)) {
				return !entries.findFirst().isPresent();
			}
		}

		return false;
	}

	public static void builtDirectoryStructure() {
		for (FOLDER folder : Constants.FOLDER.values()) {
			Path path = getPath(folder.name());

			if (Files.exists(path) && Files.isDirectory(path)) {
				logger.info("Folder exists :{}", path.toAbsolutePath().toString());
			} else {
				try {
					Path temp = Files.createDirectories(path);
					logger.info("Path :{}, isExists:{}", temp.toAbsolutePath().toString(), Files.exists(temp));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

}
