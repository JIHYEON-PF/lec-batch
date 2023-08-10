package com.example.pass.util;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class CustomCSVWriter {

    public static int write(final CsvFileType fileType, LocalDateTime csvDate, List<String[]> data) {
        int rows = 0;

        String fileName = createFileName(fileType, csvDate);

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(data);
            rows = data.size();
        } catch (Exception e) {
            log.error("Custom Csv Wrtier - write : CSV 파일 생성 실패, filename: {}", fileName);
        }

        return rows;
    }

    private static String createFileName(CsvFileType fileType, LocalDateTime csvDate) {
        StringBuilder fileName = new StringBuilder();

        switch (fileType) {
            case DAILY -> fileName.append("daily_statistics_");
            case WEEKLY -> fileName.append("weekly_statistics_");
        }

        fileName.append(LocalDateTimeUtils.format(csvDate, LocalDateTimeUtils.YYYY_MM_DD)).append(".csv");
        return fileName.toString();
    }

    public static String[] getCsvHeader(String type) {
        return new String[] {type, "allCount", "attendedCount", "cancelledCount"};
    }
}
