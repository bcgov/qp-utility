package qputility.execute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QPStreamGobbler extends Thread
{
	InputStream is;
	String type;
	Boolean showOutputStreamPrefix;
	Boolean showOutput;

	QPStreamGobbler(InputStream is, String type)
	{
		this.is = is;
		this.type = type;
		this.showOutputStreamPrefix = false;
	}
	
	QPStreamGobbler(InputStream is, String type, boolean showOutput, boolean showOutputStreamPrefix)
	{
		this.is = is;
		this.type = type;
		this.showOutput = showOutput;
		this.showOutputStreamPrefix = showOutputStreamPrefix;
	}

	public void run()
	{
		if(showOutput)
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				
				while ((line = br.readLine()) != null)
				{
					if(showOutputStreamPrefix)
					{
						System.out.print(type + ">");
					}
					System.out.println(line);
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
}