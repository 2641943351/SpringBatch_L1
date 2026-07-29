package com.edu.seiryo;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/*
 *  开启Spring Batch功能，自动创建Batch相关处理类，
 *  加入spring管理，这样下面就能依赖注入
 */
// 声明为SpringBoot启动类
//@EnableBatchProcessing
//@SpringBootApplication
public class HelloJob {

	// 作业调度器，即job启动器
	@Autowired
	private JobLauncher jobLauncher;
	
	// job作业的工厂对象，用来创建Job作业
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	// Step步骤的工厂对象，用来创建Step步骤
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	
	
	// 此方法返回  job作业  对象，给  JobLauncher作业调度器  调用
	@Bean
	public Job job1() {
		/*
		 * 使用jobBuilderFactory工厂对象，创建Job
		 * 1.命名
		 * 		.get("Job对象名") ，用于命名Job对象
		 * 
		 * 2.导入步骤
		 * 		.start()组装job中该有的step步骤
		 * 
		 * 3.创建
		 * 		.build()创建Job对象
		 */
		return jobBuilderFactory.get("job2")
				.start(step1())
				.build();
	}
	

	// 此方法返回  Step步骤  对象，用于 组装 Job作业 对象
	@Bean
	public Step step1() {
		/*
		 * 通过stepBuilderFactory工厂对象，创建Step
		 * 1.命名
		 * 		.get()
		 * 2.导入动作
		 *  	tasklet:简单版的 动作 ，这里也能写完整版的3个动作，导入、处理、导出
		 * 3.创建
		 * 		.build()
		 *  
		 */
		return stepBuilderFactory.get("step2")
				.tasklet(tasklet1())
				.build();
	}
	
	// 此方法返回 tasklet 动作对象，给步骤对象，@Bean将返回的对象创建成Bean交给spring管理
	@Bean
	public Tasklet tasklet1() {
		// 创建 Tasklet 动作对象
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Hello SpringBatch");
				// 表示执行完毕
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	/**
	 * 解决Oracle数据库运行失败问题
	 * 自定义Spring Batch配置
	 * 
	 * 作用：
	 * 修改JobRepository创建Job实例时的事务隔离级别。
	 * 
	 * Oracle默认不支持Spring Batch的ISOLATION_SERIALIZABLE，
	 * 会导致ORA-08177错误，因此调整为READ_COMMITTED。
	 */
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
	/*
	 *  简单内存版，已经执行过的job还能执行（缓存会清除）
	 *  数据库版，已经执行的job不能再执行（数据库已保存）
	 *  	要更换job名称
	 */
	public static void main(String[] args) {
		SpringApplication.run(HelloJob.class, args);
	}
}
