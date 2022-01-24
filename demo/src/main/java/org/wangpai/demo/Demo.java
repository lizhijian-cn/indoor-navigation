package org.wangpai.demo;

import lombok.Builder;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangpai.navigation.core.algo.astar.AStar;
import org.wangpai.navigation.core.edge.Edge;
import org.wangpai.navigation.core.graph.Graph;
import org.wangpai.navigation.core.vertex.Vertex;
import org.wangpai.navigation.core.vertex.VertexKey;
import org.wangpai.navigation.core.vertex.VertexTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

enum NodeEnum {
    ELEVATOR, STAIRWAY, CROSSROAD, GATE
}

@Builder
class Record {
    int floor; // starts at 1
    String sheetName;
    double x, y;
    List<Integer> crossroadNeighbours;
    List<String> otherFloorNeighbours;
    int rowNum;
    Vertex vertex;

    @Override
    public String toString() {
        return "Record{" + "floor=" + floor + ", sheetName='" + sheetName + '\'' + ", x=" + x +
                ", y=" + y + ", crossroadNeighbours=" + crossroadNeighbours + ", " +
                "otherFloorNeighbours=" + otherFloorNeighbours + '}';
    }
}

public class Demo {
    final static String elevator = "电梯", stairway = "楼梯", crossroad = "路口", gate = "室外门";
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    private VertexTypeEnum sheetName2VertexType(String sheetName) {
        switch (sheetName) {
            case elevator:
                return VertexTypeEnum.ELEVATOR;
            case stairway:
                return VertexTypeEnum.STAIRWAY;
            case crossroad:
                return VertexTypeEnum.CROSSROAD;
            case gate:
                return VertexTypeEnum.GATE;
            default:
                throw new RuntimeException();
        }
    }

    private String vertexRef(int floor, VertexTypeEnum type, int rowNum) {
        switch (type) {
            case ELEVATOR:
                return String.format("%d e %d", floor, rowNum);
            case STAIRWAY:
                return String.format("%d s %d", floor, rowNum);
            case CROSSROAD:
                return String.format("%d %d", floor, rowNum);
            case GATE:
                return String.format("%d d %d", floor, rowNum);
        }
        return "";
    }

    void run() {
        String[] prefix = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一"};
        int n = prefix.length;
        String[] filenames = new String[n];
        int[] floors = new int[n];
        for (int i = 0; i < n; i++) {
            filenames[i] = prefix[i] + "楼关键点像素" + ".xlsx";
            floors[i] = i + 1;
        }

        List<Record> records = readRecordFromXlsx(filenames, floors);

        List<Vertex> vertexList = new ArrayList<>();
        Map<String, VertexKey> refMap = new HashMap<>();
        int[] idxes = new int[floors.length + 1]; // may be wrong
        for (Record record : records) {
            VertexKey key = VertexKey.builder()
                    .floor(record.floor)
                    .idx(idxes[record.floor]++)
                    .build();
            VertexTypeEnum type;
            try {
                type = sheetName2VertexType(record.sheetName);
            } catch (Exception e) {
                logger.error("unknown sheet name {} in floor {}", record.sheetName, record.floor);
                continue;
            }
            Vertex vertex = Vertex.builder()
                    .key(key)
                    .x(record.x)
                    .y(record.y)
                    .type(type)
                    .typeIdx(record.rowNum)
                    .build();

            String ref = vertexRef(key.floor, type, record.rowNum);
            refMap.put(ref, key);

            record.vertex = vertex;
            vertexList.add(vertex);
        }

        Graph graph = new Graph(vertexList);
        List<Edge> edgeList = new ArrayList<>();
        for (Record record : records) {
            for (String ref : record.otherFloorNeighbours) {
                VertexKey to = refMap.get(ref);
                if (to == null) {
                    logger.error("{} {} {} ref {} could not find vertex", record.floor,
                            record.sheetName
                            , record.rowNum, ref);
                    continue;
                }
                graph.addEdge(record.vertex.key, to);
                graph.addEdge(to, record.vertex.key);
            }
            for (int i : record.crossroadNeighbours) {
                String ref = record.floor + " " + i;
                VertexKey to = refMap.get(ref);
                if (to == null) {
                    logger.error("{} {} {} ref {} could not find vertex", record.floor,
                            record.sheetName
                            , record.rowNum, ref);
                    continue;
                }
                graph.addEdge(record.vertex.key, to);
                graph.addEdge(to, record.vertex.key);
            }
        }

        // now we have generated a graph
        AStar aStar = new AStar(graph);
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.equals("quit")) {
                break;
            }

            String[] in = line.split(" ");
            int floorSrc = Integer.parseInt(in[0]);
            double xSrc = Double.parseDouble(in[1]), ySrc = Double.parseDouble(in[2]);
            int floorDst = Integer.parseInt(in[3]);
            double xDst = Double.parseDouble(in[4]), yDst = Double.parseDouble(in[5]);
            // TODO don't print result in AStar class directly
            aStar.search(floorSrc, xSrc, ySrc, floorDst, xDst, yDst);
        }
    }

    // record 会保存中间结果，是侵入式的
    List<Record> readRecordFromXlsx(String[] filenames, int[] floors) {
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < filenames.length; i++) {
            String filename = filenames[i];
            InputStream file = getClass().getClassLoader()
                    .getResourceAsStream(filename);
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
                for (Row row : sheet) {
                    if (row.getFirstCellNum() != 0) {
                        continue;
                    }
                    if (row.getLastCellNum() == 0) {
                        continue;
                    }
                    if (row.getLastCellNum() < 3) {
                        logger.warn("data missed in {} {}, row {}", filename, sheetName,
                                row.getRowNum());
                        continue;
                    }
                    List<Integer> crossroadNeighbours = new ArrayList<>();
                    List<String> otherFloorNeighbours = new ArrayList<>();
                    double x = row.getCell(0)
                            .getNumericCellValue(), y = row.getCell(1)
                            .getNumericCellValue();
                    for (int j = 3; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell.getCellType()
                                .equals(CellType.NUMERIC)) {
                            crossroadNeighbours.add((int) cell.getNumericCellValue());
                        } else {
                            String value = cell.getStringCellValue();
                            if (Strings.isBlank(value)) {
                                continue;
                            }
                            otherFloorNeighbours.add(value);
                        }
                    }
                    Record record = Record.builder()
                            .floor(floors[i])
                            .sheetName(sheetName)
                            .x(x)
                            .y(y)
                            .crossroadNeighbours(crossroadNeighbours)
                            .otherFloorNeighbours(otherFloorNeighbours)
                            .rowNum(row.getRowNum() + 1)
                            .build();
                    records.add(record);
                }
            }
        }
        return records;
    }
}
