package com.roodie.model.repository;

import com.roodie.model.entities.Entity;

import java.util.Collection;
import java.util.List;

/**
 * Created by Roodie on 11.03.2016.
 */
public interface Repository<T extends Entity> {

    /**
     * Retrieves an {@link Entity} from the repository
     * with the given id is not found
     *
     * @param id the id for the {@link Entity} to retrieve
     * @return the {@link Entity} retrieved.
     */
    T get(String id);

    /**
     * Adds an {@link Entity} to the repository.
     *
     * @param item The {@link Entity} to add.
     */
    void add(T item);

    /**
     * Adds a collection of {@link Entity}s to the repository.
     *
     * @param items The {@link Entity}s to add.
     */
    void addAll(Collection<T> items);

    /**
     * Update an {@link Entity} on the repository.
     *
     * @param item The {@link Entity} to update.
     */
    void update(T item);

    /**
     * Removes an {@link Entity} from the repository.
     *
     * @param item The {@link Entity} to remove
     */
    void remove(T item);

    /**
     * Removes all the {@link Entity}s that the repository has.
     */
    void removeAll();

    void removeAll(Collection<T> items);

    /**
     * @param fieldName
     * @param values
     * @return items the items with the fieldName that match with values.
     */
    List<T> findByField(String fieldName, Object... values);

    /**
     * Obtains a list containing all the {@link Entity}s in the repository
     *
     * @return the list of {@link Entity}s
     */
    List<T> getAll();

    /**
     * @param ids
     * @return All the items with the ids
     */
    List<T> getAll(List<String> ids);

    /**
     * Removes the {@link Entity} with the id
     *
     * @param id The {@link Entity} id to be removed
     */
    void remove(String id);

    /**
     * @return If the repository has data or not
     */
    Boolean isEmpty();

    Long getSize();

    /**
     * Replaces all the {@link Entity}s in the repository by new ones.
     *
     * @param items The new {@link Entity}s to replace the old ones.
     */
    void replaceAll(Collection<T> items);

    /**
     * @return The unique instance
     */
    T getUniqueInstance();
}
