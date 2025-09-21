package filterchain;

import config.AppConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={AppConfig.class})
@WebAppConfiguration
public class FilterChainTest {
    private static MockMvc mvc;

    @BeforeAll
    public static void setup(WebApplicationContext applicationContext) {
        FilterChainManager filterChainManager = applicationContext.getBean(FilterChainManager.class);

        mvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .addFilter(new DelegatingFilterProxy(filterChainManager), "/*")
                .build();
    }

    @Test
    public void testFilterNotSet() throws Throwable {
        mvc
            .perform(get("/test"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testFilterEx01() throws Throwable {
        mvc
            .perform(get("/ex01/test"))
            .andExpect(status().isNotFound())
            .andExpect(cookie().value("FilterEx01", "Works"));
    }

    @Test
    public void testFilterEx01AndEx02() throws Throwable {
        mvc
            .perform(get("/ex01/ex02/test"))
            .andExpect(status().isNotFound())
            .andExpect(cookie().value("FilterEx01", "Works"))
            .andExpect(cookie().value("FilterEx02", "Works"));
    }
}