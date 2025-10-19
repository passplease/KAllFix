package n1luik.K_multi_threading.core.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ListRemoveIterator<T> implements Iterator<T> {
    protected final List<T> list;
    protected final Iterator<T> iterator;
    protected T last;

    public ListRemoveIterator(List<T> list, Iterator<T> iterator) {
        this.list = list;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return last = iterator.next();
    }

    @Override
    public void remove() {
        list.remove(last);
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        iterator.forEachRemaining(action);
    }
}
