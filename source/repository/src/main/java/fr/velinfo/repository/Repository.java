package fr.velinfo.repository;

import java.util.Collection;

public interface Repository<K> {
    class RepositoryException extends Exception{
        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    void persist(Collection<K> objects) throws RepositoryException;
}
