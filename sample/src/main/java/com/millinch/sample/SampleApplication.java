package com.millinch.sample;

import com.millinch.mall.account.thrift.CalculatorService;
import info.developerblog.spring.thrift.annotation.ThriftClient;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.DispatcherType;
import java.util.List;

/**
 * This guy is lazy, nothing left.
 *
 * @author John Zhang
 */
@Controller
@SpringBootApplication
@EnableDiscoveryClient
@EnableOAuth2Sso
public class SampleApplication extends WebSecurityConfigurerAdapter {

    @ThriftClient(serviceId = "mall-thrift-server", path = "/test")
    CalculatorService.Client client;

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @ResponseBody
    @RequestMapping("/test")
    public int test(String operator,
                    int a, int b) throws TException {
        switch (operator) {
            case "+": return client.add(a, b);
            case "-": return client.minus(a, b);
            case "*": return client.multi(a, b);
            case "/": return client.divi(a, b);
            default:
                return client.add(a, b);
        }
    }

    @RequestMapping("/ping")
    public ResponseEntity ping(@RequestParam(defaultValue = "oauth2-server") String serviceId,
                               Model model) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance chosen = loadBalancerClient.choose(serviceId);
        model.addAttribute("instances", instances);
        model.addAttribute("chosen", chosen);
        return ResponseEntity.ok(model);
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UrlRewriteFilter());
        registration.setName("UrlRewriteFilter");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        registration.addUrlPatterns("/*");
        registration.addInitParameter("confPath", "urlrewrite.xml");
        registration.setOrder(1);
        return registration;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .logout().and()
            .authorizeRequests()
            .antMatchers("/index.html", "/home.html", "/", "/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }
}
