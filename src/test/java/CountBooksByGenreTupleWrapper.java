import javax.persistence.Tuple;
import java.util.Objects;

/**
 * Created by mtumilowicz on 2018-05-22.
 */
class CountBooksByGenreTupleWrapper {
    private final WritingGenre genre;
    private final Long count;

    CountBooksByGenreTupleWrapper(Tuple tuple) {
        this.genre = Objects.requireNonNull(tuple.get("genre", WritingGenre.class));
        this.count = Objects.requireNonNull(tuple.get("count", Long.class));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountBooksByGenreTupleWrapper that = (CountBooksByGenreTupleWrapper) o;

        if (genre != that.genre) return false;
        return count.equals(that.count);
    }
}
