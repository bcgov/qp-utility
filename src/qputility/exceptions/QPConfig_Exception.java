package qputility.exceptions;

public class QPConfig_Exception extends Exception
{
	private static final long serialVersionUID = -2227662912585093761L;
	private String message;
	
	public QPConfig_Exception()
	{
		super();
		message = "Duplicate keys have been found the in the config file.";
	}
	
	public QPConfig_Exception(String err) 
	{
		super(err);
		message = err;	
	}
	
	public QPConfig_Exception(String err, Exception e) 
	{
		super(err, e);
		message = err;	
	}
	
	public String getError()
	{
		return message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
