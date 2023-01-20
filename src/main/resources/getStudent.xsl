<?xml version="1.0" encoding="UTF-8" ?>
<!--This file is used to transform the xml payload to be filled in with the information returned from the DB. Initially, the payload is an empty XML student stub-->
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:student="http://in28minutes.com/student">

    <!--Assign 3 values as parameters. These values are extracted from the current headers in the payload which were created in the SingleStudentProcessor class-->
    <xsl:param name="id"/>
    <xsl:param name="studyYear"/>
    <xsl:param name="name"/>

    <!--Assign the 3 params in the correct XML tags to fill in the empty stub-->
    <xsl:template match="/">
        <student:GetStudentDetailsResponse>
            <student:StudentDetails>
                <student:id>
                    <xsl:value-of select="$id"></xsl:value-of>
                </student:id>
                <student:year>
                    <xsl:value-of select="$studyYear"></xsl:value-of>
                </student:year>
                <student:name>
                    <xsl:value-of select="$name"></xsl:value-of>
                </student:name>
            </student:StudentDetails>
        </student:GetStudentDetailsResponse>
    </xsl:template>
</xsl:stylesheet>