package com.dtkachenko.cashup.controller;

import com.dtkachenko.cashup.CashupApplication;
import com.dtkachenko.cashup.model.Client;
import com.dtkachenko.cashup.model.Currency;
import com.dtkachenko.cashup.model.Order;
import com.dtkachenko.cashup.model.OrderState;
import com.dtkachenko.cashup.model.Sex;
import com.dtkachenko.cashup.repository.ClientRepository;
import com.dtkachenko.cashup.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashupApplication.class)
@WebAppConfiguration
public class CashupRestControllerTest {

    private MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    private MediaType associationType = new MediaType("text", "uri-list", Charset.forName("UTF-8"));

    //    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
//            MediaType.APPLICATION_JSON.getSubtype(),
//            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Client client;

    private List<Order> orderList = new ArrayList<>();

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();

        this.orderRepository.deleteAll();
        this.clientRepository.deleteAll();

        this.client = new Client("John", "Snow", Sex.MALE, "002459886465");
        this.client.addOrder(new Order(this.client, LocalDate.of(2017, 9, 11), OrderState.COMPLETED, 1500, Currency.USD, true));
        this.client.addOrder(new Order(this.client, LocalDate.of(2017, 10, 11), OrderState.NEW, 2500, Currency.EUR, false));

        clientRepository.save(client);
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void userIsUpdated() throws Exception {
        mockMvc.perform(patch("/api/clients/" + this.client.getId())
                .content(this.json(new Client("Helen", "Gara", Sex.FEMALE, "002459898696")))
                .contentType(contentType))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/api/clients/" + this.client.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.firstName", is("Helen")))
                .andExpect(jsonPath("$.lastName", is("Gara")))
                .andExpect(jsonPath("$.sex", is(Sex.FEMALE.toString())))
                .andExpect(jsonPath("$.inn", is("002459898696")));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void userIsCreated() throws Exception {
        mockMvc.perform(post("/api/clients/")
                .content(this.json(new Client("Helen", "Gara", Sex.FEMALE, "002459898696")))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void readSingleOrder() throws Exception {

        mockMvc.perform(get("/api/clients/" + this.client.getId() + "/orders/"
                + this.client.getOrders().get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.orderState", is(OrderState.COMPLETED.toString())))
                .andExpect(jsonPath("$.amount", is(1500)))
                .andExpect(jsonPath("$.currency", is(Currency.USD.toString())))
                .andExpect(jsonPath("$.confirmed", is(true)));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void readOrders() throws Exception {
        mockMvc.perform(get("/api/clients/" + this.client.getId() + "/orders/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.orders", hasSize(2)))
                .andExpect(jsonPath("$._embedded.orders[0].orderState", is(OrderState.COMPLETED.toString())))
                .andExpect(jsonPath("$._embedded.orders[0].amount", is(1500)))
                .andExpect(jsonPath("$._embedded.orders[0].currency", is(Currency.USD.toString())))
                .andExpect(jsonPath("$._embedded.orders[0].confirmed", is(true)))
                .andExpect(jsonPath("$._embedded.orders[1].orderState", is(OrderState.NEW.toString())))
                .andExpect(jsonPath("$._embedded.orders[1].amount", is(2500)))
                .andExpect(jsonPath("$._embedded.orders[1].currency", is(Currency.EUR.toString())))
                .andExpect(jsonPath("$._embedded.orders[1].confirmed", is(false)));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void createOrder() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post("/api/orders/")
                .content(this.json(new Order(this.client, OrderState.NEW, 4500, Currency.USD, true)))
                .contentType(contentType))
                .andExpect(status().isCreated()).andReturn();

        String association = mvcResult.getResponse().getHeader("Location") + "/client";

        this.mockMvc.perform(put(association)
                .content("/api/clients/" + this.client.getId())
                .contentType(associationType))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/api/clients/" + this.client.getId() + "/orders/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.orders", hasSize(3)))
                .andExpect(jsonPath("$._embedded.orders[2].orderState", is(OrderState.NEW.toString())))
                .andExpect(jsonPath("$._embedded.orders[2].amount", is(4500)))
                .andExpect(jsonPath("$._embedded.orders[2].currency", is(Currency.USD.toString())))
                .andExpect(jsonPath("$._embedded.orders[2].confirmed", is(true)));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}