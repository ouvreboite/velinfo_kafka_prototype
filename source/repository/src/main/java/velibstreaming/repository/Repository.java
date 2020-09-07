package velibstreaming.repository;

public interface Repository<K> {
    class RepositoryException extends Exception{
        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    void persist(K object) throws RepositoryException;
}
