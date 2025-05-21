package qputility.encodeDecode;

public class QPEncodeDecode
{
	public static String htmlDecodeValue(String value)
	{
		value = value.replace("&lt;", "<");
		value = value.replace("&gt;", ">");
		return value;
	}
	
	public static String htmlEncodeValue(String value)
	{
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		return value;
	}
}
