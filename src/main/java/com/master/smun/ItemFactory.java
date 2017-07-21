package com.master.smun;

import java.util.HashMap;
import java.util.Map;

public class ItemFactory {
    /**
     * The pool where all the items are kept
     */
    private Map<Integer, Item> pool = new HashMap<Integer, Item>();

    /**
     * Method to obtain the reference of an item if this has already been
     * created
     * @param key the key of the item
     * @return the item
     */
    public Item getItem(Integer key) {
        Item item = pool.get(key);
        if (item == null) {
            item = new Item(key);
            pool.put(key, item);
        }
        return item;
    }
}
