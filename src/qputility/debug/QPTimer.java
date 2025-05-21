package qputility.debug;

/**
 * A very simple timer for measuring code execution. Will only be accurate for
 * tasks that take a fairly lengthy amount of time; Say over 100 ms
 * 
 * @author spencer.tickner
 * 
 */
public class QPTimer
{
	private long startTime, stopTime; // Millisecond representation of time
	private boolean running;

	private void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	private void setStopTime(long stopTime)
	{
		this.stopTime = stopTime;
	}

	/**
	 * The last time the timer was started.
	 * 
	 * @return start time in milliseconds.
	 */
	public long getStartTime()
	{
		return this.startTime;
	}

	/**
	 * The last time the timer was stopped.
	 * 
	 * @return stop time in milliseconds.
	 */
	public long getStopTime()
	{
		return this.stopTime;
	}

	/**
	 * Constructor to specify if you want to create a timer that starts running
	 * right away.
	 * 
	 * @param start
	 *        true if you want the timer to start running right away, false if
	 *        you want a timer in the off state
	 */
	public QPTimer(boolean start)
	{
		if (start)
		{
			this.running = true;
			this.setStartTime(System.currentTimeMillis());
		}
	}

	/**
	 * Default constructor, creates a new timer that is not running.
	 */
	public QPTimer()
	{
		this.running = false;
	}

	/**
	 * Starts the timer.
	 */
	public void start()
	{
		this.setStartTime(System.currentTimeMillis());
		this.running = true;
	}

	/**
	 * Stops the timer.
	 */
	public void stop()
	{
		this.setStopTime(System.currentTimeMillis());
		this.running = false;
	}

	/**
	 * Gets the number of milliseconds the timer has been running.
	 * 
	 * @return the number of milliseconds the timer has been running or the
	 *         difference between the start and stop time if the timer is
	 *         stopped.
	 */
	public long diff()
	{
		if (this.running)
		{
			return System.currentTimeMillis() - this.startTime;
		}
		else
		{
			return this.stopTime - this.startTime;
		}
	}

	/**
	 * Returns a string representation of the current timer.
	 * 
	 * @return a string displaying the time in Days, Hours, Minutes, Seconds and
	 *         Milliseconds.
	 */
	public String toString()
	{
		long diff = this.diff();
		long millis = diff % 1000;
		long secs = (diff / 1000) % 60;
		long mins = (diff / (1000 * 60)) % 60;
		long hs = (diff / (1000 * 3600)) % 24;
		long days = diff / (1000 * 3600 * 24);

		if (days > 0)
			return days + "d " + hs + "h " + mins + "m " + secs + "s " + millis
					+ "ms";

		if (hs > 0)
			return hs + "h " + mins + "m " + secs + "s " + millis + "ms";

		if (mins > 0)
			return mins + "m " + secs + "s " + millis + "ms";

		if (secs > 0)
			return secs + "s " + millis + "ms";

		return millis + "ms";
	}

	public static void main(String[] args) throws Exception
	{
		QPTimer qptimer = new QPTimer(true);
		qptimer.start();
		Thread.sleep(3000);
		System.out.println(qptimer.toString());

	}

}
