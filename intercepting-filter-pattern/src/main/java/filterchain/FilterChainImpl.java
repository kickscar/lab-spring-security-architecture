package filterchain;

import jakarta.servlet.*;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilterChainImpl implements FilterChain {

    // The set of filters that will be executed on this chain.
    @Getter
    private final List<FilterConfig> filters = new ArrayList<>();

    // The iterator that is used to maintain the current position in the filter chain.
    // This iterator is called the first time that doFilter() is called.
    private Iterator<FilterConfig> iterator;

    // Invoke the next filter in this chain, passing the specified request and response.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (iterator == null) {
            iterator = filters.iterator();
        }

        if (iterator.hasNext()) {
            FilterConfigImpl filterConfig = (FilterConfigImpl)iterator.next();
            Filter filter = filterConfig.getFilter();

            filter.doFilter(request, response, this);
        }
    }

    // Add a filter to the set of filters that will be executed in this chain.
    public void addFilter(FilterConfig filterConfig) {
        filters.add(filterConfig);
    }

    // Release references to the filters and wrapper executed by this chain.
    public void release() {
        filters.clear();
        iterator = null;
    }

    @Override
    public String toString() {
        return "FilterChainImpl{" +
                "filters=" + filters +
                '}';
    }
}
