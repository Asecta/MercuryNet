package com.pandoaspen.mercury.common.utils;

import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtils {

    public static <I, O, R extends Collection<O>> R remap(Function<I, O> mapper, Supplier<R> listSupplier, Spliterator<I> spliterator) {
        return StreamSupport.stream(spliterator, true).map(mapper).collect(Collectors.toCollection(listSupplier));
    }

    public static <I, O, R extends Collection<O>> R remap(Function<I, O> mapper, Supplier<R> listSupplier, Iterable<I> iterable) {
        return remap(mapper, listSupplier, iterable.spliterator());
    }

    public static <I, O, R extends Collection<O>> R remap(Function<I, O> mapper, Supplier<R> listSupplier, I... args) {
        return remap(mapper, listSupplier, Spliterators.spliterator(args, 0, args.length, 1));
    }

    public static <I, O> List<O> remap(Function<I, O> mapper, I... args) {
        return remap(mapper, ArrayList::new, args);
    }

    public static <T, U extends Comparable> Stream<T> resolveSortStream(Collection<T> collection, Function<T, U> provider, Predicate<U> filter) {
        return collection.parallelStream().map(e -> new ResolvedEntry<>(e, provider.apply(e))).filter(re -> filter.test(re.getValue())).sorted().map(e -> e.getObject());
    }


    public static <T, U extends Comparable> LinkedList<T> resolveSort(Collection<T> collection, Function<T, U> provider, Predicate<U> filter, int limit) {
        return resolveSortStream(collection, provider, filter).limit(limit).collect(Collectors.toCollection(LinkedList::new));
    }

    public static <T, U extends Comparable> Stream<T> resolveSortStream(Collection<T> collection, Function<T, U> provider) {
        return collection.parallelStream().map(e -> new ResolvedEntry<>(e, provider.apply(e))).sorted().map(e -> e.getObject());
    }

    public static <T> LinkedList<T> resolveSort(Collection<T> collection, Function<T, ? extends Comparable> provider, int limit) {
        return resolveSortStream(collection, provider).limit(limit).collect(Collectors.toCollection(LinkedList::new));
    }

    public static <T> T findAny(Collection<T> collection, Predicate<T> filter) {
        return collection.stream().filter(filter).findAny().orElse(null);
    }

    @Data
    private static class ResolvedEntry<T, U extends Comparable> implements Comparable<ResolvedEntry<T, U>> {

        private final T object;
        private final U value;

        @Override
        public int compareTo(ResolvedEntry<T, U> o) {
            return value.compareTo(o.value);
        }
    }
}