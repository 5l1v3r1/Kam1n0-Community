package ca.mcgill.sis.dmas.kam1n0.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.mcgill.sis.dmas.io.file.DmasFileOperations;

public class FileInfo {

	private static Logger logger = LoggerFactory.getLogger(FileInfo.class);
	private transient File metaFile;

	public String file;
	public String task;
	public long lastModified;
	public boolean preparing;
	public long size = -1;
	public long appId = -1;
	public String appType = null;

	public FileInfo() {
	}

	public static FileInfo readFileInfo(File file) {
		if (file.getName().endsWith(".meta"))
			return null;
		File infoFile = new File(file.getAbsolutePath() + ".meta");
		if (!infoFile.exists() && !file.exists())
			return null;
		else if (!infoFile.exists() && file.exists()) {
			FileInfo info = new FileInfo();
			info.file = file.getName();
			info.lastModified = file.lastModified();
			info.metaFile = infoFile;
			try {
				info.size = Files.size(file.toPath());
				new ObjectMapper().writeValue(infoFile, info);
				return info;
			} catch (IOException e) {
				logger.error("Failed initialize meta file for " + file.getAbsolutePath(), e);
				return null;
			}
		} else
			try {
				FileInfo info = new ObjectMapper().readValue(infoFile, FileInfo.class);
				info.metaFile = infoFile;
				info.lastModified = file.lastModified();
				info.size = Files.size(file.toPath());
				return info;
			} catch (Exception e) {
				logger.error("Failed to parse meta file for " + file.getAbsolutePath(), e);
				return null;
			}
	}

	public void save() {
		try {
			new ObjectMapper().writeValue(metaFile, this);
		} catch (IOException e) {
			logger.error("Failed save metadata to " + metaFile.getAbsolutePath(), e);
		}
	}

	public String calculateRenderURL() {
		return appType + "/" + appId + "/" + task + "?fileName=" + file;
	}
}