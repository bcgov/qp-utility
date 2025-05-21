package qputility.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chris.Ditcher
 * Class for removing files in an array based upon
 * a regex filter. 
 */
public class BackupFileFilter implements FilenameFilter {
	
	String regex;
	
	/**
	 * Constructor
	 * @param regex
	 */
	public BackupFileFilter(String regex){
		this.regex = regex;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
	}
	
	/**
	 * Given an array of files, sorts by number and
	 * removes any files over the amount of 'maxBackups'
	 * @param maxBackups the maximum number of backups allowed
	 * @param files an array of files to sort and trim
	 */
	public void sortAndRemoveBackups(int maxBackups, File[] files){
		if(files != null) 
		{
			Arrays.sort(files, new Comparator<File>() {
	            public int compare(File o1, File o2) {
	                int n1 = extractNumber(o1.getName());
	                int n2 = extractNumber(o2.getName());
	                return n1 - n2;
	            }

	            private int extractNumber(String name) {
	                int i = 0;
	                try {
	                    int s = name.indexOf('_')+1;
	                    int e = name.lastIndexOf('.');
	                    String number = name.substring(s, e);
	                    i = Integer.parseInt(number);
	                } catch(Exception e) {
	                    i = 0; // if filename does not match the format
	                           // then default to 0
	                }
	                return i;
	            }
	        });
		}
		
		// now that we have sorted the files, remove superfluous files from tail of array
		if(files.length > maxBackups)
		{
			for (int i = 0; i < (files.length-maxBackups); i++) {
				File f = files[i];
				f.delete();
			}
		}
	}

}
