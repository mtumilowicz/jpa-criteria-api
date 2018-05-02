import javax.persistence.*;
import java.util.List;

/**
 * Created by mtumilowicz on 2018-05-02.
 */
@Entity
public class Author {
    @Id
    private int id;
    
    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    public int getId() {
        return id;
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

        Author author = (Author) o;

        return id == author.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                '}';
    }
}
