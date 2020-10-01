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
	 { //第一个参数是曲线的名字，第二个参数是标题，第三个参数是纵轴名字
		 Font font=new Font("宋体",Font.BOLD,18);
		 timeSeries = new TimeSeries(title, Millisecond.class);  	//创建时序图对象 
		 TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);    //数据收集器
		 
		 //整个图形对象,第一个参数是图名，第二个是横坐标名，第三个是纵坐标名，第四个是数据集
		 JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "时间(秒)", yaxisName, timeseriescollection, true, true, false);
		 jfreechart.getLegend().setItemFont(font);
		 jfreechart.getTitle().setFont(font);
		 XYPlot xyplot = jfreechart.getXYPlot();   //图的坐标
		 ValueAxis valueaxis = xyplot.getDomainAxis();  //横轴对象
		 valueaxis.setLabelFont(font);
		 valueaxis.setAutoRange(true);  //自动设置数据轴数据范围   
		 valueaxis.setFixedAutoRange(30000D);  //横轴显示30s   

		 valueaxis = xyplot.getRangeAxis();    //纵轴对象
		 valueaxis.setLabelFont(font);
		 return jfreechart; 
	 } 
	 public void run() 
	 {
		while(Thread.currentThread().isInterrupted() == false)
		{
			timeSeries.add(new Millisecond(), newData);   //以电压值显示
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
