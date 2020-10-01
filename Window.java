import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jxl.write.WriteException;

public class Window extends JFrame implements ActionListener {
	JPanel upPanel;
	JPanel downPanel;
	JScrollPane scrollPane;
	JButton sendButton;
	JButton analog1Button;
	JButton analog2Button;
	JButton stopButton;
	JButton saveButton;
	JTextField sendArea;
	JTextField thresholdArea;
	JTextArea recArea;
	JFileChooser chooser;
	Serial serial;     //���ڷ��ͽ��ܵĴ��ڶ���
	Thread t1,t2;
	OutPut outPut = null;
	ReceiveSend rSend;
	public Window() {
		super("��λ�����ݲɼ�ϵͳ");
		this.setSize(800, 500);
		this.setLocation(600, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		upPanel = new JPanel();
		upPanel.setLayout(new BorderLayout());
		this.add(upPanel, BorderLayout.CENTER);
		downPanel = new JPanel();
		downPanel.setLayout(new FlowLayout());
		this.add(downPanel, BorderLayout.SOUTH);
		recArea = new JTextArea();
		scrollPane = new JScrollPane(recArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		upPanel.add(scrollPane);
		sendArea = new JTextField(20);
		thresholdArea = new JTextField("�ڴ������ѹ��ֵ", 8);
		downPanel.add(sendArea);
		downPanel.add(thresholdArea);
		sendButton = new JButton("����");
		analog1Button = new JButton("ģ����1");
		analog2Button = new JButton("ģ����2");
		stopButton = new JButton("ͣ��");
		saveButton = new JButton("�����ļ�");
		downPanel.add(sendButton);
		downPanel.add(analog1Button);
		downPanel.add(analog2Button);
		downPanel.add(stopButton);
		downPanel.add(saveButton);
		sendButton.addActionListener(this);
		analog1Button.addActionListener(this);
		analog2Button.addActionListener(this);
		stopButton.addActionListener(this);
		saveButton.addActionListener(this);
		this.setVisible(true);
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("����"))
		{
			String letter = sendArea.getText();
			try {
				serial.getOutputStream().write(letter.getBytes("ascii"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(thresholdArea.getText().equals("�ڴ������ѹ��ֵ") == false)
				rSend.setThreshold(new Double(this.thresholdArea.getText()));
				
		}
		if(e.getActionCommand().equals("ģ����1"))
		{
			byte[] choose1 = new byte[1];
			choose1[0] = (byte)0xfb;
			try {
				serial.getOutputStream().write(choose1);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(e.getActionCommand().equals("ģ����2"))
		{
			byte[] choose2 = new byte[1];
			choose2[0] = (byte)0xfc;
			try {
				serial.getOutputStream().write(choose2);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(e.getActionCommand().equals("ͣ��"))
		{
			byte[] stop = new byte[1];
			stop[0] = (byte) 0xfe;
			try {
				serial.getOutputStream().write(stop);
			} catch (IOException e1){
				e1.printStackTrace();
			}
			t1.interrupt();
			t2.interrupt();
			if(outPut != null)
				try {
					outPut.finalWrite();
				} catch (WriteException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		if(e.getActionCommand().equals("�����ļ�"))
		{
			chooser.showOpenDialog(null);
			outPut = new OutPut(chooser.getSelectedFile().getAbsolutePath());
			rSend.setOutput(outPut);
		}
	}
	public void setSerialPort(Serial s)
	{
		this.serial = s;
	}
	public void setThread(Thread t1,Thread t2)
	{
		this.t1 = t1;
		this.t2 = t2;
	}
	
}
