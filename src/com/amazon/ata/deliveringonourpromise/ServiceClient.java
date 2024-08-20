package com.amazon.ata.deliveringonourpromise;

import com.amazon.ata.deliveringonourpromise.types.Promise;

public interface ServiceClient {
    /**
     * The generic Promise method that all Service Clients need
     * @param customerOrderItemId the id of an Order
     * @return a Promise object tied to the specified order
     */
    Promise getDeliveryPromiseByOrderItemId(String customerOrderItemId);
}
