import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class OutPut {
	private WritableWorkbook book;   //excel�ļ�
	private WritableSheet sheet;     //������
	private int row = 1;
	public OutPut(String path)
	{
		try {
			book = Workbook.createWorkbook(new File(path + "\\data.xls"));
			sheet = book.createSheet("����", 0);
			jxl.write.Label title1 = new jxl.write.Label(0,0,"ʱ��");
			jxl.write.Label title2 = new jxl.write.Label(1,0,"ģ����");
			jxl.write.Label title3 = new jxl.write.Label(2,0,"������");
			sheet.addCell(title1);
			sheet.addCell(title2);
			sheet.addCell(title3);
		} catch (IOException | WriteException e) {
			e.printStackTrace();
		}
		
	}
	public void addData(String time, String analog, String digital)
	{
		jxl.write.Label nowTime = new jxl.write.Label(0, row, time);
		jxl.write.Label nowAnalog = new jxl.write.Label(1,row,analog);
		jxl.write.Label nowDigital = new jxl.write.Label(2,row++,digital);
		try{
			sheet.addCell(nowTime);
			sheet.addCell(nowAnalog);
			sheet.addCell(nowDigital);
		}catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public void finalWrite() throws IOException, WriteException
	{
		book.write();
		book.close();
	}
	public WritableWorkbook getWritableWorkbook()
	{
		return book;
	}
}
