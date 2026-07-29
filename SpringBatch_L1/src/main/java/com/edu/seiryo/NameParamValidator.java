package com.edu.seiryo;


import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class NameParamValidator implements JobParametersValidator {

	/*
	 *  该方法没有返回值，判断是否成功的依据是此方法是否能成功运行完毕
	 *  成功则正确运行
	 *  失败需要手动抛出错误
	 */
	@Override
	public void validate(JobParameters parameters) throws JobParametersInvalidException {
		String name = parameters.getString("name");
		// StringUtils.hasText()用于检测string是否为空,空则false
		if (!StringUtils.hasText(name)) {
			throw new JobParametersInvalidException("name 参数不能为空");
		}
		
	}

}
