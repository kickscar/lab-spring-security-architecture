package filterchain;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import lombok.Getter;

import java.util.Enumeration;

public class FilterConfigImpl implements FilterConfig {
    private Filter filter;
    private final String filterName;
    private final String filterClass;

    @Getter
    private final String urlPattern;

    public FilterConfigImpl(String filterName, String filterClass, String urlPattern) {
        this.filterName = filterName;
        this.filterClass = filterClass;
        this.urlPattern = urlPattern;
    }

    public Filter getFilter() {
        if(filter != null) {
            return filter;
        }

        try {
            filter = (Filter)Class
                .forName(filterClass)
                .getDeclaredConstructor()
                .newInstance();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        return filter;
    }

    @Override
    public String getFilterName() {
        return filterName;
    }

    /* Not Supports */
    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }

    @Override
    public String toString() {
        return "FilterConfigImpl{" +
                "urlPattern='" + urlPattern + '\'' +
                ", filterClass='" + filterClass + '\'' +
                ", filterName='" + filterName + '\'' +
                '}';
    }
}
