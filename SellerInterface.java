/**
 * Create a new store.
 */

public interface SellerInterface {
            void createStore();

            /**
             * Enter an existing store.
             */
            void enterStore();

            /**
             * Load stores from a file.
             */
            void loadStoresFromFile();

            /**
             * Save stores to a file.
             */
            void saveStoresToFile();
}


