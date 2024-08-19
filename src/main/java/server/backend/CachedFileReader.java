/* Flood is a network inspection tool
 * Copyright (C) 2024 Yegore Vlussove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package server.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashMap;

public class CachedFileReader {
	private final CachedFileReader instance = new CachedFileReader();
	private final HashMap<String, FileInfo> files = new HashMap<>();

	private CachedFileReader() {}

	public CachedFileReader getCachedFileReader() {
		return instance;
	}

	public byte[] read(String fileName) throws IOException {
		assert fileName != null;

		if (files.containsKey(fileName)) {
			if (files.get(fileName).getFileTime().equals(Files.getLastModifiedTime(Path.of(fileName)))) {
				return files.get(fileName).getContent().clone();
			}
			FileInfo fi = getFileInfo(fileName);
			files.replace(fileName, fi);
			return fi.getContent().clone();
		}

		FileInfo fi = getFileInfo(fileName);
		files.put(fileName, fi);
		return fi.getContent().clone();
	}

	public byte[] read(Path path) throws IOException {
		return read(path.toString());
	}

	public byte[] read(File file) throws IOException {
		return read(file.getAbsolutePath());
	}

	private FileInfo getFileInfo(String fileName) throws IOException {
		try(FileInputStream fis = new FileInputStream(fileName)) {
			byte[] content = fis.readAllBytes();
			FileTime ft = Files.getLastModifiedTime(Path.of(fileName));
			return new FileInfo(content, ft);
		}
	}

	private static class FileInfo {
		private FileTime fileTime;
		private byte[] content;

		public FileInfo(byte[] content, FileTime fileTime) {
			assert content != null && fileTime != null;
			this.content = content;
			this.fileTime = fileTime;
		}

		public FileTime getFileTime() {
			return fileTime;
		}

		public byte[] getContent() {
			return content;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof FileInfo) {
				return fileTime.equals(((FileInfo) o).fileTime) && Arrays.equals(content, ((FileInfo) o).content);
			}

			return false;
		}
	}
}
