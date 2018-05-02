import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Created by mtumilowicz on 2018-05-02.
 */
@Entity
public class Bookstore {
    @Id
    private int id;
    
    private String name;
    
    @OneToOne
    private Address address;
    
    @OneToMany(mappedBy = "bookstore")
    private List<Book> books;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookstore bookstore = (Bookstore) o;

        return id == bookstore.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Bookstore{" +
                "id=" + id +
                '}';
    }
}
