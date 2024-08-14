package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class PriorityProvider<T> implements Iterable<T> {

  private Collection<T> backingList;
  private Function<Collection<T>, T> priorityFunction;

  public PriorityProvider(Collection<T> backingList, Function<Collection<T>, T> priorityProvider) {
    this.backingList = backingList;
    this.priorityFunction = priorityProvider;
  }

  @Override
  public Iterator<T> iterator() {
    return new PriorityIterator<>(Lists.newArrayList(backingList), priorityFunction);
  }

  private static class PriorityIterator<T> implements Iterator<T> {

    private Collection<T> backingList;
    private Function<Collection<T>, T> priorityFunction;
    private boolean prioritized = true;

    private PriorityIterator(Collection<T> backingList, Function<Collection<T>, T> priorityProvider) {
      this.backingList = backingList;
      this.priorityFunction = priorityProvider;
    }

    @Override
    public boolean hasNext() {
      return !backingList.isEmpty();
    }

    @Override
    public T next() {
      if (prioritized) {
        T element = priorityFunction.apply(this.backingList);
        if (element != null) {
          this.backingList.remove(element);
          return element;
        } else {
          this.prioritized = false;
        }
      }
      T next = Iterables.getFirst(this.backingList, null);
      if (next != null) {
        this.backingList.remove(next);
      }
      return next;
    }

  }
}
