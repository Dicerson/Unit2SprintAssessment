package com.amazon.ata.deliveringonourpromise.dao;

import com.amazon.ata.deliveringonourpromise.ServiceClient;
import com.amazon.ata.deliveringonourpromise.ordermanipulationauthority.OrderManipulationAuthorityClient;
import com.amazon.ata.deliveringonourpromise.types.Promise;
import com.amazon.ata.ordermanipulationauthority.OrderResult;
import com.amazon.ata.ordermanipulationauthority.OrderResultItem;
import com.amazon.ata.ordermanipulationauthority.OrderShipment;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Promises.
 */
public class PromiseDao implements ReadOnlyDao<String, List<Promise>> {
    private ArrayList<ServiceClient> serviceClients;
    private OrderManipulationAuthorityClient omaClient;

    /**
     * PromiseDao constructor, creating a dao when only a single service client is given.
     * @param serviceClient DeliveryPromiseServiceClient for DAO to access DPS
     * @param omaClient OrderManipulationAuthorityClient for DAO to access OMA
     */
    public PromiseDao(ServiceClient serviceClient, OrderManipulationAuthorityClient omaClient) {
        this.serviceClients.add(serviceClient);
        this.omaClient = omaClient;
    }
    /**
     * PromiseDao constructor, accepting an ArrayList of service clients for use of multiple clients.
     * @param serviceClients DeliveryPromiseServiceClient for DAO to access DPS
     * @param omaClient OrderManipulationAuthorityClient for DAO to access OMA
     */
    public PromiseDao(ArrayList<ServiceClient> serviceClients, OrderManipulationAuthorityClient omaClient) {
        this.serviceClients = serviceClients;
        this.omaClient = omaClient;
    }
    /**
     * Function to add a new client to the PromiseDao.
     * @param client the new client to add
     * @return the index of the new client
     */
    public int addClient(ServiceClient client) {
        this.serviceClients.add(client);
        return serviceClients.indexOf(client);
    }
    /**
     * Function to add a new client to the PromiseDao at a specified index.
     * @param index the index to add the new client at
     * @param client the new client to add
     * @return the index of the new client
     * @throws IndexOutOfBoundsException Thrown when the given index is invalid
     */
    public int addClient(int index, ServiceClient client) {
        if (index < 0 || index >= this.serviceClients.size()) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        this.serviceClients.add(client);
        return serviceClients.indexOf(client);
    }
    /**
     * Function to add multiple new clients to the Dao, returns the index of the last client.
     * @param clients the list of new clients to add
     * @return the index of the last client added
     */
    public int addClients(ArrayList<ServiceClient> clients) {
        for (int i = 0; i < clients.size(); i++) {
            this.serviceClients.add(clients.get(i));
        }
        return serviceClients.indexOf(clients.get(clients.size()-1));
    }
    /**
     * Function to add multiple new clients to the Dao beginning at a specified index, returns the index of the last client.
     * @param clients the list of new clients to add
     * @param index the index to begin adding at
     * @return the index of the last client added
     * @throws IndexOutOfBoundsException Thrown when the given index is invalid
     */
    public int addClients(int index, ArrayList<ServiceClient> clients) {
        if (index < 0 || index >= this.serviceClients.size()) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        int ind = index;
        for (int i = 0; i < clients.size(); i++) {
            ind += i;
            this.serviceClients.add(index, clients.get(i));
        }
        return serviceClients.indexOf(clients.get(clients.size()-1));
    }
    /**
     * Removes a specified client from the list of Service Clients.
     * @param client
     * @return
     */
    public ServiceClient removeClient(ServiceClient client) {
        this.serviceClients.remove(client);
        return client;
    }
    /**
     * Removes the client at the specified index from the list of service clients.
     * @param index the index of the client to be removed
     * @return the client that was removed
     * @throws IndexOutOfBoundsException Thrown when the given index is invalid
     */
    public ServiceClient removeClient(int index) {
        if (index < 0 || index >= this.serviceClients.size()) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        ServiceClient client = this.serviceClients.get(index);
        this.serviceClients.remove(index);
        return client;
    }

    /**
     * Sets the specified index to the specified client.
     * @param index the index to be set
     * @param client the client to be set
     * @return the client that was replaced
     * @throws IndexOutOfBoundsException Thrown when the given index is invalid
     */
    public ServiceClient setClient(int index, ServiceClient client) {
        if (index < 0 || index >= this.serviceClients.size()) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        ServiceClient oldClient = this.serviceClients.get(index);
        this.removeClient(index);
        this.addClient(index, client);
        return oldClient;
    }
    /**
     * Returns a list of all Promises associated with the given order item ID.
     * @param customerOrderItemId the order item ID to fetch promise for
     * @return a List of promises for the given order item ID
     */
    @Override
    public List<Promise> get(String customerOrderItemId) {
        // Fetch the delivery date, so we can add to any promises that we find
        ZonedDateTime itemDeliveryDate = getDeliveryDateForOrderItem(customerOrderItemId);

        List<Promise> promises = new ArrayList<>();

        // fetch Promise from Delivery Promise Service. If exists, add to list of Promises to return.
        // Set delivery date
        for (int i = 0; i < serviceClients.size(); i++){
            Promise dpsPromise = serviceClients.get(i).getDeliveryPromiseByOrderItemId(customerOrderItemId);
            if (dpsPromise != null) {
                dpsPromise.setDeliveryDate(itemDeliveryDate);
                promises.add(dpsPromise);
            }
        }

        return promises;
    }

    /*
     * Fetches the delivery date of the shipment containing the order item specified by the given order item ID,
     * if there is one.
     *
     * If the order item ID doesn't correspond to a valid order item, or if the shipment hasn't been delivered
     * yet, return null.
     */
    private ZonedDateTime getDeliveryDateForOrderItem(String customerOrderItemId) {
        OrderResultItem orderResultItem = omaClient.getCustomerOrderItemByOrderItemId(customerOrderItemId);

        if (null == orderResultItem) {
            return null;
        }

        OrderResult orderResult = omaClient.getCustomerOrderByOrderId(orderResultItem.getOrderId());

        for (OrderShipment shipment : orderResult.getOrderShipmentList()) {
            for (OrderShipment.ShipmentItem shipmentItem : shipment.getCustomerShipmentItems()) {
                if (shipmentItem.getCustomerOrderItemId().equals(customerOrderItemId)) {
                    return shipment.getDeliveryDate();
                }
            }
        }

        // didn't find a delivery date!
        return null;
    }
}
