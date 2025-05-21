package qputility.url;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author alex.strudwick
 *
 */
public class QPURL
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param URLName
	 * @return
	 */
	public static boolean exists(String URLName)
	{
		try
		{
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
