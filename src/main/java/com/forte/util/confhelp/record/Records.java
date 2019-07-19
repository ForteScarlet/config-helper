package com.forte.util.confhelp.record;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class Records extends LinkedHashSet<Record> {

    public Record get(String name){
        for (Record record : this) {
            if(record.getName().equals(name)){
                return record;
            }
        }
        return null;
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the linked hash set
     * @param loadFactor      the load factor of the linked hash set
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero, or if the load factor is nonpositive
     */
    public Records(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the LinkedHashSet
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero
     */
    public Records(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     */
    public Records() {
    }

    /**
     * Constructs a new linked hash set with the same elements as the
     * specified collection.  The linked hash set is created with an initial
     * capacity sufficient to hold the elements in the specified collection
     * and the default load factor (0.75).
     *
     * @param c the collection whose elements are to be placed into
     *          this set
     * @throws NullPointerException if the specified collection is null
     */
    public Records(Collection<? extends Record> c) {
        super(c);
    }

}
