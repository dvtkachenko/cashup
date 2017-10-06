package com.dtkachenko.cashup.repository;


import com.dtkachenko.cashup.model.Client;
import com.dtkachenko.cashup.model.Currency;
import com.dtkachenko.cashup.model.Order;
import com.dtkachenko.cashup.model.OrderState;
import com.dtkachenko.cashup.model.Sex;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class OrderRepositoryTest extends AbstractRepositoryTest<OrderRepository> {

    private static Client john;
    private static Client helen;

    private static Order orderJohn;
    private static Order orderHelen;

    @BeforeClass
    public static void init(){

        john = new Client("John", "Snow", LocalDate.of(1986, 07, 11), Sex.MALE, "002459886465");
        john.setId(1L);
        helen = new Client("Helen", "Gara", LocalDate.of(1992, 07, 11), Sex.FEMALE, "002459898696");
        helen.setId(5L);

        orderJohn = new Order(john, LocalDate.of(2017, 9, 11), OrderState.NEW, 1500, Currency.USD, true);
        orderJohn.setId(1L);
        orderHelen = new Order(helen, LocalDate.of(2017, 8, 11), OrderState.COMPLETED, 3000, Currency.EUR, true);
        orderHelen.setId(2L);
    }

    @Test
    @Commit
    @DataSet(value = "order/empty.xml", cleanBefore = true, disableConstraints = true)
    @ExpectedDataSet(value = "order/expected-add-orders.xml")
    public void addOrder(){
        repository.save(orderJohn);
        repository.save(orderHelen);
    }

    @Test
    @DataSet(value = "order/stored-orders.xml", cleanBefore = true, disableConstraints = true)
    public void allOrdersAreFound(){
        assertThat(repository.findAll(), hasItems(orderJohn, orderHelen));
    }

    @Test
    @DataSet(value = "order/stored-orders.xml", cleanBefore = true, disableConstraints = true)
    public void ordersCanBeFound() {
        assertEquals(repository.findOne(1L), orderJohn);
        assertThat(repository.exists(4L), is(true));
        assertThat(repository.findOne(2L).getClient(), is(helen));
        assertEquals(repository.findByOrderDate(LocalDate.of(2017,9,11)).size(), 2);
    }

    @Test
    @DataSet(value = "order/stored-orders.xml", cleanBefore = true, disableConstraints = true)
    public void ifNoOrderFoundReturnNull() {
        assertThat(repository.findOne(236L), nullValue());
    }

    @Test
    @DataSet(value = "order/empty.xml", cleanBefore = true, disableConstraints = true)
    public void ifNoOrdersFoundReturnEmptyList() {
        assertThat(repository.findAll(), Matchers.is(empty()));
    }

    @Test
    @Commit
    @DataSet(value = "order/stored-orders.xml", cleanBefore = true, disableConstraints = true)
    @ExpectedDataSet("order/expected-orders.xml")
    public void shouldDeleteByEntity() {
        repository.delete(orderHelen);
        repository.delete(3L);
        repository.delete(4L);
        repository.delete(5L);
    }
}
