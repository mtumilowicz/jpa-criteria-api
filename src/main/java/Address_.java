import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Address.class)
public abstract class Address_ {

	public static volatile SingularAttribute<Address, String> country;
	public static volatile SingularAttribute<Address, Bookstore> bookstore;
	public static volatile SingularAttribute<Address, String> city;
	public static volatile SingularAttribute<Address, String> street;
	public static volatile SingularAttribute<Address, Integer> id;

}

