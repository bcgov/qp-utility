package qputility.mapDrives;

import java.io.IOException;

public class QPMapDrives
{

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		QPMapDrives.mapDrives();
	}

	public static void mapDrives() throws IOException, InterruptedException
	{
		qputility.execute.QPExecute.runCMD("script.bat", true);
	}
	
	public static void mapDrives(String logonBatFile) throws IOException, InterruptedException
	{
		qputility.execute.QPExecute.runCMD(logonBatFile, true);
	}
}
