package qputility.objects;

public class QPProcessInfoObject
{
	private String _caption = "", _commandLine = "", _pid = "";

	public QPProcessInfoObject(String caption, String commandLine, String pid)
	{
		_caption = caption;
		_commandLine = commandLine;
		_pid = pid;
	}

	public String caption()
	{
		return _caption;
	}

	public String commandLine()
	{
		return _commandLine;
	}

	public String pid()
	{
		return _pid;
	}
}
