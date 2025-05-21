package qputility.execute;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class QPExecute 
{
	public static void launchFile(File file) throws Exception
	{
		Desktop dt = Desktop.getDesktop();
		dt.open(file);
	}
	
	public static int runCMD(String command, boolean wait) throws IOException, InterruptedException 
	{
		return doCMD(command, wait, true, false, false);
	}
	
	public static int runCMD(String command, boolean wait, boolean showOutput) throws IOException, InterruptedException 
	{
		return doCMD(command, wait, showOutput, false, false);
	}
	
	public static int runCMD(String command, boolean wait, boolean showOutput, boolean showOutputStreamPrefix) throws IOException, InterruptedException
	{
		return doCMD(command, wait, showOutput, showOutputStreamPrefix, false);
	}
	
	public static int runCMD(String command, boolean wait, boolean showOutput, boolean showOutputStreamPrefix, boolean runWithoutCMDexe) throws IOException, InterruptedException
	{
		return doCMD(command, wait, showOutput, showOutputStreamPrefix, runWithoutCMDexe);
	}

	private static int doCMD(String command, boolean wait, boolean showOutput, boolean showOutputStreamPrefix, boolean runWithoutCMDexe) throws IOException, InterruptedException
	{
		String[] cmd = new String[3];
		
		cmd[0] = "cmd.exe";
		cmd[1] = "/C";
		cmd[2] = command;

		Runtime rt = Runtime.getRuntime();
		
		Process proc;
		if(runWithoutCMDexe)
		{
			proc = rt.exec(command);
		}
		else
		{
			proc = rt.exec(cmd);
		}
		
		new Thread(new QPStreamGobbler(proc.getInputStream(), "OUTPUT", showOutput, showOutputStreamPrefix)).start();
        new Thread(new QPStreamGobbler(proc.getErrorStream(), "ERROR", showOutput, showOutputStreamPrefix)).start();

        proc.getOutputStream().close();
        
		if(wait)
		{
			proc.waitFor();
		}
		
		int retVal = proc.exitValue();
		proc = null;
		return retVal;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		QPExecute.runCMD("\robocopy.exe\" \"live\" \"sync_live\" /mir /NDL /ETA /LOG:\"robocopy.log\"", true, true, true);
	}
}
