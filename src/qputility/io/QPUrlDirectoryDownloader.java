package qputility.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QPUrlDirectoryDownloader
{
	public static File downloadInto(URL url, File folder) throws Exception
	{
		return download(url, getFile(url, folder));
	}
	
	public static File download(URL url, File file) throws Exception
	{
		try
		{
			InputStream is = url.openStream();
			copyFile(is, file);
		}
		catch (Exception ex)
		{
		}
		return file;
	}
	
	public static File downloadDir(URL url, File folder) throws Exception
	{
		folder.mkdirs();
		File file = null;
		if (url.getPath().endsWith("/"))
		{
			if (folder.isDirectory())
			{
				file = download(url, getFile(url, folder));
				if (file != null)
				{
					return explode(url, file);
				}
			}
			else
			{
				new RuntimeException("Not a folder");
			}
		}
		else
		{
			new RuntimeException("URL is not a folder (it should ends with /)");
		}
		return file;
	}
	
	private static File getFile(URL url, File folder)
	{
		String fileName = "[folder-files]";
		if (!url.getFile().endsWith("/"))
		{
			String filePath[] = url.getFile().split("/");
			fileName = filePath[filePath.length - 1];
			fileName = fileName.replaceAll("%20", " ");
		}
		File file = new File(folder, fileName);
		return file;
	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException
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

	private static void copyFile(InputStream in, File out) throws IOException
	{
		out.getParentFile().mkdirs();
		copyFile(in, new FileOutputStream(out));
	}

	private static StringBuffer readFileAsString(File file) throws Exception
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData;
	}

	private static File explode(URL url, File file) throws Exception
	{
		if (url.getPath().endsWith("/"))
		{
			StringBuffer content = readFileAsString(file);
			String match = "<title>" + url.getHost() + " - " + url.getFile()
					+ "</title>";
			// Delete the file
			file.delete();

			if (content.toString().contains(match))
			{
				Pattern p = Pattern.compile("HREF=\"([^\"]*)");
				Matcher m = p.matcher(content);
				int files = 0;
				while (m.find())
				{
					String href = m.group(1);
					URL newUrl = new URL(url.getProtocol(), url.getHost(), href);
					System.out.println(href);

					if (newUrl.getFile().startsWith(url.getFile()))
					{
						if (newUrl.getPath().endsWith("/"))
						{
							href = href.substring(url.getFile().length());
							File folder = new File(file.getParentFile(), href
									.replaceAll("%20", " "));
							folder.mkdirs();
							downloadDir(newUrl, folder);
						}
						else
						{
							downloadInto(newUrl, file.getParentFile());
						}
						files++;
					}
				}
			}
		}
		return file.getParentFile();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		URL url;
		try
		{
			url = new URL("http://test");
			File folder = new File(new File(System.getProperty("java.io.tmpdir"), "Downloader"), url.getHost());
			folder.mkdirs();
			File file = downloadDir(url, folder);
			System.out.println(file.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.print("Done");
	}

}
