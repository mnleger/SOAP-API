package org.example.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//This class initializes the web service at run time
@SpringBootApplication
public class Application {
    protected static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main (String[] args) throws Exception
    {
        //Run the web service
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        //document all initialized Java beans
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : allBeanNames)
        {
            logger.debug("Bean " + beanName);
        }
    }
}
