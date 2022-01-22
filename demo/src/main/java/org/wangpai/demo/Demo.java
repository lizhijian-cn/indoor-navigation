package org.wangpai.demo;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

enum NodeEnum {
    ELEVATOR,
    STAIRWAY,
    CROSSROAD,
    GATE
}

@Data
class Node {
    int nodeIndex;
    NodeEnum type;
    double x;
    double y;
    List<String> adjacentNode;
}

public class Demo {
    private static Logger logger = LoggerFactory.getLogger(Demo.class);
    final static String elevator = "电梯", stairway = "楼梯", crossroad = "路口", gate = "";

    void run() {
        Map<String, Node> NodeMap = new HashMap<>();
        String[] floors = {"一", "二"};

        for (String floor : floors) {
            String filename = floor + "楼关键点像素" + ".xlsx";
            InputStream file = getClass().getClassLoader().getResourceAsStream(filename);
            Workbook workbook;
            try {
                workbook = new XSSFWorkbook(file);
            } catch (IOException e) {
                logger.error("failed to load {}", filename);
                continue;
            }
            for (Iterator<Sheet> it = workbook.sheetIterator(); it.hasNext(); ) {
                Sheet sheet = it.next();
                String sheetName = sheet.getSheetName();
                switch (sheetName) {
                    case elevator:
                        for (Row row : sheet) {
                            if (row.getFirstCellNum() != 0) {
                                logger.warn("incorrect data in {}, {} {} [{}, {}]", filename, sheetName, row.getRowNum(), row.getFirstCellNum(), row.getLastCellNum());
                                continue;
                            }
                            double x = row.getCell(0).getNumericCellValue(), y = row.getCell(1).getNumericCellValue();
                            for (int i = 3; i < row.getLastCellNum(); i++) {
                                Cell cell = row.getCell(i);
                                String key;
                                if (cell.getCellType().equals(CellType.NUMERIC)) {
                                    key =  cell.getNumericCellValue();
                                }
                                System.out.println(CellType.BLANK);
                            }
                        }
                        break;
                    case stairway:
                        break;
                    case crossroad:
                        break;
                    case gate:
                        break;
                    default:
                        logger.error("unknown sheet name {} in {}", sheet.getSheetName(), filename);
                }

            }
        }
    }
}
