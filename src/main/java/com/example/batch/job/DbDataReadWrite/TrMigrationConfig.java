package com.example.batch.job.DbDataReadWrite;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import com.example.batch.core.domain.accounts.AccountRepository;
import com.example.batch.core.domain.accounts.Accounts;
import com.example.batch.core.domain.orders.Orders;
import com.example.batch.core.domain.orders.OrdersRepository;

import lombok.RequiredArgsConstructor;

/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job trMigrationJob(Step trMigrationStep) {
		return jobBuilderFactory.get("trMigrationJob")
				.incrementer(new RunIdIncrementer())
				.start(trMigrationStep)
				.build();
	}

	/*
	 * 1 데이터를 읽어와서 - reader(trOrderReader)
	 * 2 처리해 주고 - processor(trOrderProcessor)
	 * 3 데이터를 저장한다 - writer(trOrderWriter)
	 */
	@JobScope
	@Bean
	public Step trMigrationStep(
			ItemReader trOrderReader,
			ItemProcessor trOrderProcessor,
			ItemWriter trOrderWriter) {
		return stepBuilderFactory.get("trMigrationStep")
				.<Orders, Accounts>chunk(5)
				.reader(trOrderReader)
				.processor(trOrderProcessor)
				.writer(trOrderWriter)
				.build();
	}

	@StepScope
	@Bean
	public RepositoryItemWriter<Accounts> trOrderWriter() {
		return new RepositoryItemWriterBuilder<Accounts>()
				.repository(accountRepository)
				.methodName("save")
				.build();
	}

	// Orders 객체를 받아서 Accounts 객체로 변환할 수 있도록 processor 작성
	@StepScope
	@Bean
	public ItemProcessor<Orders, Accounts> trOrderProcessor() {
		return new ItemProcessor<Orders, Accounts>() {
			@Override
			public Accounts process(Orders item) throws Exception {
				return new Accounts(item);
			}
		};
	}

	@StepScope
	@Bean
	public RepositoryItemReader<Orders> trOrderReader() {
		return new RepositoryItemReaderBuilder<Orders>()
				.name("trOrderReader")
				.repository(ordersRepository)
				.methodName("findAll")
				.pageSize(5)
				.arguments(Arrays.asList())
				.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
				.build();
	}

}
