package qputility.generate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QPRandom 
{
	public static String getUniqueID()
	{	
		return getUniqueID(1);
	}
	
	public static String getUniqueID(int howManyGroupsOf10Digits)
	{
		String retVal = "";
		try 
		{	
			SecureRandom random;
			random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(0);	
			for(int i = 0; i < howManyGroupsOf10Digits; i ++)
			{
				retVal += random.nextInt(Integer.MAX_VALUE) + "";
			}
		} 
		catch (NoSuchAlgorithmException e) 
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyymmmmddhhmmss");
			Date date = new Date();
			double curVal = Double.parseDouble(dateFormat.format(date));
			retVal = curVal + "";
		} 
		return retVal;
	}
	
	public static void main(String[] args)
	{
		for(int i = 0; i < 50; i ++)
		{
			System.out.println(QPRandom.getUniqueID(4));
		}
	}
}
