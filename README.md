# levi_neuenschwander_p1
## Description

This project is a an orm and "webapp" for a customer table with first name, last name, username, and password; as well as an id which acts as the primary key. The orm abstracts away any SQL use and database connection configuration. The user must make their class contain a int field "id". Any model classes must have a no args constructor available.

## Annotations

@Column is an annotation to show columns of the table and has a variable name of columnName.
@Id is an annotation to show the primary key of the table and has a variable name of columnName.
@Entity is an annotation to show the entity to be represented by a table and has a variable name of tableName.

## Tech Stack

Java 8
JUnit
Mockito
Apache Maven
Jackson library (for JSON marshalling/unmarshalling)
Java EE Servlet API (v4.0+)
PostGreSQL deployed on AWS RDS
Git SCM (on GitHub)
