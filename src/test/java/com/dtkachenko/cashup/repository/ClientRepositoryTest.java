package com.dtkachenko.cashup.repository;


import com.dtkachenko.cashup.model.Client;
import com.dtkachenko.cashup.model.Currency;
import com.dtkachenko.cashup.model.Order;
import com.dtkachenko.cashup.model.OrderState;
import com.dtkachenko.cashup.model.Sex;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClientRepositoryTest extends AbstractRepositoryTest<ClientRepository> {

    private static Client john;
    private static Client helen;

    private static Order orderJohn;
    private static Order orderHelen;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeClass
    public static void init(){
        orderJohn = new Order(john, LocalDate.of(2017, 9, 11), OrderState.NEW, 1500, Currency.USD, true);
        orderJohn.setId(1L);
        orderHelen = new Order(helen, LocalDate.of(2017, 8, 11), OrderState.COMPLETED, 3000, Currency.EUR, true);
        orderHelen.setId(2L);

        john = new Client("John", "Snow", LocalDate.of(1986, 07, 11), Sex.MALE, "002459886465");
        john.setId(1L);
        helen = new Client("Helen", "Gara", LocalDate.of(1992, 07, 11), Sex.FEMALE, "002459898696");
        helen.setId(5L);

    }

    @Test
    @Commit
    @DataSet(value = "client/empty.xml", cleanBefore = true, disableConstraints = true)
    @ExpectedDataSet(value = "client/expected-add-clients.xml")
    public void addClient(){
        repository.save(john);
        repository.save(helen);
    }

    @Test
    @DataSet(value = "client/stored-client.xml", cleanBefore = true, disableConstraints = true)
    public void allClientsAreFound(){
        assertThat(repository.findAll(), hasItems(john, helen));
    }

    @Test
    @DataSet(value = "client/stored-client.xml", cleanBefore = true, disableConstraints = true)
    public void clientsCanBeFound() {
        assertEquals(repository.findOne(1L), john);
        assertThat(repository.exists(4L), is(true));
        assertEquals(repository.findByFirstName("Anton").size(), 2);
        assertEquals(repository.findOne(1L).getOrders().size(), 2);
        assertThat(repository.findOne(1L).getOrders(), hasItems(orderJohn));
    }

    @Test
    @DataSet(value = "client/stored-client.xml", cleanBefore = true, disableConstraints = true)
    public void ifNoClientFoundReturnNull() {
        assertThat(repository.findOne(236L), nullValue());
    }

    @Test
    @DataSet(value = "client/empty.xml", cleanBefore = true, disableConstraints = true)
    public void ifNoUsersFoundReturnEmptyList() {
        assertThat(repository.findAll(), Matchers.is(empty()));
    }

    @Test
    @Commit
    @DataSet(value = "client/stored-client.xml", cleanBefore = true, disableConstraints = true)
    @ExpectedDataSet("client/expected-clients.xml")
    public void shouldDeleteByEntity() {
        repository.delete(2L);
        repository.delete(3L);
        repository.delete(4L);
        repository.delete(helen);
    }
}
