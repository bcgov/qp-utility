package qputility.exceptions;

public class QPGenericException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -141509415406662745L;
	private String message;
	
	public QPGenericException()
	{
		super();
		message = "An Exception has occured.";
	}
	
	public QPGenericException(String err) 
	{
		super(err);
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
