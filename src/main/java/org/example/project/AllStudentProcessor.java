package org.example.project;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Map;

//This class takes in the current payload (JSON object returned from the DB) and parses it back into XML
public class AllStudentProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception
    {
        //Get the JSON object returned from the DB
        ArrayList<Map<String, Object>> students = (ArrayList<Map<String, Object>>) exchange.getIn().getBody();

        //If no object was returned, the query retrieved an empty payload
        if (students.size() == 0)
        {
            //set the body in which to return to the client to be an empty stub
            exchange.getIn().setBody(IOUtils.toString(new ClassPathResource("EmptyStudentsStub.xml").getInputStream(), "UTF-8"));
        }
        //the DB returned a list of JSON objects
        else
        {
            //construct the xml payload in which to return to the client
            String body = "<students>\n";

            //for each JSON object returned
            for (Map<String, Object> student : students)
            {
                //construct the XML equivalent of the given JSON object
                body += "<student>";
                body += "<id>" + student.get("id")+ "</id>\n";
                body += "<year>" + student.get("studyYear")+ "</year>\n";
                body += "<name>" + student.get("name")+ "</name>\n";
                body += "</student>\n";
            }
            //close the parent tag of the return payload
            body += "</students>";
            exchange.getIn().setBody(body);
        }
    }
}
