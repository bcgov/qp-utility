package qputility.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QPDate
{
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static String currentDateAndTime()
	{
		return now(DATE_FORMAT_NOW);
	}
	
	public static String currentDate()
	{
		return now("yyyy-MM-dd");
	}
	
	public static String currentTime()
	{
		return now("HH:mm:ss");
	}
	
	public static String now(String dateFormat)
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static void main(String[] args)
	{
		 System.out.println("Now : " + QPDate.currentDate());
		 System.out.println("Now : " + QPDate.currentDateAndTime());
		 System.out.println("Now : " + QPDate.currentTime());
	}

}
