package com.example.batch.job.jobListener;

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

/**
 * desc: 배치 실행 전 로그 추가 리스너
 * run: --spring.batch.job.names=jobListenerJob
 */
@Configuration
@RequiredArgsConstructor
public class JobListenerJobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	// 1. Job 에서 Step 호출
	@Bean
	public Job jobListenerJob() {
		return jobBuilderFactory.get("jobListenerJob")
				.incrementer(new RunIdIncrementer())
				.listener(new JobLoggerListener())
				.start(jobListenerStep())
				.build();
	}

	// 2. Step 에서 Tasklet 호출
	@JobScope
	@Bean
	public Step jobListenerStep() {
		return stepBuilderFactory.get("jobListenerStep")
				.tasklet(jobListenerTasklet())
				.build();
	}

	// 3. Tasklet 구현
	@StepScope
	@Bean
	public Tasklet jobListenerTasklet() {
		return new Tasklet() {

			@Override
			@Nullable
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("jobListener Tasklet");
				return RepeatStatus.FINISHED;
				// fail 테스트
//				throw new Exception("Failed!!!!");
			}

		};
	}

}
