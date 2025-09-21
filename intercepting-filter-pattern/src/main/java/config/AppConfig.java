package config;

import filterchain.FilterChainManager;
import filterchain.FilterConfigImpl;
import jakarta.servlet.FilterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FilterChainManager filterChainManager() {
        FilterChainManager filterChainManager = new FilterChainManager();
        filterChainManager.setFilterConfigs(new FilterConfig[]{filterEx01Config(),  filterEx02Config(), filterEx03Config()});

        return filterChainManager;
    }

    @Bean
    public FilterConfig filterEx01Config() {
        return new FilterConfigImpl("ex01", "filter.FilterEx01", "/ex01/*");
    }

    @Bean
    public FilterConfig filterEx02Config() {
        return new FilterConfigImpl("ex02", "filter.FilterEx02", "/ex01/ex02/*");
    }

    @Bean
    public FilterConfig filterEx03Config() {
        return new FilterConfigImpl("ex03", "filter.FilterEx03", "/ex01/ex02/ex03/*");
    }
}
