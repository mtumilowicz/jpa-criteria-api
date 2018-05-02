import org.flywaydb.core.Flyway;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public static List<String> getAllBookNames() {
        EntityManager entityManager = emf.createEntityManager();

        entityManager.createQuery("SELECT b.name FROM Book b", Book.class);

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
}
