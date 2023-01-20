package org.example.project;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.Map;

//This class takes in a JSON object returned from the DB and creates headers that will be used in an XSL file later in the Camel route
public class SingleStudentProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception
    {
        //Get the JSON object that was returned from the DB
        ArrayList<Map<String, Object>> students = (ArrayList<Map<String, Object>>) exchange.getIn().getBody();
        //if the DB returned a student
        if (students.size() != 0)
        {
            //create headers for the year of study and name of the student returned from the DB
            Map<String, Object> student = students.get(0);
            exchange.getIn().setHeader("studyYear", student.get("studyYear"));
            exchange.getIn().setHeader("name", student.get("name"));
        }
        //if the DB did not return a student, create empty headers for id, year of study, and name
        else
        {
            exchange.getIn().setHeader("id", "");
            exchange.getIn().setHeader("studyYear", "");
            exchange.getIn().setHeader("name", "");
        }
    }
}
