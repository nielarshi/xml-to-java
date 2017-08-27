# xml-to-java

Given an xml as input, it creates Java POJO classes using reflection.
It then takes values from xml and populates the object to be directly consumed in application reducing the work by:

1. No need of generating POJO based on xsd.
2. No need of writing own XML parser to parse and populate Java objects.

It does both together, using just xml.

Useful when xsd is not available and need to use xml in a project.
