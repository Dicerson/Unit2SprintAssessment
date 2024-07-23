package com.amazon.ata.deliveringonourpromise.TCTtest.taskcompletion.mastery.task2;

import com.amazon.ata.deliveringonourpromise.App;
import com.amazon.ata.deliveringonourpromise.dao.OrderDao;
import com.amazon.ata.deliveringonourpromise.data.OrderDatastore;
import com.amazon.ata.deliveringonourpromise.ordermanipulationauthority.OrderManipulationAuthorityClient;
import com.amazon.ata.ordermanipulationauthority.OrderManipulationAuthority;
import com.amazon.ata.test.helper.AtaTestHelper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MasteryTaskTwoTests {

    private OrderManipulationAuthorityClient client;

    @BeforeMethod
    private void setup() {
        this.client = App.getOrderManipulationAuthorityClient();
    }

    @Test
    public void masteryTaskTwo_masteryTaskTwoFile_existsAndIncludesMultipleTests() {
        // GIVEN
        String masteryTaskTwoFileName = "OrderDaoTestPlan.md";

        // WHEN
        String content = AtaTestHelper.getFileContentFromResources(masteryTaskTwoFileName);
        Pattern methodNames = Pattern.compile("get_[^_]+_.+");
        Matcher methodNameMatcher = methodNames.matcher(content);
        List<String> matches = new ArrayList<>();
        while (methodNameMatcher.find()) {
            matches.add(methodNameMatcher.group());
        }

        // THEN
        assertThat(matches.size())
            .as("Expected multiple test names in OrderDaoTestPlan.md!")
            .isGreaterThan(1);
    }

    @Test
    public void OrderDaoGet_forKnownOrderID_returnsOrder(){
        //GIVEN An order ID that we know exists
        String orderID = "900-3746401-0000001";

        //WHEN We call 'get()' with that order ID
        OrderDao orderDao = new OrderDao(client);

        //THEN The result is not null
        assertTrue(null != orderDao.get(orderID));
    }

    @Test
    public void OrderDaoGet_forNullID_returnsOrder(){
        //GIVEN An order ID that we know exists
        String orderID = null;

        //WHEN We call 'get()' with that order ID
        OrderDao orderDao = new OrderDao(client);

        //THEN The result is not null
        assertNull(orderDao.get(orderID));
    }

    @Test
    public void OrderDaoGet_forNonexistentID_returnsOrder(){
        //GIVEN An order ID that we know does not exist
        String orderID = "900-0000000-0000000";

        //WHEN We call 'get()' with that order ID
        OrderDao orderDao = new OrderDao(client);

        //THEN The result is not null
        assertNull(orderDao.get(orderID));
    }

    @Test
    public void OrderDaoGet_forInvalidIDFormat_returnsOrder(){
        //GIVEN An invalid string input
        String orderID = "babies";

        //WHEN We call 'get()' with that order ID
        OrderDao orderDao = new OrderDao(client);

        //THEN The result is not null
        assertNull(orderDao.get(orderID));
    }
}
