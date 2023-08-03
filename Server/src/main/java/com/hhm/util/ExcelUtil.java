package com.hhm.util;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelUtil {
    public static void appendToExcel(MultipartFile uploadedFile, String existingFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        // 读取上传的Excel文件内容
        InputStream inputStream = uploadedFile.getInputStream();
        Workbook uploadedWorkbook = new XSSFWorkbook(inputStream);

        // 打开现有的Excel文件
        FileInputStream fileInputStream = new FileInputStream(existingFilePath);
        Workbook existingWorkbook = new XSSFWorkbook(fileInputStream);

        // 追加上传的Sheet到现有的Excel文件中
        for (int sheetIndex = 0; sheetIndex < uploadedWorkbook.getNumberOfSheets(); sheetIndex++) {
            Sheet existingSheet;
            Sheet uploadedSheet = uploadedWorkbook.getSheetAt(sheetIndex);
            if (existingWorkbook.getSheet(uploadedSheet.getSheetName()) == null){
                existingSheet = existingWorkbook.createSheet(uploadedSheet.getSheetName()); // 创建一个新的Sheet
            }else {
                existingSheet = existingWorkbook.getSheet(uploadedSheet.getSheetName());
            }

            int newRowStartIndex = 0;
            int lastRowNum = existingSheet.getLastRowNum();
            if (lastRowNum > 0) {
                newRowStartIndex = lastRowNum + 1;
            }

            for (Row row : uploadedSheet) {
                Row newRow = existingSheet.createRow(newRowStartIndex++);
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell oldCell = row.getCell(i);
                    Cell newCell = newRow.createCell(i);
                    if (oldCell != null) {
                        // 复制单元格样式
                        CellStyle newCellStyle = existingWorkbook.createCellStyle();
                        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                        newCell.setCellStyle(newCellStyle);

                        // 复制单元格内容
                        switch (oldCell.getCellType()) {
                            case STRING:
                                newCell.setCellValue(oldCell.getStringCellValue());
                                break;
                            case NUMERIC:
                                newCell.setCellValue(oldCell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                newCell.setCellValue(oldCell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                newCell.setCellFormula(oldCell.getCellFormula());
                                break;
                            // 其他类型可以根据需要进行添加
                            default:
                                break;
                        }
                    }
                }
            }
        }

        // 保存更新后的Excel文件
        FileOutputStream fileOutputStream = new FileOutputStream(existingFilePath);
        existingWorkbook.write(fileOutputStream);
        fileOutputStream.close();

        // 关闭资源
        existingWorkbook.close();
        uploadedWorkbook.close();
    }
}
