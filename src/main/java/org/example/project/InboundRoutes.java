package org.example.project;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

//This class handles the routing of the incoming payload. Depending on the header operationName, it guides the payload to the correct set of processes
@Component
public class InboundRoutes extends RouteBuilder {
    protected static Logger logger = LoggerFactory.getLogger(InboundRoutes.class);

    @Override
    public void configure() throws IOException
    {
        //from the cxf endpoint
        from("cxf:bean:soapInbound?dataFormat=PAYLOAD")
            .choice()
                //when the payload intends to return one student, follow the steps in direct:getStudentRoute
                .when(simple("${in.header.operationName} == 'getStudent'"))
                    .to("direct:getStudentRoute")
                //when the payload intends to return all students, follow the steps in direct:getAllStudentRoute
                .when(simple("${in.header.operationName} == 'getAllStudents'"))
                    .to("direct:getAllStudentsRoute")
                //when the payload intends to add a student to the database, follow the steps in direct:createStudentRoute
                .when(simple("${in.header.operationName} == 'postStudent'"))
                    .to("direct:createStudentRoute")
                //when the payload intends to delete a student from the database, follow the steps in direct:deleteStudentRoute
                .when(simple("${in.header.operationName} == 'deleteStudent'"))
                    .to("direct:deleteStudentRoute")
            .endChoice();

        //return one student from the database based on the inputted id
        from("direct:getStudentRoute")
            //extract the value of id from the payload and set it as a header
            .setHeader("id", xpath("//*[local-name()='id']", String.class))
            //run the sql script. The body will be set to the result of the database query
            .to("sql:classpath:getStudent.sql")
            //create a new instance of this processor to analyze the data returned from the DB
            .process(new SingleStudentProcessor())
            //initialize the body as an empty student XML stub
            .setBody(simple(IOUtils.toString(new ClassPathResource("EmptyStudentStub.xml").getInputStream(), "UTF-8")))
            //route the body to an XSL file that will fill in the contents required for the JSON object we received from the DB
            .to("xslt-saxon:getStudent.xsl")
            //remove added headers
            .removeHeader("id")
            .removeHeader("studyYear")
            .removeHeader("name");

        //return all students currently posted to the database
        from("direct:getAllStudentsRoute")
            //run the sql script and refer to the AllStudentProcessor to process the result of the sql query
            .to("sql:classpath:getAllStudents.sql")
            .process(new AllStudentProcessor());

        //post a student object to the DB
        from("direct:createStudentRoute")
            //extract the information of id, name, and year of study from the incoming XML payload
            .setHeader("id", xpath("//*[local-name()='id']", Integer.class))
            .setHeader("name", xpath("//*[local-name()='name']", String.class))
            .setHeader("studyYear", xpath("//*[local-name()='year']", Integer.class))
            //run the sql script to post to the DB
            .to("sql:classpath:createStudent.sql")
            //set the body to be equal to a status object which returns the code 200 to indicate the post was successful
            .setBody(simple(IOUtils.toString(new ClassPathResource("SuccessfulPostStatusStub.xml").getInputStream(), "UTF-8")))
            //remove added headers
            .removeHeader("id")
            .removeHeader("studyYear")
            .removeHeader("name");

        //delete one student form the DB based on the id passed into the incoming payload
        from("direct:deleteStudentRoute")
            //acquire the id of the student in which to delete form the DB
            .setHeader("id", xpath("//*[local-name()='id']", Integer.class))
            //run the sql script and create a response message to indicate the delete was successful
            .to("sql:classpath:deleteStudent.sql")
            .setBody(simple(IOUtils.toString(new ClassPathResource("SuccessfulDeleteStatusStub.xml").getInputStream(), "UTF-8")))
            //remove added header
            .removeHeader("id");
    }
}
