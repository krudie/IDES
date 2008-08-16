package ides.api.plugin.io;

import ides.api.plugin.model.DESModel;

import java.io.File;
import java.io.IOException;

public interface IOSubsytem
{
	/**
	 * Extension for workspace files.
	 */
	public static final String WORKSPACE_FILE_EXT = "xws";

	/**
	 * Extension for model files.
	 */
	public static final String MODEL_FILE_EXT = "xmd";

	public DESModel load(File file) throws IOException;

	public void save(DESModel model, File file) throws IOException;

	public DESModel importFile(File src, String description) throws IOException;

	public void export(DESModel model, File dst, String description)
			throws IOException;

}
