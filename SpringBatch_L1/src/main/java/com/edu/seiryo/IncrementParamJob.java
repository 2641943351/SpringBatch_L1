package com.edu.seiryo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

//@SpringBootApplication
//@EnableBatchProcessing
public class IncrementParamJob {
	
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // 获取run.id，即自动生成的id
            	Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
                System.out.println("params---run.id:" + parameters.get("run.id"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step step1(){
        return  stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("incr-params-job")
                .start(step1())
                .incrementer(new RunIdIncrementer())  //参数增量器(run.id自增)
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(IncrementParamJob.class, args);
    }
}
