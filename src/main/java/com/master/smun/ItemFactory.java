package com.master.smun;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ItemFactory<T extends Comparable> {

    /**
     * The only constructor
     */
    public ItemFactory() {
    }
    
    /**
     * The pool where all the items are kept
     */
    private Map<T, Item> pool = new HashMap<T, Item>();

    /**
     * Method to obtain the reference of an item if this has already been
     * created
     * @param key the key of the item
     * @return the item
     */
    public Item getItem(T key) {
        Item item = pool.get(key);
        if (item == null) {
            item = new Item(key);
            pool.put(key, item);
        }
        return item;
    }
}
