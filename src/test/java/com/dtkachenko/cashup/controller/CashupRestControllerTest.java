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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

//    private String userName = "user";

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
//        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();

        this.orderRepository.deleteAll();
        this.clientRepository.deleteAll();

        this.client = new Client("John", "Snow", Sex.MALE, "002459886465");
        this.client.addOrder(new Order(this.client, LocalDate.of(2017, 9, 11), OrderState.NEW, 1500, Currency.USD, true));
        this.client.addOrder(new Order(this.client, LocalDate.of(2017, 10, 11), OrderState.NEW, 2500, Currency.EUR, true));

        clientRepository.save(client);
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/api/clients/")
                .content(this.json(new Client("Helen", "Gara", Sex.FEMALE, "002459898696")))
                .contentType(contentType))
                .andExpect(status().isFound());

        ResultMatcher strm = status().isFound();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void readSingleOrder() throws Exception {

        mockMvc.perform(get("/api/clients/" + this.client.getId() + "/orders/"
                + this.client.getOrders().get(0).getId()))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.orderState", is(OrderState.NEW.toString())))
                .andExpect(jsonPath("$.amount", is(1500)))
                .andExpect(jsonPath("$.currency", is(Currency.USD.toString())))
                .andExpect(jsonPath("$.confirmed", is(true)));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void readOrders() throws Exception {
        mockMvc.perform(get("/api/clients/" + this.client.getId() + "/orders/"))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void createOrder() throws Exception {
        String orderJson = json(new Order(this.client, LocalDate.of(2017, 7, 11), OrderState.NEW, 4500, Currency.USD, true));

        this.mockMvc.perform(post("/api/clients/" + this.client.getId() + "/orders/")
                .contentType(contentType)
                .content(orderJson))
                .andExpect(status().isCreated());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}