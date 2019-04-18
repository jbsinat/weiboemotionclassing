package com.biao.weiboemotionclassing;

import com.biao.weiboemotionclassing.filter.CrosFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeiboemotionclassingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeiboemotionclassingApplication.class, args);
	}

	/**
	 * 配置跨域访问的过滤器
	 * @return
	 */
	@Bean
	public FilterRegistrationBean registerFilter(){
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.addUrlPatterns("/*");
		bean.setFilter(new CrosFilter());
		return bean;
	}

}
