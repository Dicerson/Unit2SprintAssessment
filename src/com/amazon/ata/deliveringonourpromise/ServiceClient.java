package com.amazon.ata.deliveringonourpromise;

import com.amazon.ata.deliveringonourpromise.types.Promise;

public interface ServiceClient {
    public Promise getDeliveryPromiseByOrderItemId(String customerOrderItemId);
}
