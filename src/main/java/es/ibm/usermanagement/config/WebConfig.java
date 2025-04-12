package es.ibm.usermanagement.config;

import es.ibm.usermanagement.interceptor.SearchUserValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SearchUserValidationInterceptor validationInterceptor;

    public WebConfig(SearchUserValidationInterceptor validationInterceptor) {
        this.validationInterceptor = validationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validationInterceptor)
                .addPathPatterns("/api/v1/users/search");
    }
}
