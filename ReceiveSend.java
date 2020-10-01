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
	private byte[] analogHalf;   //������ģ����
	private double threshold = 10000;
	private double analogVolt;
	private int analogUnsigned;
	private JTextArea textArea;
	private Chart chart;
	private OutPut outPut;
	private String analogString;   //ģ����ת��Ϊ�ַ���
	private	String digitalString;
	private String show;      //Ҫ��ʾ���ַ���
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
				int byteNum = serial.getInputStream().read(digital);   //��ģ������������
				byteNum = serial.getInputStream().read(analog);
				if(analog[0] < 0)
					analogUnsigned = ((int)analog[0] + 256);
				else
					analogUnsigned = analog[0];
				analogVolt = ((double)analogUnsigned/256)*5.0;   //�����ѹֵ
			}
			//�������Ļ
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
					show = "��������" + digitalString + "  " + "ģ������" + analogString + "\n";
					chart.setNewData(analogVolt);
				}
				else
				{
					analogString = new String(Integer.toHexString(analog[0]).substring(6));
					show = "��������" + digitalString + "  " + "ģ������" + analogString + "\n";
					chart.setNewData(analogVolt);   //�з��Ÿ���תΪ�޷�����
				}
				textArea.append(show);
				Date day=new Date();    
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //��ȡʱ��
				if(outPut != null)
					outPut.addData(df.format(day), analogString, digitalString);
				analogHalf[0] = (byte) (analogUnsigned/2);    //���ͼ��������
				serial.getOutputStream().write(analogHalf);
			}
			if(analogVolt > threshold)   //���������ֵ������
			{	
				if(haveMessageBox == false)
				{
					JOptionPane.showMessageDialog(null, "������ֵ!");
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
			JOptionPane.showMessageDialog(null, "���ݴ����г���");
		
	}
	public void setOutput(OutPut outPut)
	{
		this.outPut = outPut;
	}
}
