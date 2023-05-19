# Web Services With Spring Boot  

## To compile (run mvn command in test directory)

### To launch the project
* mvn spring-boot:run
### To launch unit and integration tests
* mvn test
### To build ans package the application in target directory
* mvn clean package

## The project
A SpringBoot API that exposes two services:
* one that allows to register a user
* one that displays the details of a registered user by id
* one that displays all the users as a list
A user is defined by:
* a user name
* a birthdate
* it has a country of residence which must be France  

A user has optional attributes:
* a phone number
* a gender  

A request parameter "verify" with default value to true, can be set to PUT request parameter to avoid updating an existing user (same name and birthday)

Business rules : Only adult French residents are allowed to create an account.  
Inputs are validated and return proper error messages/http statuses.

### Bonus
* Use AOP to log inputs and outputs of each method call as well as the processing time
* UML/Database schema

## The REST API developed
### UserDto definition

A UserDto can be defined like that fot the POST and PUT body and the GET result:

{  
"id": 1,  
"gender": "Male",  
"name": "Dupont",  
"birthday": "2000-10-21",  
"countryName": "France",  
"countryCode": "fr",  
"phoneNumber": "+33610287915"  
}

id : Not in the POST or PUT  
gender : Optional "Male" or "Female"  
name : Mandatory  
birthday : Mandatory YYYY-MM-DD  
countryName : Optional if countryCode is valid  
countryCode : Optional if countryName is valid
phoneNumber : Optional + followed by digits  

The name, birthday and countryOfResidence are mandatory  
The country of residence must contain name or countryCode

The file data.sqm in resources directory set the default countries.
Country controller web service have not been developed but CountryService exists.

### How to call the rest API

* GET : (Return code 200 if Ok) a list of UsersDto localhost:8080/users  
To get a list of all users

* GET : (Return code 200 if Ok) a UserDto localhost:8080/users/{id}  
To get a UserDto by the user id

* POST/PUT : (Return code 201 if Ok) localhost:8080/users (UserDto)  
The response body contains a UserDto with the id and the countryName and countryCode set.  
The post can be done if mandatory fields are presents, if the age of user is less than 18 years and if country is France.
For the PUT, a request parameter "verify" can be set to "false" to avoid existing verification and update by id.
Otherwise, if there is no user with same name and same birthday the PUT (if verify is set to true) or POST can not be done.
A UserDto is returned.

* DELETE (Return code 204 if Ok, 404 if user id not found) : localhost:8080/users/{id}  
To delete a user by its id

### The error messages
The error message is returned like this if any error occurs:  
{  
"errorCode": An internal error code,  
"errorMessage": "The error message",  
"devErrorMessage": The stack trace  
"additionalData": {}  
}

additionalData : could have been representative in a real project  

## The architecture
### A layer architecture
The architecture is a layer architecture. The entry point of the web services is the controller layer which call the 
services layer which call the repository layer which interact with the model which defines the database and interact with it.  
The presentation is made with a DTO (UserDto) which enable to set a user, and it's country at once.  
The mapping from User and Country to UserDto and the opposite is made with a mapper.  
Exceptions are thrown if the constraints or the business rules, which are implemented in the service layer, are not respected.
A GlobalExceptionHandler controller manages the thrown exceptions and provides the server error message to display and server return code.  
A LoggingAspect class provide a log of all methods and their time to process. It uses AOP.  
A class DateFormatting is only used in tests to create a jav.sql.Date from a String.  

* Controller package
* Service package
* Repository package
* Model package
* Dto package
* Mapper package
* Aspect package for AOP

### UML conception

In this schema we can see the package layer architecture and the main classes.

* [UML Schema](images/UML.png)

### The SQL database

For this simple project, a H2 database have been used.  
The database access is set in the application.properties file in resources directory.

* [DB Schema](images/Database.png)


## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.6/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#using.devtools)

## Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

