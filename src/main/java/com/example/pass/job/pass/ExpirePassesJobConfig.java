package com.example.pass.job.pass;

import com.example.pass.repository.pass.PassEntity;
import com.example.pass.repository.pass.PassStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ExpirePassesJobConfig {
    private final int CHUNK_SIZE = 5;

    // @EnableBatchProcessing 을 통해 Bean 으로 제공된 JobBuilderFactory, StepBuilderFactory 사용
    private final JobBuilderFactory jobBuilder;
    private final StepBuilderFactory stepBuilder;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job expirePassesJob() {
        return this.jobBuilder.get("expirePassesJob")
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return  this.stepBuilder.get("expirePassesStep")
                .<PassEntity, PassEntity> chunk(CHUNK_SIZE)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    /**
     * JpaCursorItemReader : JpaPagingItemReader 만 지원하다가 Spring 4.3에서 추가
     * 페이징 기법보다 높은 성능으로, 데이터 변겨에 무관한 무결성 조회가 가능
     */

    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT PASS " +
                        "     FROM PassEntity PASS " +
                        "     WHERE PASS.status = :status " +
                        "       AND PASS.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    /**
     * JPaItemWriter : JPA의 영속성 관리를 위해 EntityManager를 필수로 설정해줘야 한다.
     */
    @Bean
    public ItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
