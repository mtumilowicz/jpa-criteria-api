import javax.persistence.*;
import java.util.List;

/**
 * Created by mtumilowicz on 2018-05-02.
 */
@Entity
public class Book {
    @Id
    private int id;
    
    private String name;
    
    private int price;
    
    @Enumerated(EnumType.STRING)
    private WritingGenre genre;
    
    @ManyToOne
    private Bookstore bookstore;

    @ManyToMany
    private List<Author> authors;

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public WritingGenre getGenre() {
        return genre;
    }

    public void setGenre(WritingGenre genre) {
        this.genre = genre;
    }

    public Bookstore getBookstore() {
        return bookstore;
    }

    public void setBookstore(Bookstore bookstore) {
        this.bookstore = bookstore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return id == book.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                '}';
    }
}
