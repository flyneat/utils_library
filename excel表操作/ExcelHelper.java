package com.utils;


import com.nlutils.util.LoggerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel工具
 *
 * @author jianshengd
 * @date 2018/3/7
 */
public class ExcelHelper {
    private Workbook mWb;
    private OutputStream mOs;
    private InputStream mIs;
    private Sheet mSheet;

    /**
     * 创建Excel
     *
     * @param os excel文件的输出流
     */
    public ExcelHelper(OutputStream os) {
        this.mOs = os;
    }

    /**
     * 读取Excel
     *
     * @param is excel文件的输入流
     */
    public ExcelHelper(InputStream is) {
        this.mIs = is;
    }

    /**
     * 创建表
     *
     * @param sheetName 表名
     * @return 是否成功
     */
    public boolean createSheet(String sheetName) {
        try {
            if (mWb == null) {
                //这里新建了一个exccel 2003的文件,后缀是xls
                mWb = new HSSFWorkbook();
                //Workbook wb = new XSSFWorkbook();  这里是一个excel2007的文件，相应的输出流后缀应该是xlsx

            }
            LoggerUtils.e("create sheet:" + sheetName);
            mSheet = mWb.createSheet(sheetName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取表格
     *
     * @param index 表号
     * @return 是否成功
     */
    public boolean readSheet(int index) {
        try {
            mWb = new HSSFWorkbook(mIs);
            mSheet = mWb.getSheetAt(index);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 单元格中写入数据
     *
     * @param nRow  行，0开始
     * @param nCell 列 ，0开始
     * @param data  数据
     * @return 是否成功
     */
    public boolean write(int nRow, int nCell, String data) {
        if (mWb == null || mSheet == null) {
            return false;
        }
        LoggerUtils.e("行:" + nRow + " 列:" + nCell + " 内容:" + data);
        Row row = mSheet.getRow(nRow);
        if (row == null) {
            row = mSheet.createRow(nRow);
        }
        Cell cell = row.createCell(nCell);
        cell.setCellValue(data);

        return true;
    }

    /**
     * 读取数据
     *
     * @param nRow  行
     * @param nCell 列
     * @return 读取到的数据
     */
    public String read(int nRow, int nCell) {
        if (mWb == null || mSheet == null) {
            return null;
        }
        Row row = mSheet.getRow(nRow);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(nCell);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue();
    }

    /**
     * 操作完成
     */
    public void finish() {
        try {
            if (mIs != null) {
                mIs.close();
            }
            if (mOs != null) {
                mOs.close();
                mWb.write(mOs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
