package query_service.query_pre_processor.config;

import query_service.query_pre_processor.callback.CallBackService;
import query_service.query_pre_processor.query.QueryService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {QueryService.class, CallBackService.class})
public class RootConfiguration {
}
