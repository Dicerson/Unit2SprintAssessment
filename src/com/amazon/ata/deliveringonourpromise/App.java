package com.amazon.ata.deliveringonourpromise;

import com.amazon.ata.deliveringonourpromise.activity.GetPromiseHistoryByOrderIdActivity;
import com.amazon.ata.deliveringonourpromise.dao.OrderDao;
import com.amazon.ata.deliveringonourpromise.dao.PromiseDao;
import com.amazon.ata.deliveringonourpromise.data.OrderDatastore;
import com.amazon.ata.deliveringonourpromise.deliverypromiseservice.DeliveryPromiseServiceClient;
import com.amazon.ata.deliveringonourpromise.orderfulfillmentservice.OrderFulfillmentServiceClient;
import com.amazon.ata.deliveringonourpromise.ordermanipulationauthority.OrderManipulationAuthorityClient;
import com.amazon.ata.deliveringonourpromise.promisehistoryservice.PromiseHistoryClient;
import com.amazon.ata.deliverypromiseservice.service.DeliveryPromiseService;
import com.amazon.ata.orderfulfillmentservice.OrderFulfillmentService;
import com.amazon.ata.ordermanipulationauthority.OrderManipulationAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides inversion of control for the DeliveringOnOurPromise project by instantiating all of the
 * dependencies needed by the Shell and its dependency classes.
 */
public class App {
    /* don't instantiate me */
    private App() {}

    /**
     * Fetch a new PromiseHistoryClient with all of its dependencies loaded for use in the Shell!
     * @return fully loaded PromiseHistoryClient, ready for service! (er, client)
     */
    public static PromiseHistoryClient getPromiseHistoryClient() {
        return new PromiseHistoryClient(getPromiseHistoryByOrderIdActivity());
    }

    /* helpers */

    public static GetPromiseHistoryByOrderIdActivity getPromiseHistoryByOrderIdActivity() {
        return new GetPromiseHistoryByOrderIdActivity(getOrderDao(), getPromiseDao());
    }

    /**
     * Fetches the OrderDao from the OMAclient.
     * @return the OrderDao
     */
    // DAOs
    public static OrderDao getOrderDao() {
        return new OrderDao(getOrderManipulationAuthorityClient());
    }

    /**
     * Generates a list of all known clients and puts one instance of each into the PromiseDAO.
     * @return the PromiseDAO
     */
    public static PromiseDao getPromiseDao() {
        List<ServiceClient> clients = new ArrayList<>();
        clients.add(getDeliveryPromiseServiceClient());
        clients.add(getOrderFulfillmentServiceClient());
        return new PromiseDao(clients,
                              getOrderManipulationAuthorityClient()
        );
    }

    // service clients
    public static OrderManipulationAuthorityClient getOrderManipulationAuthorityClient() {
        return new OrderManipulationAuthorityClient(getOrderManipulationAuthority());
    }
    public static DeliveryPromiseServiceClient getDeliveryPromiseServiceClient() {
        return new DeliveryPromiseServiceClient(getDeliveryPromiseService());
    }
    public static OrderFulfillmentServiceClient getOrderFulfillmentServiceClient() {
        return new OrderFulfillmentServiceClient(getOrderFulfillmentService());
    }

    // dependency services
    public static OrderManipulationAuthority getOrderManipulationAuthority() {
        return new OrderManipulationAuthority(getOrderDatastore());
    }
    public static DeliveryPromiseService getDeliveryPromiseService() {
        return new DeliveryPromiseService(getOrderDatastore());
    }
    public static OrderFulfillmentService getOrderFulfillmentService() {
        return new OrderFulfillmentService(getOrderDatastore(), getDeliveryPromiseService());
    }

    // sample data
    public static OrderDatastore getOrderDatastore() {
        return OrderDatastore.getDatastore();
    }
}
