package com.faizul.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

/**
 * Regression guard: TIDAK boleh ada bean `RestClient.Builder` global yang
 * @LoadBalanced. Bean semacam itu ikut dipakai Eureka client sebagai transport
 * HTTP-nya, sehingga Eureka mencoba mem-load-balance `localhost:8761` sebagai
 * nama service dan gagal registrasi ("No instances available for localhost").
 *
 * Load-balancing ke service lain harus dilakukan eksplisit lewat LoadBalancerClient,
 * bukan lewat bean builder global.
 */
@SpringBootTest
class LoadBalancerIsolationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void tidakAdaGlobalLoadBalancedRestClientBuilder() {
		for (String name : context.getBeanNamesForType(RestClient.Builder.class)) {
			LoadBalanced annotation = context.findAnnotationOnBean(name, LoadBalanced.class);
			assertThat(annotation)
					.as("Bean '%s' tidak boleh @LoadBalanced global (merusak Eureka client)", name)
					.isNull();
		}
	}
}
