package com.example.batch.job.TestChunkJob;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TestChunkJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job testTaskChunkJob() {
        return jobBuilderFactory.get("testTaskChunkJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskStep1())
                .build();
    }

    @JobScope
    @Bean
    public Step taskStep1() {
        return stepBuilderFactory.get("taskStep1")
                .tasklet(tasklet())
                .build();
    }

    @StepScope
    @Bean
    public Tasklet tasklet() { // tasklet으로 모두 처리
        return (contribution, chunkContext) -> {
            List<String> items = getItems();

            log.info("items : " + items.toString());

            return RepeatStatus.FINISHED;
        };
    }

    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + " test!");
        }

        return items;
    }

}
