package qputility.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QPImageBase64
{
	public static String getBase64(String loc) throws FileNotFoundException,IOException
	{
		String result64 = "";
		File f = null;
		FileInputStream is = null;
		try
		{
			f = new File(loc);
			is = new FileInputStream(new File(loc));
			byte[] bytes = new byte[(int) f.length()];
			is.read(bytes);
			result64 = new sun.misc.BASE64Encoder().encode(bytes);
		}
		catch (FileNotFoundException fnf)
		{
			throw new FileNotFoundException(fnf.getMessage());
		}
		catch (IOException io)
		{
			throw new IOException(io.getMessage());
		}
		finally
		{
			if (is != null)
				is.close();
		}
		return result64;
	}

	public static byte[] getBytes(String base64File) throws IOException
	{
		byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(base64File);
		return bytes;
	}
}
