package com.example.pass.job.statistics;

import com.example.pass.repository.booking.BookingEntity;
import com.example.pass.repository.booking.BookingRepository;
import com.example.pass.repository.statistics.StatisticsEntity;
import com.example.pass.repository.statistics.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration

public class MakeStatisticsJobConfig {
    private final BookingRepository bookingRepository;

    private static final int CHUNK_SIZE = 10;

    private final JobBuilderFactory jobBuilder;
    private final StepBuilderFactory stepBuilder;
    private final EntityManagerFactory entityManager;
    private final StatisticsRepository statisticsRepository;
    private final MakeDailyStatisticsTasklet dailyStatisticsTasklet;
    private final MakeWeeklyStatisticsTasklet weeklyStatisticsTasklet;

    @Bean
    public Job makeStatisticsJob() {
        Flow addStatisticsFlow = new FlowBuilder<Flow>("addStatisticsFlow")
                .start(addStatisticsStep())
                .build();

        Flow makeDailyStatisticsFlow = new FlowBuilder<Flow>("makeDailyStatisticsFlow")
                .start(makeDailyStatisticsStep())
                .build();

        Flow makeWeeklyStatisticsFlow = new FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
                .start(makeWeeklyStatisticsStep())
                .build();

        Flow parallelMakeStatisticsFlow = new FlowBuilder<Flow>("parallelMakeStatisticsFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
                .build();

        return this.jobBuilder.get("makeStatisticsJob")
                .start(addStatisticsFlow)
                .next(parallelMakeStatisticsFlow)
                .build()
                .build();
    }

    @Bean
    public Step addStatisticsStep() {
        return this.stepBuilder.get("addStatisticsStep")
                .<BookingEntity, BookingEntity>chunk(CHUNK_SIZE)
                .reader(addStatisticsItemReader(null, null))
                .writer(addStatisticsItemWriter())
                .build();

    }

    @Bean
    @StepScope
    public JpaCursorItemReader<BookingEntity> addStatisticsItemReader(@Value("#{jobParameters[from]}") String fromString,
                                                                      @Value("#{jobParameters[to]}") String toString) {
        final LocalDateTime from = LocalDateTime.parse(fromString);
        final LocalDateTime to = LocalDateTime.parse(toString);

        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("addStatisticsItemReader")
                .entityManagerFactory(entityManager)
                .queryString("select b from BookingEntity b where b.endedAt between :from and :to")
                .parameterValues(Map.of("from", from, "to", to))
                .build();

    }

    @Bean
    public ItemWriter<BookingEntity> addStatisticsItemWriter() {
        return bookingEntities -> {
            Map<LocalDateTime, StatisticsEntity> statisticsEntityMap = new LinkedHashMap<>();

            for (BookingEntity bookingEntity : bookingEntities) {
                final LocalDateTime statisticsAt = bookingEntity.getStatisticsAt();
                StatisticsEntity statisticsEntity = statisticsEntityMap.get(statisticsAt);

                if (statisticsEntity == null) {
                    statisticsEntityMap.put(statisticsAt, StatisticsEntity.create(bookingEntity));
                } else {
                    statisticsEntity.add(bookingEntity);
                }
            }
            final List<StatisticsEntity> statisticsEntities = new ArrayList<>(statisticsEntityMap.values());
            statisticsRepository.saveAll(statisticsEntities);
        };
    }

    @Bean
    public Step makeDailyStatisticsStep() {
        return this.stepBuilder.get("makeDailyStatisticsStep")
                .tasklet(dailyStatisticsTasklet)
                .build();
    }

    @Bean
    public Step makeWeeklyStatisticsStep() {
        return this.stepBuilder.get("makeWeeklyStatisticsStep")
                .tasklet(weeklyStatisticsTasklet)
                .build();

    }
}
