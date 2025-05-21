package qputility.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import qputility.date.QPDate;
import qputility.enums.QPDisplayDebugInfo;
import qputility.exceptions.QPConfig_Exception;
import qputility.filter.BackupFileFilter;
import qputility.zip.ZipUtil;

public class QPFileIO
{
	public static void copyFile(String source, String dest, long lastModifed, boolean overwrite) throws IOException
	{
		copyFile(source, dest, overwrite);
		new File(dest).setLastModified(lastModifed);
	}

	public static void copyFile(String source, String dest, boolean overwrite) throws IOException
	{
		File destFile = new File(dest);
		if (destFile.exists())
		{
			if (overwrite)
			{
				destFile.delete();
				copyFile(source, dest);
			}
			else
			{
				throw new IOException("Target file location exists - " + dest);
			}
		}
		else
		{
			copyFile(source, dest);
		}
	}

	public static void copyFile(String source, String dest, long lastModifed) throws IOException
	{
		copyFile(source, dest);
		new File(dest).setLastModified(lastModifed);
	}
	
	public static void copyFile(File source, File dest) throws IOException
	{
		copyFile(new FileInputStream(source), new FileOutputStream(dest));
	}

	public static void copyFile(String source, String dest) throws IOException
	{
		dest = dest.replace("\\", "/");
		if (dest.contains("/"))
		{
			String dirToMake = "";
			String[] splits = dest.split("/");
			for (int s = 0; s < splits.length - 1; s++)
			{
				dirToMake += splits[s] + "/";
			}
			File targetDir = new File(dirToMake);
			if (!targetDir.exists())
			{
				targetDir.mkdirs();
			}
		}
		copyFile(new FileInputStream(source), new FileOutputStream(dest));
	}

