package qputility.romanNumeral;

public class QPRomanNumeral 
{
	public static boolean isValidRomanNumeral(String rootCurrentItemNumber)
	{
		rootCurrentItemNumber = rootCurrentItemNumber.toLowerCase();
		boolean valid = true;
		for(int i = 0; i < rootCurrentItemNumber.length(); i ++)
		{
			if(!"mdclxvi".contains(rootCurrentItemNumber.charAt(i) + ""))
			{
				valid = false;
			}
		}
		return valid;
	}
	
	public static String getRomanValueOfInt(int num)
	{
		int[] numbers = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		String[] letters = { "m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"};

		String retVal = "";
		for(int i = 0; i < numbers.length; i ++)
		{
			while (num >= numbers[i])
			{
				retVal += letters[i];
				num -= numbers[i];
			}
		}
		return retVal;
	}
	
	public static int getIntValueOfRomanNumeral(String roman)
	{
		roman = roman.toLowerCase();
		int retVal = 0;
		int i = 0;
		
		while(i < roman.length())
		{
			char letter = roman.charAt(i);
			int number = romanLetterToNumber(letter);
			
			i++;
			if(i == roman.length())
			{
				retVal += number;
			}
			else
			{
				int nextNumber = romanLetterToNumber(roman.charAt(i));
				if(nextNumber > number)
				{
					retVal += (nextNumber - number);
					i++;
				}
				else
				{
					retVal += number;
				}				
			}
		}
		return retVal;
	}
	
	public static int romanLetterToNumber(char letter) 
	{        
		switch (letter) 
		{
	        case 'i':  return 1;
	        case 'v':  return 5;
	        case 'x':  return 10;
	        case 'l':  return 50;
	        case 'c':  return 100;
	        case 'd':  return 500;
	        case 'm':  return 1000;
	        default:   return -1;
		}
	}
}