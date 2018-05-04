import com.google.common.base.Preconditions;
import org.flywaydb.core.Flyway;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.Collection;
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
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        
        Assert.assertThat(entityManager.createQuery(query).getResultList(), 
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
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        query.orderBy(cb.asc(query_root.get("title")));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }
    
    public static List<Book> getBooksByTitleLike(String like) {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("SELECT b " +
                "FROM Book b " +
                "WHERE b.title LIKE :like", Book.class);

        jpql_query.setParameter("like", like);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        query.where(cb.like(query_root.get("title"), like));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * the easiest way:
     * "SELECT DISTINCT b.bookstore " +
     * "FROM Book b " +
     * "WHERE b.title LIKE :title"
     * 
     * example below shows how to use subqueries in 'IN' clause
     */
    public static List<Bookstore> getBookstoresWithTitlesLike(String title) {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> query1 = entityManager.createQuery("SELECT bookstore " +
                "FROM Bookstore bookstore JOIN bookstore.books books " +
                "WHERE books IN (" +
                    "SELECT book " +
                "FROM Book book " +
                "WHERE book.title LIKE :title)", Bookstore.class);

        query1.setParameter("title", title);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = query.from(Bookstore.class);
        Join<Bookstore, Book> books = query_root.join("books");
        query.select(query_root);

        Subquery<Book> subquery = query.subquery(Book.class);
        Root<Book> sq_root = subquery.from(Book.class);
        subquery.select(sq_root);
        subquery.where(cb.like(sq_root.get("title"), title));

        query.where(cb.in(books).value(subquery));

        return entityManager.createQuery(query).getResultList();
    }
    
    public static List<Book> getBooksWithPriceIn(Collection<Integer> prices) {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> query1 = 
                entityManager.createQuery("SELECT b " +
                        "FROM Book b " +
                        "WHERE b.price IN :prices", Book.class);
        query1.setParameter("prices", prices);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        query.where(query_root.get("price").in(prices));

        return entityManager.createQuery(query).getResultList();
    }

    @Test
    public void getAllBookTitles() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<String> jpql_query = entityManager.createQuery("" +
                        "SELECT b.title " +
                        "FROM Book b",
                String.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("title"));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    public static List<Book> getBooksWithPriceMoreThan(int value) {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b " +
                "FROM Book b " +
                "WHERE b.price > :value", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("name"));
        query.where(cb.gt(query_root.get("price"), cb.parameter(Integer.class, "value")));

        return entityManager.createQuery(query)
                .setParameter("value", value)
                .getResultList();
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
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        query.where(cb.gt(cb.size(query_root.get("authors")),1));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
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
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(cb.count(query_root));

        Assert.assertEquals(jpql_query.getSingleResult(), entityManager.createQuery(query).getSingleResult());
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
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Book> query_root = query.from(Book.class);
        query.select(cb.tuple(
                query_root.get("genre").alias("genre"), 
                cb.count(query_root).alias("count")));
        query.groupBy(query_root.get("genre"));

        List<Tuple> jpqlResultList = jpql_query.getResultList();
        Set<Object> bookGenre = jpqlResultList.stream().map(x -> x.get("bookGenre")).collect(Collectors.toSet());
        Set<Object> bookCount = jpqlResultList.stream().map(x -> x.get("bookCount")).collect(Collectors.toSet());

        List<Tuple> resultList = entityManager.createQuery(query).getResultList();
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
        CriteriaQuery<WritingGenre> query = cb.createQuery(WritingGenre.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("genre"));
        query.groupBy(query_root.get("genre"));
        query.having(cb.gt(cb.count(query_root.get("genre")), 1));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }
    
    public static List<Book> getBooksByTitle(String title) {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> query1 = 
                entityManager.createQuery("SELECT b " +
                        "FROM Book b " +
                        "WHERE b.title = :title", Book.class);

        query1.setParameter("title", title);
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        query.where(cb.equal(
                query_root.get("title"), 
                cb.parameter(String.class, "title")));

        return entityManager.createQuery(query)
                .setParameter("title", title)
                .getResultList();
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
        CriteriaQuery<Bookstore> query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = query.from(Bookstore.class);
        query_root.join("books"); 
        query.select(query_root).distinct(true);

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query_in.getResultList().toArray()));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query_join.getResultList().toArray()));
    }
    
    @Test
    public void getBookstoresWithMostExpensiveBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" + 
                "SELECT book.bookstore " +
                "FROM Book book " +
                "WHERE book.price = (SELECT MAX(b.price) FROM Book b)", Bookstore.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> query = cb.createQuery(Bookstore.class);
        Root<Book> query_root = query.from(Book.class);
        query_root.join("bookstore");
        query.select(query_root.get("bookstore"));

        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<Book> max_subquery_root = subquery.from(Book.class);
        subquery.select(cb.max(max_subquery_root.get("price")));

        query.where(cb.equal(query_root.get("price"), subquery));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }
    
    @Test
    public void getBookstoresFromNewYork() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> jpql_query = entityManager.createQuery("" +
                "SELECT bookstore " +
                "FROM Bookstore bookstore " +
                "WHERE bookstore.address.city = 'New York'", Bookstore.class);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = query.from(Bookstore.class);
        query.select(query_root);
        query.where(cb.equal(query_root.get("address").get("city"), "New York"));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }

    /**
     * the easiest way:
     * SELECT DISTINCT book.BOOKSTORE_ID
     * FROM BOOK book
     * WHERE book.TITLE LIKE :title
     * 
     * we show how to reference a FROM expression of the parent query in the FROM clause
     * of a subquery
     */
    public static List<Bookstore> getBookstoresThatHaveTitle(String title) {
        EntityManager entityManager = emf.createEntityManager();
        TypedQuery<Bookstore> query2 = entityManager.createQuery(
                "SELECT bookstore " +
                "FROM Bookstore bookstore " +
                        "WHERE EXISTS " +
                        "(SELECT b " +
                        "FROM bookstore.books b " +
                        "WHERE b.title = :title)", Bookstore.class);
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = cc_query.from(Bookstore.class);
        cc_query.select(query_root);

        Subquery<Book> subquery = cc_query.subquery(Book.class);
        Root<Bookstore> subquery_root = subquery.correlate(query_root);
        Join<Bookstore, Book> book = subquery_root.join("books");
        subquery.select(book);
        subquery.where(cb.equal(book.get("title"), cb.parameter(String.class, title)));
        
        cc_query.where(cb.exists(subquery));
        
        return entityManager.createQuery(cc_query).getResultList();
    }

    /**
     * the easiest way:
     * SELECT DISTINCT b.BOOKSTORE_ID
     * FROM AUTHOR author
     *  JOIN BOOK_AUTHOR bookAuthor ON (bookAuthor.AUTHORS_ID = author.ID)
     *  JOIN BOOK B on bookAuthor.BOOKS_ID = B.ID
     * WHERE author.NAME = :author
     * 
     * we show how to reference a JOIN expression of the parent query in the FROM clause
     * of a subquery
     */
    public static List<Bookstore> getBookstoresThatHaveAtLeastOneBookWrittenBy(String author) {
        EntityManager entityManager = emf.createEntityManager();
        
        TypedQuery<Bookstore> query2 = entityManager.createQuery(
                "SELECT bookstore " +
                        "FROM Bookstore bookstore JOIN bookstore.books book " +
                        "WHERE EXISTS (SELECT ath FROM book.authors ath WHERE ath.name = :author)", 
                Bookstore.class);

        query2.setParameter("author", author);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> c = cb.createQuery(Bookstore.class);
        Root<Bookstore> bookstore = c.from(Bookstore.class);
        Join<Bookstore, Book> books = bookstore.join("books");
        Subquery<Author> sq = c.subquery(Author.class);
        Join<Bookstore, Book> sqBooks = sq.correlate(books);
        Join<Book, Author> authors = sqBooks.join("authors");
        sq.select(authors);
        sq.where(cb.equal(authors.get("name"), cb.parameter(String.class, "author")));
        c.select(bookstore)
                .where(cb.exists(sq));
    
    return entityManager.createQuery(c).setParameter("author", author).getResultList();
    }
    
    @Test
    public void getBooksWithFetchedAuthors() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> jpql_query = entityManager.createQuery("" +
                        "SELECT b " +
                        "FROM Book b JOIN FETCH b.authors",
                Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query_root.fetch("authors");
        query.select(query_root);

        List<Book> resultList = entityManager.createQuery(query).getResultList();
        
        Preconditions.checkState(resultList.stream().map(Book::getAuthors).allMatch(Hibernate::isInitialized), 
                "Not all book.authors are fully initialized!");

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
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
        CriteriaQuery<BookstoreCountAVG> query = cb.createQuery(BookstoreCountAVG.class);
        Root<Book> query_root = query.from(Book.class);
        query.multiselect(query_root.get("bookstore"), cb.count(query_root), cb.avg(query_root.get("price")));
        query.groupBy(query_root.get("bookstore"));

        Assert.assertThat(entityManager.createQuery(query).getResultList(),
                containsInAnyOrder(jpql_query.getResultList().toArray()));
    }
    
}
