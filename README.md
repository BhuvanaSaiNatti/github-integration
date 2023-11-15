# Top starred Github repositories
This backend application exposes api to fetch the most popular Github repositories.

## How it is structured
The application uses Spring Boot. And the code organize as this -

1. `configuration` contains the configuration needed
2. `controller` contains the REST resource classes
3. `exception` contains the exception classes and handlers
4. `service` contains the business logic implemented as service classes
5. `model` contains the model classes and response entities
6. `validator` contains the classes that perform the validations

## CRUD API supported
The following api are implemented to support operations on repositories
#### GET /repositories - Fetches the popular repositories on Github
By default, fetches the top 30 starred repositories on Github.
  - Query Params (All are optional)
    - language : Please find the supported languages in Language enum class.
    - createdAfter : Fetches repositories which are created on or after this datetime.
    - limitCount : Fetches this number of repositories from Github. We could use this to fetch top 10, 50 or 100.
    - page : Fetches the results from this mentioned page. 
  - Validations
    - language - If provided, Must match case-insensitive with the Language enum values.
    - createdAfter - If provided, 
      - Must follow DateTime format.
      - Will not allow a date from future
      - Will not allow wrong date (wrong month or year etc)
    - limitCount - Allows maximum of 100 results. If user wants more, they need to send the next page number under page query param.
      - Should be greater than 0 and less than 100. If not specified by user, 30 will be considered as default value.
    - page - Should be greater than 0. If not specified by user, 1 will be considered as default value.
  - Caching - Used Cacheable annotation of spring boot. Caches the results from Github (with key as query parameters) as per cron expression specified. This is done in order to avoid frequent calls on Github for the same query parameter calls. 
  - Authentication - Currently, the Github call is unauthenticated. So, Github allows max of 10 requests per minute. You could specify your Github personal access token under github.service.token in application.properties to make authenticated requests to Github.


## Technical specifications
- Spring Boot - Framework for restful web service
- Java - Programming language
- Maven - For Dependency management
- JUnit and Mockito - For unit tests

## How to start the application and test

1. Build the application (Run spotless if you make any code changes)
    ```
   mvn spotless:apply
   mvn clean install
    ```
2. Add Application configuration in IDE with com.redcarepharmacy.Application class and Start the application.
3. Access the API with the following
    ```
   http://localhost:8080/repositories
   http://localhost:8080/repositories?createdAfter=2023-11-11T11:11:11&language=java&limitCount=10&page=1
   ```