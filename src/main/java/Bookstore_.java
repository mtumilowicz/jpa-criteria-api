import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Bookstore.class)
public abstract class Bookstore_ {

	public static volatile SingularAttribute<Bookstore, Address> address;
	public static volatile ListAttribute<Bookstore, Book> books;
	public static volatile SingularAttribute<Bookstore, String> name;
	public static volatile SingularAttribute<Bookstore, Integer> id;

}

