package org.example.project;

import org.apache.camel.CamelContext;
import org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//This class sets up the initial configurations of the api before payloads can be sent to the web service
@Configuration
public class ApplicationConfiguration {
    protected Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    //This bean initializes the configuraiton of Apache Camel, which is used in the InboundRoutes class to guide the payload to the correct sql query
    @Bean
    CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                camelContext.setStreamCaching(true);
                camelContext.addRoutePolicyFactory(new MicrometerRoutePolicyFactory());
                camelContext.setMessageHistoryFactory((new MicrometerMessageHistoryFactory()));
                camelContext.getManagementStrategy().addEventNotifier(new MicrometerExchangeEventNotifier());
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                logger.info("Components:\n");
                for (String componentName : camelContext.getComponentNames()) {
                    logger.info(componentName);
                }
                logger.info("Configuration completed.");
            }


        };
    }
}
