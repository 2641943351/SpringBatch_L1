package com.edu.seiryo;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

//作业状态--接口方式
public class JobStateListener  implements JobExecutionListener {
	
    //作业执行前
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.err.println("执行前-status：" + jobExecution.getStatus());
    }
    //作业执行后
    @Override
    public void afterJob(JobExecution jobExecution) {
        System.err.println("执行后-status：" + jobExecution.getStatus());
    }
    
}