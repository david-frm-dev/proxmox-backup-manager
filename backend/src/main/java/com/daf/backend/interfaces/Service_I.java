package com.daf.backend.interfaces;

import java.util.List;
import java.util.UUID;

/**
 * Defines the used methods
 *
 * @param <T> The ReturnType (e.g. BackupJob)
 * @param <G> The inputType (e.g. BackupJobDto)
 * */
public interface Service_I<T, G> {
    public List<T> findAll();

    /**
     * Finds a single {@link T} by its ID.
     *
     * @param id the ID of the{@link T} to look up
     * @return the{@link T} with the given ID
     * @throws java.util.NoSuchElementException if no{@link T} with the given ID exists
     */
    public T findById(UUID id);

    /**
     * Creates a new {@link T} from the given DTO and persists it.
     *
     * @param dto the data used to create the {@link T}
     * @return the newly created and persisted {@link T}
     */
    public T create(G dto);

    /**
     * Updates a {@link T} from the given ID and DTO
     *
     * @param id is used to find the {@link T}
     * @param dto holds the changes values
     *
     * @return the updated {@link T}
     * */
    public T update(UUID id, G dto);

    /**
     * Deletes the {@link T}
     *
     * @param id is used to find the {@link T}
     * */
    public void delete(UUID id);
}
