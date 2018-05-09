# jpa-criteria-api
## project structure
![](classes-diag.jpg)

## technologies used
* **Flyway** - constructs the database & provide datafeed in two migrations.
Ensures data coherence by checking if schema in database is up to date (
by validating additional database table concerning migrations: 
`flyway_schema_history`).  
__Reference__: https://flywaydb.org/documentation/
* **JPA 2.1**
* Hibernate