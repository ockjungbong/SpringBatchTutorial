package com.example.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	// 1. Job 에서 Step 호출
	@Bean
	public Job HelloWorldJob() {
		return jobBuilderFactory.get("helloWorldJob")
				.incrementer(new RunIdIncrementer())
				.start(helloWorldStep())
				.build();
	}

	// 2. Step 에서 Tasklet 호출
	@JobScope
	@Bean
	public Step helloWorldStep() {
		return stepBuilderFactory.get("helloWorldStep")
				.tasklet(helloWorldTasklet())
				.build();
	}

	// 3. Tasklet 구현
	@StepScope
	@Bean
	public Tasklet helloWorldTasklet() {
		return new Tasklet() {

			@Override
			@Nullable
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Hello World Spring Batch");
				return RepeatStatus.FINISHED;
			}

		};
	}

}
