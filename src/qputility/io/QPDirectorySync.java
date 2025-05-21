package qputility.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qputility.debug.QPTimer;
import qputility.enums.QPDirectorySyncMode;
import qputility.xPath.QPXPath;
import qputility.xml.QPCreateXMLFileStructure;

public class QPDirectorySync
{
	/**
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException 
	 */
	private qputility.debug.QPTimer timer;
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		// TODO Auto-generated method stub
		System.out.println("start sync");
		ensureSync("Test Environment\\author 2", "Test Environment\\target2", QPDirectorySyncMode.clean_all_orphans_only, true, null);
	
		System.out.println("done sync");
	}

	public static void ensureSync(String sourceDir, String targetDir, QPDirectorySyncMode mode, boolean updateNewerFilesInTarget, String[] fileFilter) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		System.out.println("Sync mode = " + mode.toString());
		QPDirectorySync sync = new QPDirectorySync();
		sync.timer = new QPTimer();
		sync.timer.start();
		
		boolean addNewFiles = false, updateExistingTargetFiles = false, deleteTargetOrphanFiles = false, deleteTargetOrphanFolders = false;
		if(mode.equals(QPDirectorySyncMode.target_mirrors_source))
		{
			addNewFiles = true;
			updateExistingTargetFiles = true;
			deleteTargetOrphanFiles = true;
			deleteTargetOrphanFolders = true;
		}
		else if(mode.equals(QPDirectorySyncMode.update_target))
		{
			addNewFiles = true;
			updateExistingTargetFiles = true;
			deleteTargetOrphanFiles = false;
			deleteTargetOrphanFolders = false;
		}
		else if (mode.equals(QPDirectorySyncMode.update_existing_files_only))
		{
			addNewFiles = false;
			updateExistingTargetFiles = true;
			deleteTargetOrphanFiles = false;
			deleteTargetOrphanFolders = false;
		}
		else if (mode.equals(QPDirectorySyncMode.clean_up_orphan_files_only))
		{
			addNewFiles = false;
			updateExistingTargetFiles = false;
			deleteTargetOrphanFiles = true;
			deleteTargetOrphanFolders = false;
		}
		else if (mode.equals(QPDirectorySyncMode.clean_up_orphan_folders_only))
		{
			addNewFiles = false;
			updateExistingTargetFiles = false;
			deleteTargetOrphanFiles = false;
			deleteTargetOrphanFolders = true;
		}
		else if (mode.equals(QPDirectorySyncMode.clean_all_orphans_only))
		{
			addNewFiles = false;
			updateExistingTargetFiles = false;
			deleteTargetOrphanFiles = true;
			deleteTargetOrphanFolders = true;
		}
		
		sourceDir = sourceDir.replace("/", "\\");
		targetDir = targetDir.replace("/", "\\");

		if (!sourceDir.endsWith("\\"))
		{
			sourceDir += "\\";
		}
		if (!targetDir.endsWith("\\"))
		{
			targetDir += "\\";
		}
		
		File targetDirFile = new File(targetDir);
		if(!targetDirFile.exists())
		{
			targetDirFile.mkdirs();
		}

		QPTimer structureTimer = new QPTimer();
		structureTimer.start();
		Document sourceFileStructureDocument = QPCreateXMLFileStructure.createXMLStructure(sourceDir);
		Document targetFileStructureDocument = QPCreateXMLFileStructure.createXMLStructure(targetDir);
		structureTimer.stop();
		System.out.println("Creating structures took " + structureTimer.toString());

		QPTimer listTimer = new QPTimer();
		listTimer.start();
		sourceFileStructureDocument.getDocumentElement().normalize();
		targetFileStructureDocument.getDocumentElement().normalize();

		NodeList sourceFileList = sourceFileStructureDocument.getElementsByTagName("file");
		NodeList targetFileList = targetFileStructureDocument.getElementsByTagName("file");

		NodeList targetFolderList = targetFileStructureDocument.getElementsByTagName("directory");
		
		listTimer.stop();
		System.out.println("Creating nodeLists took " + listTimer.toString());
		
		if(addNewFiles | updateExistingTargetFiles)
		{
			QPTimer addNewFileTimer = new QPTimer();
			addNewFileTimer.start();
			// checking for new or updated files
			for (int i = 0; i < sourceFileList.getLength(); i++)
			{
				Element sourceElem = (Element)sourceFileList.item(i);
				String sourceFileID = sourceElem.getAttribute("id");
				if(!QPFileIO.isFileInFileFilter(new File(sourceFileID), fileFilter))
				{
					// what the id should be in the target dir
					String proposedTargetFileId = sourceFileID.replace(sourceDir, targetDir);
		
					// testing to see if the file exists in the target
					String targetFileID = getFileIDFromStructure(proposedTargetFileId, targetFileStructureDocument);
					long sourceLastModified = Long.parseLong(sourceElem.getAttribute("modified"));
					if (targetFileID.trim().equals(""))
					{
						if(addNewFiles)
						{
							// copy file since it does not exist.
							QPFileIO.copyFile(sourceFileID, proposedTargetFileId, sourceLastModified);
						}
					}
					else
					{
						// check the last modified on the file
						if(updateExistingTargetFiles)
						{
							long targetLastModifed = getLastModifiedFromStructure(proposedTargetFileId, targetFileStructureDocument);
							if (sourceLastModified > targetLastModifed)
							{
								// file has been modified
								QPFileIO.copyFile(sourceFileID, proposedTargetFileId, sourceLastModified);
							}
							else if(targetLastModifed > sourceLastModified)
							{
								if(updateNewerFilesInTarget)
								{
									QPFileIO.copyFile(sourceFileID, proposedTargetFileId, sourceLastModified);
								}
								else
								{
									System.out.println("found a newer file in the target - " + sourceFileID);
								}
							}
						}
					}
				}
			}
			addNewFileTimer.stop();
			System.out.println("Adding new files took " + addNewFileTimer.toString());
		}
			
		if(deleteTargetOrphanFiles)
		{
			QPTimer deleteOrphanFilesTimer = new QPTimer();
			deleteOrphanFilesTimer.start();
			//deleted file check
			for(int i = 0; i < targetFileList.getLength(); i++)
			{
				Element targetElem = (Element)targetFileList.item(i);
				String targetFileID = targetElem.getAttribute("id");
				
				String proposedSourceFileId = targetFileID.replace(targetDir, sourceDir);
				String sourceFileID = getFileIDFromStructure(proposedSourceFileId, sourceFileStructureDocument);
				if(sourceFileID.trim().equals(""))
				{
					new File(targetFileID).delete();
				}
			}
			deleteOrphanFilesTimer.stop();
			System.out.println("Deleting Orphan Files took " + deleteOrphanFilesTimer.toString());
		}
				
		if(deleteTargetOrphanFolders)
		{
			QPTimer deleteOrphanFolderTimer = new QPTimer();
			deleteOrphanFolderTimer.start();
			//deleted folder check
			for(int i = 0; i < targetFolderList.getLength(); i++)
			{
				Element targetElem = (Element)targetFolderList.item(i);
				String targetFolderID = targetElem.getAttribute("id");
				
				String proposedSourceFolderId = targetFolderID.replace(targetDir, sourceDir);
				String sourceFolderID = getFolderIDFromStructure(proposedSourceFolderId, sourceFileStructureDocument);
				if(sourceFolderID.trim().equals(""))
				{
					QPFileIO.deleteDirectory(targetFolderID);
				}
			}
			deleteOrphanFolderTimer.stop();
			System.out.println("Delete Orphan Folders took " + deleteOrphanFolderTimer.toString());
		}
		
		sync.timer.stop();
		System.out.println("total time =  " + sync.timer.toString());
	}

	private static String getFileIDFromStructure(String id, Document doc) throws XPathExpressionException, FileNotFoundException
	{
		if (id.contains("'"))
		{
			return QPXPath.xPathXMLDocument(
					"/root//file[@id=\"" + id + "\"][1]/@id", doc);
		}
		else
		{
			return QPXPath.xPathXMLDocument("/root//file[@id='"
					+ id + "'][1]/@id", doc);
		}
	}

	private static String getFolderIDFromStructure(String id, Document doc) throws XPathExpressionException, FileNotFoundException
	{
		if(id.contains("'"))
		{
			return QPXPath.xPathXMLDocument("/root//directory[@id=\"" + id + "\"][1]/@id", doc);
		}
		else
		{
			return QPXPath.xPathXMLDocument("/root//directory[@id='" + id + "'][1]/@id", doc);
		}
	}
	
	private static long getLastModifiedFromStructure(String id, Document doc) throws XPathExpressionException, FileNotFoundException
	{
		try
		{
			if (id.contains("'"))
			{
				return Long.parseLong(QPXPath.xPathXMLDocument(
						"/root//file[@id=\"" + id + "\"][1]/@modified", doc));
			}
			else
			{
				return Long.parseLong(QPXPath.xPathXMLDocument(
						"/root//file[@id='" + id + "'][1]/@modified", doc));
			}
		}
		catch (NumberFormatException nfe)
		{
			return 0;
		}
	}
}
