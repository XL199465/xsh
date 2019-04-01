package cn.itcast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ParseExcel {
    public List<String[]> parseExcel(InputStream inputStream, String suffix, int startRow) throws IOException {
        // 1.定义Excel变量
        Workbook workbook = null;

        // 2.判断后缀名
        if ("xls".equals(suffix)) {
            // 2003
            workbook = new HSSFWorkbook(inputStream);
        } else if ("xlsx".equals(suffix)){
            // 2007
            workbook = new XSSFWorkbook(inputStream);
        } else {
            return null;
        }

        return null;
    }
}
