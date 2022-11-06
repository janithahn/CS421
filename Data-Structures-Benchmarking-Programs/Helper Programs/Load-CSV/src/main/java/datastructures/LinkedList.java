package datastructures;

import java.util.List;

public class LinkedList implements Membership {

    private static final List<Integer> LINKED_LIST = new java.util.LinkedList<>();

    @Override
    public boolean isMember(int x) {
        return LINKED_LIST.get(x) != null;
    }

    @Override
    public void insert(int x) {
        LINKED_LIST.add(x);
    }

    @Override
    public void delete(int x) {
        LINKED_LIST.remove(x);
    }
}
