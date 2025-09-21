package filterchain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Slf4j
public class FilterChainManager implements Filter {
    @Setter
    private FilterConfig[] filterConfigs;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterChainImpl filterChain = createFilterChain(((HttpServletRequest)request).getRequestURI());

        log.info(filterChain == null ? "Empty FilterChain" : filterChain.toString());

        if(filterChain != null) {
            // Call the filter chain for this request
            filterChain.doFilter(request, response);

            // Release the filter chain (if any) for this request
            filterChain.release();
        }

        // Call original filter chain
        // NOTE:
        // FilterChainManager is implemented as filter for spring mvc test integrated
        // This also calls the servlet's service() method
        chain.doFilter(request, response);
    }

    private FilterChainImpl createFilterChain(String urlPattern) {
        return Arrays
                .stream(filterConfigs)
                // Add filters that match on it's URL Pattern and Request URL
                .filter(filterConfig -> urlPatternMaches(((FilterConfigImpl)filterConfig).getUrlPattern(), urlPattern))
                .reduce(
                        Optional.<FilterChainImpl>empty(), // Create and initialize a filter chain object
                        (optionalFilterChain, filterConfig) -> {
                            FilterChainImpl filterChain = optionalFilterChain.orElseGet(FilterChainImpl::new);
                            filterChain.addFilter(filterConfig);
                            return Optional.of(filterChain);
                        },
                        (optionalFilterChain1, optionalFilterChain2) -> {
                            if(optionalFilterChain1.isEmpty()) {
                                return optionalFilterChain2;
                            }

                            if(optionalFilterChain2.isEmpty()) {
                                return optionalFilterChain1;
                            }

                            optionalFilterChain2.get().getFilters().forEach(optionalFilterChain1.get()::addFilter);
                            return optionalFilterChain1;
                        }).orElse(null);
    }

    private boolean urlPatternMaches(String pattern, String url) {
        String[] paths = pattern.split("/");
        AtomicInteger index = new AtomicInteger();

        String regExp = Arrays
                .stream(paths)
                .map(s -> {
                    boolean isEnd = index.getAndIncrement() == paths.length - 1;

                    if(s.equals("*")) {
                        return  isEnd ? ".*" :"[^/]*";
                    }

                    if(s.startsWith("*.") && isEnd) {
                        return  s.replaceAll("\\*.", ".*");
                    }

                    return s.replaceAll("\\*", "[^/]*");

                }).reduce("", (r, s) -> r + (s.isEmpty() ? "" : "/" + s));

        return Pattern.matches("^" + regExp + "$", url);
    }
}
