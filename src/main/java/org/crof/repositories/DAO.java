package org.crof.repositories;

import java.util.Map;

public interface DAO<S,T> {
    S save(T record);
    T load(S id);
    Map<S,T> loadAll();
}
