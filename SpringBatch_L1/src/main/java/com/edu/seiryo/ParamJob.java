package com.edu.seiryo;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//@EnableBatchProcessing
//@SpringBootApplication
public class ParamJob {
	
	// job作业的工厂对象，用来创建Job作业
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	// Step步骤的工厂对象，用来创建Step步骤
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
//	@Bean
//	public Tasklet tasklet1() {
//		return new Tasklet() {
//			@Override
//			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//				System.out.println("param SpringBatch");
//				return RepeatStatus.FINISHED;
//			}
//		};
//	}
	
	
//	@Bean
//	public Tasklet tasklet1(){
//	    return new Tasklet() {
//	        @Override
//	        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//	            // 使用chunkContext对象，获取 启动参数
//	        	Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
//	            System.out.println("params---name:" + parameters.get("name"));
//	            return RepeatStatus.FINISHED;
//	        }
//	    };
//	}
	
	@StepScope
	@Bean
	public Tasklet tasklet1(@Value("#{jobParameters['name']}")String name){
	    return new Tasklet() {
	        @Override
	        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
	            System.out.println("param SpringBatch:" + name);
	            return RepeatStatus.FINISHED;
	        }
	    };
	}
	
	// 此方法返回  Step步骤  对象，用于 组装 Job作业 对象
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step9")
				.tasklet(tasklet1(null))
				.build();
	}
	
	// 此方法用于创建name参数校验器
	@Bean
	public NameParamValidator validator () {
		return new NameParamValidator();
	}
	
	// 此方法返回  job作业  对象，给  JobLauncher作业调度器  调用
	@Bean
	public Job job1() {
		return jobBuilderFactory.get("job9")
				.start(step1())
				// 调用参数校验器
				.validator(validator())
				.build();
	}
	

	@Bean
    public BatchConfigurer batchConfigurer(DataSource dataSource) {
        return new DefaultBatchConfigurer(dataSource) {

            @Override
            protected JobRepository createJobRepository() throws Exception {
                JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();

                factory.setDataSource(dataSource);
                factory.setTransactionManager(getTransactionManager());
                factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");

                factory.afterPropertiesSet();

                return factory.getObject();
            }
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(ParamJob.class, args);
	}
}
