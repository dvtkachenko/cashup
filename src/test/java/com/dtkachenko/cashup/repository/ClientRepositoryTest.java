package com.dtkachenko.cashup.repository;


import com.dtkachenko.cashup.model.Client;
import com.dtkachenko.cashup.model.Sex;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClientRepositoryTest extends AbstractRepositoryTest<ClientRepository>{

    private static Client john;
    private static Client helen;

    @BeforeClass
    public static void init(){
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
    public void clientsCanBeFoundById() {
        assertEquals(repository.findOne(1L), john);
        assertThat(repository.exists(4L), is(true));
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
