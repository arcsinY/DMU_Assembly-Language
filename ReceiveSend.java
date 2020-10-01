import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.comm.*;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class ReceiveSend implements Runnable, SerialPortEventListener{
	private Serial serial;
	private byte[] analog;
	private byte[] digital;
	private byte[] analogHalf;   //减半后的模拟量
	private double threshold = 10000;
	private double analogVolt;
	private int analogUnsigned;
	private JTextArea textArea;
	private Chart chart;
	private OutPut outPut;
	private String analogString;   //模拟量转化为字符串
	private	String digitalString;
	private String show;      //要显示的字符串
	private boolean haveMessageBox;
	public ReceiveSend(Serial serial, JTextArea textArea, Chart chart) throws TooManyListenersException {
		this.serial = serial;
		this.textArea = textArea;
		this.chart = chart;
		this.outPut = outPut;
		analog = new byte[1];
		digital = new byte[1];
		analogHalf = new byte[1];
		haveMessageBox = false;
		serial.getSerial().addEventListener(this);
	}

	public void run() {
		while(Thread.currentThread().isInterrupted() == false)
		{
		try 
		{
			if(serial.getInputStream().available() > 0)
			{
				int byteNum = serial.getInputStream().read(digital);   //收模拟量和数字量
				byteNum = serial.getInputStream().read(analog);
				if(analog[0] < 0)
					analogUnsigned = ((int)analog[0] + 256);
				else
					analogUnsigned = analog[0];
				analogVolt = ((double)analogUnsigned/256)*5.0;   //计算电压值
			}
			//输出到屏幕
			if(analog[0] != 0 || digital[0] != 0)
			{
				show = new String();
				digitalString = new String(Integer.toHexString(digital[0]));
				if(digital[0] >= 0)
					digitalString = digitalString.substring(6);
				else
					digitalString = digitalString.substring(6);
				if(analog[0] >= 0)
				{	
					analogString = new String(Integer.toHexString(analog[0]));
					show = "数字量：" + digitalString + "  " + "模拟量：" + analogString + "\n";
					chart.setNewData(analogVolt);
				}
				else
				{
					analogString = new String(Integer.toHexString(analog[0]).substring(6));
					show = "数字量：" + digitalString + "  " + "模拟量：" + analogString + "\n";
					chart.setNewData(analogVolt);   //有符号负数转为无符号数
				}
				textArea.append(show);
				Date day=new Date();    
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //获取时间
				if(outPut != null)
					outPut.addData(df.format(day), analogString, digitalString);
				analogHalf[0] = (byte) (analogUnsigned/2);    //发送减半的数据
				serial.getOutputStream().write(analogHalf);
			}
			if(analogVolt > threshold)   //如果超过阈值，弹框
			{	
				if(haveMessageBox == false)
				{
					JOptionPane.showMessageDialog(null, "超过阈值!");
					haveMessageBox = true;
				}
			}
			else
			{
				haveMessageBox = false;
			}
			try
			{
				Thread.sleep(500);
			}catch(InterruptedException e){
				return;
			}
			
		} catch (IOException e){}
		}			
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	@Override
	public void serialEvent(SerialPortEvent e) {
		if(e.getEventType() == SerialPortEvent.PE)
			JOptionPane.showMessageDialog(null, "数据传输中出错！");
		
	}
	public void setOutput(OutPut outPut)
	{
		this.outPut = outPut;
	}
}
