package qputility.forms;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class QPRunningProgressBar 
{
	private JFrame theFrame;
	public QPRunningProgressBar(String title, int width, int height, int locationX, int locationY) 
	{
		final JProgressBar aJProgressBar = new JProgressBar(0, 100);
		aJProgressBar.setIndeterminate(true);

		theFrame = new JFrame(title);
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = theFrame.getContentPane();
		contentPane.add(aJProgressBar, BorderLayout.CENTER);
		theFrame.setSize(width, height);
		theFrame.setLocation(locationX, locationY);
		theFrame.setVisible(true);
	}
	
	public void killProgressBar()
	{
		theFrame.dispose();
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		QPRunningProgressBar qpbar = new QPRunningProgressBar("title", 500, 50, 900, 400);
		//qpbar.killProgressBar();
	}
}
