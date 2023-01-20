package org.example.project;

import org.apache.camel.component.cxf.CxfConfigurer;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.xml.namespace.QName;

//This class configure the CXF endpoint, which is the endpoint in which XML payloads enter the api
@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class CxfEndpointConfig{

    //Grab the values found in the .properties file and assign them to Java variables
    @Value("${inbound.project.scheme}")
    private String inboundProjectScheme;

    @Value("${inbound.project.host}")
    private String inboundProjectHost;

    @Value("${inbound.project.port}")
    private String inboundProjectPort;

    @Value("${inbound.project.resource.path}")
    private String inboundProjectResourcePath;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Bus bus;

    //This bean configures the cxf endpoint to match the previously defined informaiton from the WSDL file
    @Bean(name = "soapInbound")
    public CxfEndpoint soapServiceInbound() {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();

        //Set the bean id so that Apache Camel knows which from() method corresponds to this endpoint
        cxfEndpoint.setBeanId("soapInbound");

        //set the URL of the endpoint
        cxfEndpoint.setAddress(inboundProjectScheme + "://" + inboundProjectHost + ":" + inboundProjectPort + "/" + inboundProjectResourcePath);
        cxfEndpoint.setPublishedEndpointUrl(inboundProjectScheme + "://" + inboundProjectHost + ":" + inboundProjectPort + "/" + inboundProjectResourcePath);
        cxfEndpoint.setBus(bus);

        //Assign the WSDL file to the endpoint. This lets the endpoint know what the incoming payloads will look like
        cxfEndpoint.setWsdlURL("src/main/resources/project.wsdl");
        cxfEndpoint.setServiceNameAsQName(new QName("http://www.examples.com/ProjectService", "Student_Service", "s"));
        cxfEndpoint.setEndpointNameAsQName(new QName("http://www.examples.com/ProjectService", "Student_Port", "s"));
        return cxfEndpoint;
    }

    //This method configures the underlying rules for the client in which the cxf endpoint runs
    private CxfConfigurer getCxfConfigure(){
        return new CxfConfigurer() {
            @Override
            public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {

            }

            @Override
            public void configureClient(Client client) {
                HTTPConduit conduit = (HTTPConduit) client.getConduit();
                HTTPClientPolicy policy = new HTTPClientPolicy();
                policy.setAllowChunking(false);
                policy.setCacheControl("No-Cache");
                policy.setConnection(ConnectionType.KEEP_ALIVE);
                conduit.setClient(policy);
            }

            @Override
            public void configureServer(Server server) {

            }
        };
    }
}
