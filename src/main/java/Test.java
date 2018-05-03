import org.flywaydb.core.Flyway;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by mtumilowicz on 2018-05-02.
 */
public class Test {
    
    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("NewPersistenceUnit");

    public static void main(String[] args) {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:h2:file:./database", null, null);
        flyway.migrate();
    }
    
    public static List<Book> getAllBooks() {
        EntityManager entityManager = emf.createEntityManager();
        
        entityManager.createQuery("SELECT b FROM Book b", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root);
        
        return entityManager.createQuery(query).getResultList();
    }

    public static List<String> getAllBookTitles() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b.title FROM Book b", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("name"));

        return entityManager.createQuery(query).getResultList();
    }

    public static List<Book> getBooksWithPriceMoreThan10() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b FROM Book b WHERE b.price > 10", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("name"));
        query.where(cb.gt(query_root.get("price"), 10));

        return entityManager.createQuery(query).getResultList();
    }
    
    public static List<Book> getBooksWithMoreThanOneAuthors() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b FROM Book b WHERE size(b.authors) > 1", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("name"));
        query.where(cb.gt(cb.size(query_root.get("authors")),1));

        return entityManager.createQuery(query).getResultList();
    }
    
    public static long countBooks() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT count(b) FROM Book b", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(cb.count(query_root));

        return entityManager.createQuery(query).getSingleResult();
    }
    
    public static List<Tuple> countBooksByGenre() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b.genre, count(b) FROM Book b GROUP BY b.genre", Book.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Book> query_root = query.from(Book.class);
        query.select(cb.tuple(
                query_root.get("genre").alias("genre"), 
                cb.count(query_root).alias("count")));
        query.groupBy(query_root.get("genre"));

        return entityManager.createQuery(query).getResultList();
    }
    
    public static List<WritingGenre> getGenresThatHaveMoreThanOneBook() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b.genre FROM Book b GROUP BY b.genre HAVING count(b.genre) > 1", WritingGenre.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<WritingGenre> query = cb.createQuery(WritingGenre.class);
        Root<Book> query_root = query.from(Book.class);
        query.select(query_root.get("genre"));
        query.groupBy(query_root.get("genre"));
        query.having(cb.gt(cb.count(query_root.get("genre")), 1));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    public static List<Book> getBooksByTitle(String title) {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Book> query1 = 
                entityManager.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class);

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
    
    public static List<Bookstore> getBookstoresWithAtLeastOneBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> query_in = 
                entityManager.createQuery("SELECT DISTINCT bookstore " +
                        "FROM Bookstore bookstore, IN(bookstore.books) books", 
                        // note that without alias 'books' in won't work (org.hibernate.hql.internal.ast.QuerySyntaxException)
                        Bookstore.class);

        TypedQuery<Bookstore> query_join =
                entityManager.createQuery("SELECT DISTINCT bookstore " +
                                "FROM Bookstore bookstore JOIN bookstore.books",
                        Bookstore.class);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = query.from(Bookstore.class);
        query_root.join("books"); 
        query.select(query_root).distinct(true);
        
        return entityManager.createQuery(query).getResultList();
    }
    
    public static List<Bookstore> getBookstoresWithMostExpensiveBook() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> query = entityManager.createQuery("SELECT book.bookstore " +
                "FROM Book book " +
                "WHERE book.price = (SELECT MAX(b.price) FROM Book b)", Bookstore.class);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Book> query_root = cc_query.from(Book.class);
        query_root.join("bookstore");
        cc_query.select(query_root.get("bookstore"));

        Subquery<Integer> subquery = cc_query.subquery(Integer.class);
        Root<Book> max_subquery_root = subquery.from(Book.class);
        subquery.select(cb.max(max_subquery_root.get("price")));

        cc_query.where(cb.equal(query_root.get("price"), subquery));
        
        return entityManager.createQuery(cc_query).getResultList();
    }
    
    public static List<Bookstore> getBookstoresFromNewYork() {
        EntityManager entityManager = emf.createEntityManager();

        TypedQuery<Bookstore> query2 = entityManager.createQuery("SELECT bookstore " +
                "FROM Bookstore bookstore " +
                "WHERE bookstore.address.city = 'New York'", Bookstore.class);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bookstore> cc_query = cb.createQuery(Bookstore.class);
        Root<Bookstore> query_root = cc_query.from(Bookstore.class);
        cc_query.select(query_root);
        cc_query.where(cb.equal(query_root.get("address").get("city"), "New York"));
        
        return entityManager.createQuery(cc_query).getResultList();
    }
    
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
    
}
