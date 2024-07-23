package com.amazon.ata.deliveringonourpromise.dao;
/**
 * DAO interface to abstract calls.
 */
public interface ReadOnlyDao<X, Y> {
    /**
     * Get object method to be implemented.
     * @param orderId Order Id
     * @return Object abstracted object
     */
    Y get(X orderId);
}
