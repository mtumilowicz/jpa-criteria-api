import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by mtumilowicz on 2018-05-02.
 */

@Entity
public class Item {

    @Id
    private long id;
    
    public long getId() {
        return id;
    }
}
