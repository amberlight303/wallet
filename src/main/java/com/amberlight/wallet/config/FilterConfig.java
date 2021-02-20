package com.amberlight.wallet.config;



import com.amberlight.wallet.web.filter.ThreadLogContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Configuration of filters
 */
@Configuration
public class FilterConfig {

    /**
     * Registers custom implementation of {@link OncePerRequestFilter}
     * that fills Log4j2 ThreadContext with data related to each request.
     * @return
     */
    @Bean
    public FilterRegistrationBean createThreadLogContextFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new ThreadLogContextFilter());
        return registrationBean;
    }

}
