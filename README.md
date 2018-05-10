# jpa-criteria-api
The main goal of this project is to explore basic query features of 
`JPA 2.1` specification:  
* **JPQL**
* **JPA Criteria API** (typesafe with auto-generated
static metamodels)

As a `JPA` provider we choose `Hibernate` to test some of `HQL`.

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
_Reference_: [JSR 338: JavaTM Persistence API, Version 2.1](http://download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf)  
* **Hibernate** - provider of `JPA 2.1` specification. We don't use
`EclipseLink` despite the fact that it is `reference implementation` of
`JPA 2.1` because apart from `JPQL` we want to explore `HQL` as a more
powerful query language.  
_Reference_: [Java Persistence with Hibernate Second Edition](https://www.amazon.com/exec/obidos/ASIN/1617290459)  
_Reference_: [Hibernate Interview Questions and Answers](https://www.journaldev.com/3633/hibernate-interview-questions-and-answers)  
* **Jupiter & AssertJ** - to test result sets (`AssertJ` has very 
convinient methods to comparing lists regardless order).

## project description  
The main idea is to explore `JPA Criteria API` by writing a querys in 
`JPQL` or `HQL`, then to try re-write them in `JPA Criteria API` & check 
result-sets identity.  
Comparing equality of result sets with `JUnit` & `hamcrest`:
```
import static org.hamcrest.Matchers.containsInAnyOrder;
...
Assert.assertThat(entityManager.createQuery(cc_query)
                .getResultList(),
        containsInAnyOrder(jpql_query.getResultList().toArray()));
```
but we decide to use `Jupiter` & `AssertJ` (more expressive):
```
import static org.assertj.core.api.Assertions.assertThat;
...
assertThat(entityManager.createQuery(cc_query).getResultList())
        .containsExactlyInAnyOrderElementsOf(jpql_query.getResultList());
```

## project content
`src/main/java`: entities, utility classes & `META-INF` folder with 
`persistence.xml`  
`resources/db/migration`: `Flyway` migrations as `SQL` scripts  
`test/java`: showcase of `JPQL`, `HQL` & `Criteria API` with tests  
`target/generated-sources`: static metamodels of entities  

## tests
We have to classes with nearly the same content: `Tests` and 
`TestsWithFullTypeSafe`. The only difference is that in the first class 
we use strings to denote fields while in the letter we use static 
metamodels.  
Example:  
From `Tests`:  
```
cc_query.orderBy(cb.asc(cc_query_root.get("title")));
```
From `TestsWithFullTypeSafe`:
```
cc_query.orderBy(cb.asc(cc_query_root.get(Book_.title)));
```

All methods are quite simple & straightforward use of `Criteria API`.   
The most interesting are:  

**getBookstoresWithMostExpensiveBook()** - we show how to use subqueries
```
SELECT book.bookstore
FROM Book book
WHERE book.price = (SELECT MAX(b.price) FROM Book b)
```
and then we transcript them in `Criteria API`
```
...
Subquery<Integer> cc_max_subquery = cc_query.subquery(Integer.class);
Root<Book> cc_max_subquery_root = cc_max_subquery.from(Book.class);
cc_max_subquery.select(cb.max(cc_max_subquery_root.get("price")));

cc_query.where(cb.equal(cc_query_root.get("price"), cc_max_subquery)); // connect subquery with main query
```

**getBookstoresThatHaveTitle()** - we show how to reference a `FROM` 
expression of the parent query in the `FROM` clause of a subquery
```
SELECT bookstore
FROM Bookstore bookstore
WHERE EXISTS (
    SELECT b
    FROM bookstore.books b
    WHERE b.title = :title
    )
```
and then we transcript them in `Criteria API` using `correlate`:
```
Subquery<Book> cc_subquery = cc_query.subquery(Book.class);
Root<Bookstore> cc_subquery_root = cc_subquery.correlate(cc_query_root); // said reference
Join<Bookstore, Book> book = cc_subquery_root.join("books");
cc_subquery.select(book);
cc_subquery.where(cb.equal(book.get("title"), cb.parameter(String.class, "title")));

cc_query.where(cb.exists(cc_subquery)); // connect subquery with main query
```
 
**getBookstoresThatHaveAtLeastOneBookWrittenBy()** - we show how to 
reference a `JOIN` expression of the parent query in the `FROM` clause
of a subquery
```
SELECT bookstore
FROM Bookstore bookstore JOIN bookstore.books book
WHERE EXISTS (SELECT ath FROM book.authors ath WHERE ath.name = :author)
```
and then we transcript them in `Criteria API` using `correlate`:
```
Subquery<Author> cc_subquery = cc_query.subquery(Author.class);
Join<Bookstore, Book> cc_subquery_root = cc_subquery.correlate(books); // said reference
Join<Book, Author> authors = cc_subquery_root.join("authors");
cc_subquery.select(authors);
cc_subquery.where(cb.equal(authors.get("name"), cb.parameter(String.class, "author")));
        
cc_query.select(cc_query_root)
    .where(cb.exists(cc_subquery)); // connect subquery with main query
```

## static metamodels
They are auto-generated by `maven-compiler-plugin`.  
All you need to do is:  
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArguments>
            <processor>org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor</processor>
        </compilerArguments>
    </configuration>
</plugin>
```
```
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jpamodelgen</artifactId>
    <version>5.2.17.Final</version>
    <scope>provided</scope>
</dependency>
```
Mark `target/generated-sources` as a `Generated Sources Root`.  
In `IntelliJ` just left-click on `target/generated-sources` -> 
`Mark Directory As` -> `Generated Sources Root`.