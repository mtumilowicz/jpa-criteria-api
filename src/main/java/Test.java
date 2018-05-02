import org.flywaydb.core.Flyway;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
}
