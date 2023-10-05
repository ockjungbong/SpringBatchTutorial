package com.example.batch.job.validatedParam;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import com.example.batch.job.validatedParam.validator.FileParamValidator;

import lombok.RequiredArgsConstructor;

/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/*
	 * 배치 호출 구조
	 * 1. Job 에서 Step 호출
	 * 2. Step 에서 Tasklet 호출
	 * 3. Tasklet 구현
	 */
	@Bean
	public Job validatedParamJob(Step validatedParamStep) {
		return jobBuilderFactory.get("validatedParamJob")
				.incrementer(new RunIdIncrementer())
				// .validator(new FileParamValidator())
				// validator가 여러개일 경우는 multipleValidator() 호출
				.validator(multipleValidator())
				.start(validatedParamStep)
				.build();
	}

	private CompositeJobParametersValidator multipleValidator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		// validator가 여러 개일 경우는 열거형으로 넣어준다.
		// validator.setValidators(Arrays.asList(new FileParamValidator(), , , ));
		validator.setValidators(Arrays.asList(new FileParamValidator()));

		return validator;

	}

	@JobScope
	@Bean
	public Step validatedParamStep(Tasklet validatedParamTasklet) {
		return stepBuilderFactory.get("validatedParamStep")
				.tasklet(validatedParamTasklet)
				.build();
	}

	// 배치를 수행할 때 특정 파일 네임을 파라미터로 받아서 파일을 읽어올 수 있다.
	@StepScope
	@Bean
	public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
		return new Tasklet() {

			@Override
			@Nullable
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("fileName >> " + fileName);
				System.out.println("validated Param Tasklet");
				return RepeatStatus.FINISHED;
			}

		};
	}

}
