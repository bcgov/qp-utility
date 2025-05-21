package qputility.process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import qputility.execute.QPExecute;
import qputility.objects.QPProcessInfoObject;

public class QPProcessChecker
{
	public static boolean isProcessRunning(String process)
	{
		boolean found = false;
		try
		{
			File file = File.createTempFile("Temp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
					+ "Set locator = CreateObject(\"WbemScripting.SWbemLocator\")\n"
					+ "Set service = locator.ConnectServer()\n"
					+ "Set processes = service.ExecQuery _\n"
					+ " (\"select name from Win32_Process where name='"
					+ process
					+ "'\")\n"
					+ "For Each process in processes\n"
					+ "wscript.echo process.Name \n"
					+ "Next\n"
					+ "Set WSHShell = Nothing\n";

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			System.out.println(file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line;
			line = input.readLine();
			if (line != null)
			{
				if (line.equals(process))
				{
					found = true;
				}
			}
			input.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return found;
	}
		
	public static ArrayList<QPProcessInfoObject> getInfoOnAllInstancesOfProcess(String processName) throws Exception
	{
		ArrayList<QPProcessInfoObject> retVal = new ArrayList<QPProcessInfoObject>();
		
		File file = File.createTempFile("Temp", ".txt");
		file.deleteOnExit();
		
		final String command = "wmic /OUTPUT:" + file.getPath() + " process where name=\"" + processName + "\" get Caption,Commandline,Processid";
		QPExecute.runCMD(command, true);
					
		FileInputStream fis = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-16"));
		
		String line;
		while((line = br.readLine()) != null)
		{
			if(!line.contains("Caption          CommandLine"))
			{
				line = line.trim();
				String[] splits = line.split("  ");
				String caption = splits[0].trim();
				String commandLine = splits[1].trim().replace("\"", "");
				String pid = splits[2].trim();
				
				retVal.add(new QPProcessInfoObject(caption, commandLine, pid));
			}
		}
		in.close();
		return retVal;
	}
}
