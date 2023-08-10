package com.example.pass.job.statistics;

import com.example.pass.repository.statistics.AggregatedStatistics;
import com.example.pass.repository.statistics.StatisticsRepository;
import com.example.pass.util.CsvFileType;
import com.example.pass.util.CustomCSVWriter;
import com.example.pass.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@StepScope
@Component
public class MakeDailyStatisticsTasklet implements Tasklet {

    @Value("#{jobParameter[from]}")
    private String fromString;

    @Value("#{jobParameter[to]}")
    private String toString;

    private final StatisticsRepository statisticsRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalDateTime from = LocalDateTime.parse(fromString);
        final LocalDateTime to = LocalDateTime.parse(toString);

        final List<AggregatedStatistics> statistics = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

        List<String[]> data = new ArrayList<>();

        data.add(CustomCSVWriter.getCsvHeader("statisticsAt"));

        for (AggregatedStatistics statistic : statistics) {
            data.add(new String[] {
                    LocalDateTimeUtils.format(statistic.getStatisticsAt()),
                    String.valueOf(statistic.getAllCount()),
                    String.valueOf(statistic.getAttendedCount()),
                    String.valueOf(statistic.getCancelledCount())
            });
        }

        CustomCSVWriter.write(CsvFileType.DAILY, from, data);
        return RepeatStatus.FINISHED;
    }
}
