
import java.io.*;
import java.util.*;
import javax.comm.*;
import javax.swing.JOptionPane;
public class Serial {
	private CommPortIdentifier portID;
	private SerialPort serial;
	private OutputStream outputStream;
	private InputStream inputStream;
	private String comName = new String("COM1");
	public Serial()
	{
		try
		{
			portID = CommPortIdentifier.getPortIdentifier(comName);   //找串口
			serial = (SerialPort)portID.open(comName, 2000);    //打开串口
			inputStream = serial.getInputStream();
			outputStream = serial.getOutputStream();
			serial.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
		} 
		catch (NoSuchPortException e) 
		{
			e.printStackTrace();
		} 
		catch (PortInUseException e)
		{
			JOptionPane.showMessageDialog(null, "串口正在使用");
		} 
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public SerialPort getSerial()
	{
		return serial;
	}
	public OutputStream getOutputStream()
	{
		return outputStream;
	}
	public InputStream getInputStream()
	{
		return inputStream;
	}
}
