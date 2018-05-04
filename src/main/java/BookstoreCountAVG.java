/**
 * Created by mtumilowicz on 2018-05-04.
 */
public class BookstoreCountAVG {
    private final Bookstore bookstore;
    private final long bookCount;
    private final double priceAvg;

    public BookstoreCountAVG(Bookstore bookstore, long bookCount, double priceAvg) {
        this.bookstore = bookstore;
        this.bookCount = bookCount;
        this.priceAvg = priceAvg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookstoreCountAVG that = (BookstoreCountAVG) o;

        if (bookCount != that.bookCount) return false;
        if (Double.compare(that.priceAvg, priceAvg) != 0) return false;
        return bookstore != null ? bookstore.equals(that.bookstore) : that.bookstore == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = bookstore != null ? bookstore.hashCode() : 0;
        result = 31 * result + (int) (bookCount ^ (bookCount >>> 32));
        temp = Double.doubleToLongBits(priceAvg);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BookstoreCountAVG{" +
                "bookstore=" + bookstore +
                ", bookCount=" + bookCount +
                ", priceAvg=" + priceAvg +
                '}';
    }
}