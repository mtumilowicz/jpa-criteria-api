import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.flywaydb.core.Flyway;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Created by mtumilowicz on 2018-05-02.
 */
public class Tests {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("NewPersistenceUnit");

    @BeforeClass
    public static void prepareForTests() {
        prepareDatabaseForTests();
    }

    private static void prepareDatabaseForTests() {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:h2:file:./database", null, null);
        flyway.migrate();
    }

    @Test
    public void getAllBooks() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query =
                entityManager.createQuery("" +
                                "SELECT b " +
                                "FROM Book b",
                        Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getAllBooksOrderByTitle() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query =
                entityManager.createQuery("" +
                                "SELECT b " +
                                "FROM Book b " +
                                "ORDER BY b.title",
                        Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.orderBy(cb.asc(cc_query_root.get("title")));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBooksByTitleLike() {
        String titleLike = "Lord%";
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("" +
                        "SELECT b " +
                        "FROM Book b " +
                        "WHERE b.title LIKE :like",
                Book.class)
                .setParameter("like", titleLike);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.where(cb.like(cc_query_root.get("title"), titleLike));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    /**
     * the easiest way:
     * "SELECT DISTINCT b.bookstore " +
     * "FROM Book b " +
     * "WHERE b.title LIKE :title"
     * <p>
     * example below shows how to use subqueries in 'IN' clause
     */
    @Test
    public void getBookstoresWithTitlesLike() {
        String titleLike = "Lord%";
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" +
                        "SELECT bookstore " +
                        "FROM Bookstore bookstore JOIN bookstore.books books " +
                        "WHERE books IN (" +
                        "SELECT book " +
                        "FROM Book book " +
                        "WHERE book.title LIKE :title)",
                Bookstore.class)
                .setParameter("title", titleLike);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> cc_query_root = cc_query.from(Bookstore.class);
        Join<Bookstore, Book> books = cc_query_root.join("books");
        cc_query.select(cc_query_root);

        Subquery<Book> cc_subquery = cc_query.subquery(Book.class);
        Root<Book> cc_subquery_root = cc_subquery.from(Book.class);
        cc_subquery.select(cc_subquery_root);
        cc_subquery.where(cb.like(cc_subquery_root.get("title"), titleLike));

        cc_query.where(cb.in(books).value(cc_subquery));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBooksWithPriceIn() {
        ImmutableSet<Integer> prices = ImmutableSet.of(10, 15, 20);

        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query =
                entityManager.createQuery("" +
                                "SELECT b " +
                                "FROM Book b " +
                                "WHERE b.price IN :prices",
                        Book.class)
                        .setParameter("prices", prices);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.where(cc_query_root.get("price").in(prices));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getAllBookTitles() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<String> jpql_query = entityManager.createQuery("" +
                        "SELECT b.title " +
                        "FROM Book b",
                String.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cc_query = cb.createQuery(String.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root.get("title"));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBooksWithPriceMoreThan() {
        int value = 10;
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("" +
                        "SELECT b " +
                        "FROM Book b " +
                        "WHERE b.price > :value",
                Book.class)
                .setParameter("value", value);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.where(cb.gt(cc_query_root.get("price"), cb.parameter(Integer.class, "value")));

        Assert.assertThat(entityManager.createQuery(cc_query)
                        .setParameter("value", value)
                        .getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBooksWithMoreThanOneAuthors() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("" +
                        "SELECT b " +
                        "FROM Book b " +
                        "WHERE size(b.authors) > 1",
                Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.where(cb.gt(cb.size(cc_query_root.get("authors")), 1));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void countBooks() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Long> jpql_query = entityManager.createQuery("" +
                        "SELECT count(b) " +
                        "FROM Book b",
                Long.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cc_query = cb.createQuery(Long.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cb.count(cc_query_root));

        Assert.assertEquals(jpql_query.getSingleResult(), 
                entityManager.createQuery(cc_query).getSingleResult());
    }

    @Test
    public void countBooksByGenre() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Tuple> jpql_query =
                entityManager.createQuery("" +
                                "SELECT " +
                                "b.genre AS bookGenre, count(b) AS bookCount " +
                                "FROM Book b " +
                                "GROUP BY b.genre",
                        Tuple.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cc_query = cb.createTupleQuery();
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cb.tuple(
                cc_query_root.get("genre").alias("genre"),
                cb.count(cc_query_root).alias("count")));
        cc_query.groupBy(cc_query_root.get("genre"));

        List<Tuple> jpqlResultList = jpql_query.getResultList();
        Set<Object> bookGenre = jpqlResultList.stream().map(x -> x.get("bookGenre")).collect(Collectors.toSet());
        Set<Object> bookCount = jpqlResultList.stream().map(x -> x.get("bookCount")).collect(Collectors.toSet());

        List<Tuple> resultList = entityManager.createQuery(cc_query).getResultList();
        Set<Object> genre = resultList.stream().map(x -> x.get("genre")).collect(Collectors.toSet());
        Set<Object> count = resultList.stream().map(x -> x.get("count")).collect(Collectors.toSet());


        Assert.assertEquals(bookGenre, genre);
        Assert.assertEquals(bookCount, count);
    }

    @Test
    public void getGenresThatHaveMoreThanOneBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<WritingGenre> jpql_query = entityManager.createQuery("" +
                        "SELECT b.genre " +
                        "FROM Book b " +
                        "GROUP BY b.genre " +
                        "HAVING count(b.genre) > 1",
                WritingGenre.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<WritingGenre> cc_query = cb.createQuery(WritingGenre.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root.get("genre"));
        cc_query.groupBy(cc_query_root.get("genre"));
        cc_query.having(cb.gt(cb.count(cc_query_root.get("genre")), 1));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    public void getBooksByTitle() {
        String title = "Harry Potter";
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query =
                entityManager.createQuery("" +
                                "SELECT b " +
                                "FROM Book b " +
                                "WHERE b.title = :title",
                        Book.class)
                        .setParameter("title", title);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.select(cc_query_root);
        cc_query.where(cb.equal(
                cc_query_root.get("title"),
                cb.parameter(String.class, "title")));

        Assert.assertThat(entityManager.createQuery(cc_query)
                        .setParameter("title", title)
                        .getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBookstoresWithAtLeastOneBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query_in =
                entityManager.createQuery("" +
                                "SELECT DISTINCT bookstore " +
                                "FROM Bookstore bookstore, IN(bookstore.books) books",
                        // note that without alias 'books' in won't work
                        // (org.hibernate.hql.internal.ast.QuerySyntaxException)
                        Bookstore.class);

        TypedQuery<Bookstore> jpql_query_join =
                entityManager.createQuery("" +
                                "SELECT DISTINCT bookstore " +
                                "FROM Bookstore bookstore JOIN bookstore.books",
                        Bookstore.class);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> cc_query_root = cc_query.from(Bookstore.class);
        cc_query_root.join("books");
        cc_query.select(cc_query_root).distinct(true);

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query_in.getResultList().toArray()));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query_join.getResultList().toArray()));
    }

    @Test
    public void getBookstoresWithMostExpensiveBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" +
                        "SELECT book.bookstore " +
                        "FROM Book book " +
                        "WHERE book.price = (SELECT MAX(b.price) FROM Book b)",
                Bookstore.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query_root.join("bookstore");
        cc_query.select(cc_query_root.get("bookstore"));

        Subquery<Integer> cc_max_subquery = cc_query.subquery(Integer.class);
        Root<Book> cc_max_subquery_root = cc_max_subquery.from(Book.class);
        cc_max_subquery.select(cb.max(cc_max_subquery_root.get("price")));

        cc_query.where(cb.equal(cc_query_root.get("price"), cc_max_subquery));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBookstoresFromNewYork() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" +
                        "SELECT bookstore " +
                        "FROM Bookstore bookstore " +
                        "WHERE bookstore.address.city = 'New York'",
                Bookstore.class);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> cc_query_root = cc_query.from(Bookstore.class);
        cc_query.select(cc_query_root);
        cc_query.where(cb.equal(cc_query_root.get("address").get("city"), "New York"));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    /**
     * the easiest way:
     * SELECT DISTINCT book.BOOKSTORE_ID
     * FROM BOOK book
     * WHERE book.TITLE LIKE :title
     * <p>
     * we show how to reference a FROM expression of the parent query in the FROM clause
     * of a subquery
     */
    @Test
    public void getBookstoresThatHaveTitle() {
        String title = "Harry Potter";
        EntityManager entityManager = emf.createEntityManager();
        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" +
                        "SELECT bookstore " +
                        "FROM Bookstore bookstore " +
                        "WHERE EXISTS " +
                        "(SELECT b " +
                        "FROM bookstore.books b " +
                        "WHERE b.title = :title)",
                Bookstore.class)
                .setParameter("title", title);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> cc_query_root = cc_query.from(Bookstore.class);
        cc_query.select(cc_query_root);

        Subquery<Book> cc_subquery = cc_query.subquery(Book.class);
        Root<Bookstore> cc_subquery_root = cc_subquery.correlate(cc_query_root);
        Join<Bookstore, Book> book = cc_subquery_root.join("books");
        cc_subquery.select(book);
        cc_subquery.where(cb.equal(book.get("title"), cb.parameter(String.class, "title")));

        cc_query.where(cb.exists(cc_subquery));

        Assert.assertThat(entityManager.createQuery(cc_query)
                        .setParameter("title", title)
                        .getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    /**
     * the easiest way:
     * SELECT DISTINCT b.BOOKSTORE_ID
     * FROM AUTHOR author
     * JOIN BOOK_AUTHOR bookAuthor ON (bookAuthor.AUTHORS_ID = author.ID)
     * JOIN BOOK B on bookAuthor.BOOKS_ID = B.ID
     * WHERE author.NAME = :author
     * <p>
     * we show how to reference a JOIN expression of the parent query in the FROM clause
     * of a subquery
     */
    @Test
    public void getBookstoresThatHaveAtLeastOneBookWrittenBy() {
        String author = "Joshua Bloch";
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery(
                "SELECT bookstore " +
                        "FROM Bookstore bookstore JOIN bookstore.books book " +
                        "WHERE EXISTS (SELECT ath FROM book.authors ath WHERE ath.name = :author)",
                Bookstore.class)
                .setParameter("author", author);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> cc_query_root = cc_query.from(Bookstore.class);
        Join<Bookstore, Book> books = cc_query_root.join("books");
        Subquery<Author> cc_subquery = cc_query.subquery(Author.class);
        Join<Bookstore, Book> cc_subquery_root = cc_subquery.correlate(books);
        Join<Book, Author> authors = cc_subquery_root.join("authors");
        cc_subquery.select(authors);
        cc_subquery.where(cb.equal(authors.get("name"), cb.parameter(String.class, "author")));
        cc_query.select(cc_query_root)
                .where(cb.exists(cc_subquery));

        Assert.assertThat(entityManager.createQuery(cc_query)
                        .setParameter("author", author)
                        .getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBooksWithFetchedAuthors() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("" +
                        "SELECT b " +
                        "FROM Book b JOIN FETCH b.authors",
                Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query_root.fetch("authors");
        cc_query.select(cc_query_root);

        List<Book> resultList = entityManager.createQuery(cc_query).getResultList();

        Preconditions.checkState(resultList.stream().map(Book::getAuthors).allMatch(Hibernate::isInitialized),
                "Not all book.authors are fully initialized!");

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    @Test
    public void getBookstoresWithCountBooksAndPriceAverage() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<BookstoreCountAVG> jpql_query = entityManager.createQuery("" +
                        "SELECT new BookstoreCountAVG(b.bookstore, count(b), avg(b.price)) " +
                        "FROM Book b " +
                        "GROUP BY b.bookstore",
                BookstoreCountAVG.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BookstoreCountAVG> cc_query = cb.createQuery(BookstoreCountAVG.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);
        cc_query.multiselect(cc_query_root.get("bookstore"), cb.count(cc_query_root), cb.avg(cc_query_root.get("price")));
        cc_query.groupBy(cc_query_root.get("bookstore"));

        Assert.assertThat(entityManager.createQuery(cc_query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

}
