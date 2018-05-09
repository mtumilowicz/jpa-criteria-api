# jpa-criteria-api
## project structure
![](classes-diag.jpg)

## technologies used
* **Flyway** - constructs the database & provide datafeed in two migrations.
Ensures data coherence by checking if schema in database is up to date (
by validating additional database table concerning migrations: 
`flyway_schema_history`).  
_Reference_: https://flywaydb.org/documentation/
* **JPA 2.1**  
_Reference_: [Pro JPA 2 2nd Edition](https://www.amazon.com/Pro-JPA-Experts-Voice-Java/dp/1430249269)  
_Reference_: [JSR 338: JavaTM Persistence API, Version 2.1](http://download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf?AuthParam=1525854294_ebba2ec6df9aff2c1b90ef7a62789831)
* **Hibernate** - provider of `JPA 2.1` specification. We don't use
`EclipseLink` despite the fact that it is `reference implementation` of
`JPA 2.1` because apart from `JPQL` we want to explore `HQL` as a more
powerful query language.