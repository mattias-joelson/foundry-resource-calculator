package org.joelson.mattias.foundry.resource_calculator.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ListUtil {

    private ListUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static <T extends Comparable<T>> List<T> requireUniqueMembers(List<T> list) {
        return requireUniqueMembers(list, t -> t);
    }

    public static <T, R extends Comparable<R>> List<T> requireUniqueMembers(
            List<T> list, Function<T, R> getter) {
        Set<R> set = new HashSet<>();
        if (list == null) {
            throw new IllegalArgumentException("List is null");
        }
        for (T t : list) {
            R r = getter.apply(t);
            if (set.contains(r)) {
                throw new IllegalArgumentException("List contain same element: " + t);
            }
            set.add(r);
        }
        return list;
    }
}
