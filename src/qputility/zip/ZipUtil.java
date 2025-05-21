package qputility.zip;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

/**
 * @author Chris.Ditcher
 * A faster, better, less error-prone alternative to QPZip.
 */
public class ZipUtil {
	
	/**
	 * Creates a zip file with default compression of 'source' parameter
	 * and writes to the 'target' location.
	 * @param source can be file or directory (this method supports recursive zipping)
	 * @param target the zip file to write to
	 * @throws Exception
	 */
	public static void zip(File source, File target) throws Exception{
		ZipFile zipFile = new ZipFile(target);
		if(source.isDirectory())
		{
			zipFile.addFolder(source, new ZipParameters());
		}
		else
		{
			zipFile.addFile(source, new ZipParameters());
		}
	}

}
