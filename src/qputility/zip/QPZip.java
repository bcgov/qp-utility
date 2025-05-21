package qputility.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class QPZip 
{
	private ZipOutputStream cpZipOutputStream = null;
	public String strSource = "";		
	public String strTarget = "";		
	private static long size = 0;	
	private static int numOfFiles = 0;
	
	public void zip() throws Exception
	{							
		File cpFile = new File (strSource);									
		if (!cpFile.isFile() && !cpFile.isDirectory() ) 
		{							
			return;					
		}								
		FileOutputStream fos = new FileOutputStream(strTarget);			
		cpZipOutputStream = new ZipOutputStream(fos);					
		cpZipOutputStream.setLevel(9);					
		zipFiles( cpFile);					
		cpZipOutputStream.finish();					
		cpZipOutputStream.close();
	}	
	
	private void  zipFiles(File cpFile) throws Exception
	{
		int byteCount;
		final int DATA_BLOCK_SIZE = 2048;	
		FileInputStream cpFileInputStream;
				
		if (cpFile.isDirectory()) 
		{						
			if(cpFile.getName().equalsIgnoreCase(".metadata"))
			{ 
				//if directory name is .metadata, skip it.					
				return;			
			}			
			File[] fList = cpFile.listFiles();						
			for (int i=0; i< fList.length; i++)
			{								
				zipFiles(fList[i]) ;						
			}				
		} 
		else 
		{		
			if(cpFile.getAbsolutePath().equalsIgnoreCase(strTarget))
			{
				return;
			}		
			size += cpFile.length();
			
			numOfFiles++;				
			String strAbsPath = cpFile.getPath();				
			String strZipEntryName = strAbsPath.substring(strSource.length(), strAbsPath.length());								
				
			cpFileInputStream = new FileInputStream (cpFile) ;															
			ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
			cpZipOutputStream.putNextEntry(cpZipEntry );
				
			byte[] b = new byte[DATA_BLOCK_SIZE];
			while ( (byteCount = cpFileInputStream.read(b, 0, DATA_BLOCK_SIZE)) != -1)
			{
				cpZipOutputStream.write(b, 0, byteCount);
			}
		
			cpZipOutputStream.closeEntry() ;
		}		
	}

	public void unZip(String filePath, String rootDirPath) throws Exception
	{
		int BUFFER = 2048;
		try
		{
			new File(rootDirPath).mkdirs();
		}
		catch(Exception e)
		{}
		try 
		{
	         BufferedOutputStream dest = null;
	         FileInputStream fis = new FileInputStream(filePath);
	         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	         ZipEntry entry;
	         while((entry = zis.getNextEntry()) != null) 
	         {
	        	 if(entry.isDirectory())
	        	 {
	        		 String[] splits = entry.getName().split("/");
	        		 String dirToMake = rootDirPath;
	        		 for(int i = 0; i <= splits.length -1; i ++)
	        		 {	    			
	        			 dirToMake += splits[i] + "/";
	        			 new File (dirToMake).mkdirs();
	        		 }
	        	 }
	        	 else
	        	 {
	        		if(entry.getName().contains("\\"))
	     			{
	        			String tempEntry = entry.getName().replace("\\", "/");
	     				String dirToMake = rootDirPath;
	     				String[] splits = tempEntry.split("/");
	     				for(int s = 0; s < splits.length -1; s++)
	     				{
	     					dirToMake += splits[s] + "/";
	     				}
	     				File targetDir = new File(dirToMake);
	     				if(!targetDir.exists())
	     				{
	     					targetDir.mkdirs();
	     				}
	     			}
	        		 
	        		 //System.out.println("Extracting: " + entry + " - name =  " + entry.getName());
	        		 int count;
	        		 byte data[] = new byte[BUFFER];
	        		 // write the files to the disk
	        		 FileOutputStream fos = new 
	        		 FileOutputStream(rootDirPath + entry.getName());
	        		 dest = new BufferedOutputStream(fos, BUFFER);
	        		 while ((count = zis.read(data, 0, BUFFER)) != -1) 
	        		 {
	        			 dest.write(data, 0, count);
	        		 }
	        		 dest.flush();
	        		 dest.close();
	        	 }
	         }
	         zis.close();
		} 
		catch(Exception e) 
		{
			throw new Exception(e);
		}
	}	
}
