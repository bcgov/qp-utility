package qputility.io;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

public class QPWriter {

	
	private int bufferSize = 1024 * 2;
	
	/**
	 * When using the class to write you can conserve memory by setting this value low or improve performance by setting value high,
	 * default is 1024 * 2.
	 * @param bufferSize	A multiple of 1024 bytes representing the amount to read in to memory before writing to file.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Write's a file to disk using a buffer for fast performance, buffer is set to 1024 by default.
	 * @param from			The file you are writing from.
	 * @param to			The file you are writing to.
	 * @param append		Whether to append to the new file or overwrite it.
	 * @throws IOException	An exception occurred reading or writing to the file.
	 */
	public void writeFile(String from, String to, Boolean append) throws IOException {
		
		File fIn = new File(from);
		File fOut = new File(to);
		OutputStream out = null;
		InputStream inStream = null;
		try {
			out = new FileOutputStream(fOut, append);
			inStream = new BufferedInputStream(new FileInputStream(fIn));
			byte[] buffer = new byte[this.bufferSize];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} catch (IOException e) {
			throw new IOException();
		} finally {
			fOut = null;
			fIn = null;
			if(out != null)
				out.close();
			if(inStream != null)
				inStream.close();
		}
	}
	
	/**
	 * Writes an array of strings to a file line by line.
	 * @param lines				Content to write to the file.
	 * @param outputFilePath	Path to the output file.
	 * @param append			Whether to append or overwrite existing content.
	 * @param create			If the file does not exits whether or not to create it.
	 * @throws IOException		The file does not exist and create is false, or an exception reading or writing to the file.
	 */
	public void writeFileLineByLine(String[] lines, String outputFilePath, Boolean append, Boolean create) throws IOException {
		
		File f = new File(outputFilePath);
		
		if (!f.exists()) {
			if (create) {
				f.createNewFile();
			} else {
				throw new IOException("Error: File " + outputFilePath + " does not exist.");
			}
		}
		
		f = null;
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < lines.length; i++) {
			sb.append(lines[i] + "\r\n");
		}
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(outputFilePath, append));
			output.write(sb.toString());
		} catch (IOException io) {
			throw new IOException(io);
		} finally {
			output.close();
		}
		
	}
	
	public void writeStringToFile(String content, String outputFilePath, Boolean append, Boolean create) throws IOException
	{
		String[] lines = {content};
		writeFileLineByLine(lines, outputFilePath, append, create);
	}
	
	/**
	 * Writes an arraylist of content to a file.
	 * @param lines				The content to write to the file.
	 * @param outputFilePath	The file path to write the content to.
	 * @param append			Whether to append or overwrite the content.
	 * @param create			If the file does not exist whether to create it or not.
	 * @throws IOException		The file does not exist and create is false, or an exception reading or writing to the file.
	 */
	public void writeFileLineByLine(ArrayList<String> lines, String outputFilePath, Boolean append, Boolean create) throws IOException {
		this.writeFileLineByLine((String[])lines.toArray(new String[lines.size()]), outputFilePath, append, create);
	}
	
	/**
	 * Creates a new file based on a list of other files.
	 * @param from			A List of file paths we will combine.
	 * @param to			The output file of the content.
	 * @throws IOException	If an error occurs reading or writing to the files.
	 */
	public void appendMultipleFiles(List<String> from, String to) throws IOException {
		
		for (int i = 0; i < from.size(); i++) {
			if (i == 0) {
				this.writeFile(from.get(i), to, false);
			} else {
				this.writeFile(from.get(i), to, true);
			}
		}
		/*
		byte[] buffer = new byte[this.bufferSize];
		File fOut = new File(to);
		File fIn = null;
		OutputStream out = null;
		InputStream inStream = null;
		int bytesRead = -1;
		
		try {
			if(fOut.exists())
				fOut.delete(); // When appending multiple files we always create a new file
			
			out = new FileOutputStream(fOut, true); // Create our output stream
			for (int i = 0; i < from.size(); i++) {
				fIn = new File(from.get(i));
				inStream = new BufferedInputStream(new FileInputStream(fIn));
				bytesRead = -1;
				while ((bytesRead = inStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.flush();
				inStream.close();
				inStream = null;
			}
		} catch(IOException io) {
			throw new IOException(io);
		} finally {
			if (out != null) {
				out.close();
			}
			if (inStream != null) {
				inStream.close();
			}
			
		}
		*/
	}
}
