package com.amazon.ata.deliveringonourpromise.dao;

import com.amazon.ata.deliveringonourpromise.types.Order;
import com.amazon.ata.deliveringonourpromise.types.OrderItem;
import com.amazon.ata.ordermanipulationauthority.OrderCondition;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.testng.Assert.fail;

public class OrderTest {

    private Order testOrder;

    @BeforeEach
    private void setup() {
        OrderItem.Builder builder1 = new OrderItem.Builder();
        builder1.withCustomerOrderItemId("Bob");
        builder1.withOrderId("11023674");
        builder1.withAsin("1234567");
        builder1.withMerchantId("Jahad Ali");
        builder1.withQuantity(100);
        builder1.withTitle("Captain Crunch");
        builder1.withConfidence(1);
        builder1.withIsConfidenceTracked(true);
        OrderItem testOrderItem = builder1.build();

        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        orderItemList.add(testOrderItem);

        ZonedDateTime dateTime = ZonedDateTime.of(1,2,1,1,1,1,1, ZoneId.of("UTC+8"));

        Order.Builder builder2 = new Order.Builder();
        builder2.withCondition(OrderCondition.PENDING);
        builder2.withCustomerOrderItemList(orderItemList);
        builder2.withOrderDate(dateTime);

        testOrder = builder2.build();
    }
    @Test
    public void orderDao_directAccessAttempt_Fails() {
        //GIVEN An Order with numerous variables

        //WHEN Attempting to modify mutable variable objects using indirect access of their pointers via getters
        OrderCondition condition = testOrder.getCondition();
        List<OrderItem> fakeOrderItemList = testOrder.getCustomerOrderItemList();
        ZonedDateTime orderDate = testOrder.getOrderDate();
        condition = null;
        fakeOrderItemList = null;
        orderDate = null;

        //THEN Original values remain unchanged
        if (testOrder.getCondition() == null) {
            fail("Order Condition should not have changed");
        }
        if (testOrder.getCustomerOrderItemList() == null) {
            fail("Customer Order Item List should not have changed");
        }
        if (testOrder.getOrderDate() == null) {
            fail("Order Date should not have changed");
        }
    }
}