	public static void copyFile(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

	public static boolean deleteDirectory(String path)
	{
		return deleteDirectory(new File(path));
	}

	public static boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static void copyDirectory(String sourceLocation, String targetLocation) throws IOException
	{
		copyDirectory(new File(sourceLocation), new File(targetLocation), false);
	}

	public static void copyDirectory(String sourceLocation, String targetLocation, boolean overwrite) throws IOException
	{
		copyDirectory(new File(sourceLocation), new File(targetLocation), overwrite);
	}

	public static void copyDirectory(String sourceLocation, String targetLocation, String[] fileFilter, boolean overwrite) throws IOException
	{
		copyDirectory(new File(sourceLocation), new File(targetLocation), fileFilter, overwrite);
	}

	public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException
	{
		copyDirectory(sourceLocation, targetLocation, null, false);
	}

	public static void copyDirectory(File sourceLocation, File targetLocation, boolean overwrite) throws IOException
	{
		copyDirectory(sourceLocation, targetLocation, null, overwrite);
	}

	public static void copyDirectory(File sourceLocation, File targetLocation, String[] fileFilter, boolean overwrite) throws IOException
	{
		if (sourceLocation.isDirectory())
		{
			if (!targetLocation.exists())
			{
				targetLocation.mkdirs();
			}
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++)
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), fileFilter, overwrite);
			}
		}
		else if (!isFileInFileFilter(sourceLocation, fileFilter))
		{
			if (targetLocation.exists())
			{
				if (overwrite)
				{
					targetLocation.delete();
					copyFile(new FileInputStream(sourceLocation), new FileOutputStream(targetLocation));
				}
				else
				{
					throw new IOException("Target file location exists - " + targetLocation.getAbsolutePath());
				}
			}
			else
			{
				copyFile(new FileInputStream(sourceLocation), new FileOutputStream(targetLocation));
			}
		}
	}

	public static boolean isFileInFileFilter(String filePath, String[] fileFilter)
	{
		return isFileInFileFilter(new File(filePath), fileFilter, false, QPDisplayDebugInfo.doNotDisplayDebugInfo);
	}

	public static boolean isFileInFileFilter(String filePath, String[] fileFilter, boolean caseInsensitive)
	{
		return isFileInFileFilter(new File(filePath), fileFilter, caseInsensitive, QPDisplayDebugInfo.doNotDisplayDebugInfo);
	}

	public static boolean isFileInFileFilter(String filePath, String[] fileFilter, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo)
	{
		return isFileInFileFilter(new File(filePath), fileFilter, caseInsensitive, displayDebugInfo);
	}

	public static boolean isFileInFileFilter(File file, String[] fileFilter)
	{
		return isFileInFileFilter(file, fileFilter, false, QPDisplayDebugInfo.doNotDisplayDebugInfo);
	}

	public static boolean isFileInFileFilter(File file, String[] fileFilter, boolean caseInsensitive)
	{
		return isFileInFileFilter(file, fileFilter, caseInsensitive, QPDisplayDebugInfo.doNotDisplayDebugInfo);
	}

	public static boolean isFileInFileFilter(File file, String[] fileFilter, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo)
	{
		if (fileFilter == null)
		{
			return false;
		}
		else
		{
			boolean retVal = false;

			String fileName = file.getName();
			String filePath = file.getPath();
			if (caseInsensitive)
			{
				fileName = fileName.toLowerCase();
			}
			for (int i = 0; i < fileFilter.length; i++)
			{
				String currentFilter = fileFilter[i];
				if (caseInsensitive)
				{
					currentFilter = currentFilter.toLowerCase();
				}

				if (fileName.equals(currentFilter))
				{
					if (displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
					{
						System.out.println(filePath + " was caught in the filter by direct match");
					}
					return true;
				}

				if (currentFilter.contains("*") | currentFilter.contains("?"))
				{
					String regexFilter = currentFilter.replace(".", "\\.").replace("*", ".*").replace("?", ".").replace(" ", "\\s");
					Pattern regexPattern;
					if (caseInsensitive)
					{
						regexPattern = Pattern.compile(regexFilter, Pattern.CASE_INSENSITIVE);
					}
					else
					{
						regexPattern = Pattern.compile(regexFilter);
					}

					Matcher regexMatcher = regexPattern.matcher(filePath);

					if (regexMatcher.find())
					{
						if (displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
						{
							System.out.println(filePath + " was caught in the filter by regex");
						}
						return true;
					}
				}
			}

			return retVal;
		}
	}

	public static String getFileExtension(File file)
	{
		String fileLocation = file.getAbsolutePath();
		return fileLocation.substring(fileLocation.lastIndexOf("."));
	}

	public static String getFileExtension(String fileLocation)
	{
		return getFileExtension(new File(fileLocation));
	}

	public static String getFileName(File file)
	{
		String fileLocation = file.getAbsolutePath();
		return fileLocation.substring(fileLocation.lastIndexOf(System.getProperty("file.separator")) + 1, fileLocation.lastIndexOf("."));
	}

	public static String getFileName(String fileLocation)
	{
		return getFileName(new File(fileLocation));
	}

	/**
	 * Backs up a directory to the same root location appended with a date stamp
	 * @param sourceDirectoryPath
	 * @return
	 * @throws IOException
	 */
	public static String backupDirectory(String sourceDirectoryPath) throws IOException
	{
		String targetDir = QPDirectory.removeTrailingSlash(sourceDirectoryPath) + " " + QPDate.currentDateAndTime().replace(":", ".");
		QPFileIO.copyDirectory(sourceDirectoryPath, targetDir);
		return targetDir;
	}
	
	/**
	 * Creates a rolling backup copy of a file or directory in zipped format
	 * with a numeric time-stamp. 
	 * @param source the file or directory you wish to backup
	 * @param destinationDirectory the location (has to be a directory) where you want the backups to go
	 * @param maxBackups the maximum number of backup copies you wish to keep. older copies are removed.
	 * @throws Exception
	 */
	public static void rollingBackup(String source, String destinationDirectory, int maxBackups) throws Exception {
		File src = new File(source);
		File dest = new File(destinationDirectory);
		// a little io checking...
		if(!src.exists())
		{
			throw new IOException(source + " does not exist or is not accessible.");
		}
		if(dest.exists() && !dest.isDirectory())
		{
			throw new IOException(destinationDirectory + " needs to be a valid directory.");
		}
		else
		{
			// create the directory
			dest.mkdir();
		}
		// backup with zip
		ZipUtil.zip(src, new File(QPDirectory.validatePath(destinationDirectory) + src.getName() + "_" + new Date().getTime() + ".zip"));
		// remove old file(s)
		BackupFileFilter filter = new BackupFileFilter(src.getName() + "_\\d+\\.zip");
		filter.sortAndRemoveBackups(maxBackups, dest.listFiles(filter));
	}

	/**
	 * @param file
	 *            A Path to the directory
	 * @param fileFilter
	 *            A filter containing the files, or directories to be deleted
	 */
	public static void removeFilesInFileFilter(String file, String[] fileFilter)
	{
		removeFilesInFileFilter(new File(file), fileFilter, false, true);
	}

	/**
	 * @param file
	 *            A Path to the directory
	 * @param fileFilter
	 *            A filter containing the files, or directories to be deleted
	 */
	public static void removeFilesInFileFilter(File file, String[] fileFilter)
	{
		removeFilesInFileFilter(file, fileFilter, false, true);
	}

	/**
	 * @param file
	 *            A Path to the directory
	 * @param fileFilter
	 *            A filter containing the files, or directories to be deleted
	 * @param caseInsensitive
	 *            if the filter is case sensitive or not
	 */
	public static void removeFilesInFileFilter(String file, String[] fileFilter, boolean caseInsensitive)
	{
		removeFilesInFileFilter(new File(file), fileFilter, caseInsensitive, true);
	}

	/**
	 * @param file
	 *            A Path to the directory
	 * @param fileFilter
	 *            A filter containing the files, or directories to be deleted
	 * @param caseInsensitive
	 *            if the filter is case sensitive or not
	 */
	public static void removeFilesInFileFilter(File file, String[] fileFilter, boolean caseInsensitive, boolean verbose)
	{
		QPDisplayDebugInfo info = (verbose) ? QPDisplayDebugInfo.displayDebugInfo : QPDisplayDebugInfo.doNotDisplayDebugInfo;
		
		if (QPFileIO.isFileInFileFilter(file, fileFilter, caseInsensitive, info))
		{
			if (file.isDirectory())
			{
				QPFileIO.deleteDirectory(file);
				return;
			}
			else
			{
				file.delete();
				return;
			}
		}

		if (file.isDirectory())
		{
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++)
			{
				removeFilesInFileFilter(children[i], fileFilter, caseInsensitive, verbose);
			}
		}
	}

	public static ArrayList<String> getFilesInFileFilter(String dir, String[] fileFilter)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(new File(dir), fileFilter, false, false, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(File dir, String[] fileFilter)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(dir, fileFilter, false, false, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(String dir, String[] fileFilter, boolean recursive)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(new File(dir), fileFilter, recursive, false, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(File dir, String[] fileFilter, boolean recursive)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(dir, fileFilter, recursive, false, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(String dir, String[] fileFilter, boolean recursive, boolean caseInsensitive)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(new File(dir), fileFilter, recursive, caseInsensitive, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(File dir, String[] fileFilter, boolean recursive, boolean caseInsensitive)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(dir, fileFilter, recursive, caseInsensitive, QPDisplayDebugInfo.doNotDisplayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(String dir, String[] fileFilter, boolean recursive, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(new File(dir), fileFilter, recursive, caseInsensitive, displayDebugInfo, new ArrayList<String>());
	}
	
	public static ArrayList<String> getFilesInFileFilter(File dir, String[] fileFilter, boolean recursive, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo)
	{
		QPFileIO fio = new QPFileIO();
		return fio.getFilesInFileFilter(dir, fileFilter, recursive, caseInsensitive, displayDebugInfo, new ArrayList<String>());
	}
	
	private ArrayList<String> getFilesInFileFilter(File dir, String[] fileFilter, boolean recursive, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo, ArrayList<String> caughtFiles)
	{
		File[] files = dir.listFiles();
		
		for(int i = 0; i < files.length; i ++)
		{
			File file = files[i];
			String filePath = file.getPath();
			if(file.isFile())
			{
				if(QPFileIO.isFileInFileFilter(filePath, fileFilter, caseInsensitive, displayDebugInfo))
				{
					caughtFiles.add(filePath);
				}
			}
			if(file.isDirectory() && recursive)
			{
				caughtFiles = getFilesInFileFilter(file, fileFilter, recursive, caseInsensitive, displayDebugInfo, caughtFiles);
			}
				
		}
		return caughtFiles;
	}
	
	public static void deleteFilesNOTInFileFilter(File dir, String[] fileFilter, boolean recursive, boolean caseInsensitive, QPDisplayDebugInfo displayDebugInfo)
	{
		File[] files = dir.listFiles();
		
		for(int i = 0; i < files.length; i ++)
		{
			File file = files[i];
			if(file.isFile())
			{
				if(!QPFileIO.isFileInFileFilter(file.getPath(), fileFilter, caseInsensitive, displayDebugInfo))
				{
					System.out.println("Deleting File: " + file.getPath());
					file.delete();
				}
			}	
			if(file.isDirectory() && recursive)
			{
				deleteFilesNOTInFileFilter(file, fileFilter, recursive, caseInsensitive, displayDebugInfo);
			}
		}
		
		if(dir.listFiles().length == 0)
		{
			//QPFileIO.deleteDirectory(dir);
			System.out.println("Deleting Dir: " + dir.getPath());
			dir.delete();
		}
	}
	
	public static void main(String[] args) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		String testFile = "test files";
		String dest = "test files";
		try {
			rollingBackup(testFile, dest, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
