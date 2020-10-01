import javax.swing.JFrame;

import jxl.write.WriteException;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.TooManyListenersException;
public class Run {
	public static void main(String[] args) throws TooManyListenersException, IOException, WriteException
	{
		Window window = new Window();  //窗口
		Serial serialObject = new Serial();
		window.setSerialPort(serialObject);
		JFrame frame = new JFrame("Test Chart");    //图
		Chart rtcp = new Chart(null, "动态曲线图", "电压"); 
		new BorderLayout();
		frame.getContentPane().add(rtcp, BorderLayout.CENTER); 
		frame.pack(); 
		frame.setVisible(true); 
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		ReceiveSend rS = new ReceiveSend(serialObject, window.recArea, rtcp);
		window.rSend = rS;
		Thread readThread = new Thread(rS);
		Thread rtcpTh = new Thread(rtcp);
		window.setThread(readThread, rtcpTh);
		readThread.start();
		rtcpTh.start();
	}
}
