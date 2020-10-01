import java.awt.Font;

import org.jfree.chart.ChartFactory; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.axis.ValueAxis; 
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond; 
import org.jfree.data.time.TimeSeries; 
import org.jfree.data.time.TimeSeriesCollection;

public class Chart extends ChartPanel implements Runnable{
	 private static TimeSeries timeSeries; 
	 private double newData;
	 public Chart(String chartContent, String title, String yaxisName)
	 { 
		 super(createChart(chartContent, title, yaxisName)); 
	 }
	 @SuppressWarnings("deprecation")
	 private static JFreeChart createChart(String chartContent, String title, String yaxisName) 
	 { //��һ�����������ߵ����֣��ڶ��������Ǳ��⣬��������������������
		 Font font=new Font("����",Font.BOLD,18);
		 timeSeries = new TimeSeries(title, Millisecond.class);  	//����ʱ��ͼ���� 
		 TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);    //�����ռ���
		 
		 //����ͼ�ζ���,��һ��������ͼ�����ڶ����Ǻ������������������������������ĸ������ݼ�
		 JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "ʱ��(��)", yaxisName, timeseriescollection, true, true, false);
		 jfreechart.getLegend().setItemFont(font);
		 jfreechart.getTitle().setFont(font);
		 XYPlot xyplot = jfreechart.getXYPlot();   //ͼ������
		 ValueAxis valueaxis = xyplot.getDomainAxis();  //�������
		 valueaxis.setLabelFont(font);
		 valueaxis.setAutoRange(true);  //�Զ��������������ݷ�Χ   
		 valueaxis.setFixedAutoRange(30000D);  //������ʾ30s   

		 valueaxis = xyplot.getRangeAxis();    //�������
		 valueaxis.setLabelFont(font);
		 return jfreechart; 
	 } 
	 public void run() 
	 {
		while(Thread.currentThread().isInterrupted() == false)
		{
			timeSeries.add(new Millisecond(), newData);   //�Ե�ѹֵ��ʾ
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {return;} 
		}	
	 } 
	 public void setNewData(double data)
	 {
		 newData = data;
	 }
}
