import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Book.class)
public abstract class Book_ {

	public static volatile SingularAttribute<Book, Bookstore> bookstore;
	public static volatile SingularAttribute<Book, Integer> price;
	public static volatile SingularAttribute<Book, WritingGenre> genre;
	public static volatile SingularAttribute<Book, Integer> id;
	public static volatile SingularAttribute<Book, String> title;
	public static volatile ListAttribute<Book, Author> authors;

}

