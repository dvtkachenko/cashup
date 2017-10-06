package com.dtkachenko.cashup.repository;

import com.github.database.rider.core.DBUnitRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = {"test", "test-repository"})
public abstract class AbstractRepositoryTest<T> {

    @Qualifier("dataSource")
    @Autowired
    protected DataSource dataSource;

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> dataSource.getConnection());

    @Autowired
    protected TestEntityManager em;

    @Autowired
    protected T repository;

}