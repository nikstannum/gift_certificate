package ru.clevertec.ecl.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.ecl.integration.BaseIntegrationTest;
import ru.clevertec.ecl.service.impl.GiftCertificateServiceImpl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class RestCertificateControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private GiftCertificateServiceImpl service;


    @Test
    void checkDeleteShouldReturnStatus204() throws Exception {
        mvc.perform(delete("/api/certificates/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkCreateShouldReturnCertificateAndStatus201() throws Exception {
        mvc.perform(post("/api/certificates")
                        .param("cert", "name:name,descr:description,price:123.45,duration:1")
                        .param("tag", "name:health,name:beauty"))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.price", is(123.45)))
                .andExpect(jsonPath("$.duration", is(1)))
                .andExpect(jsonPath("$.tags[*].name", containsInAnyOrder("health", "beauty")));
    }

    @Test
    void checkUpdateShouldReturnCertificateAndStatus201() throws Exception {
        mvc.perform(put("/api/certificates/{id}", 1)
                        .param("cert", "name:name,descr:description,price:123.45,duration:1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.price", is(123.45)))
                .andExpect(jsonPath("$.duration", is(1)));
    }

    @Test
    void checkFindByParamsShouldReturnCertificateAndStatus200() throws Exception {
        mvc.perform(get("/api/certificates").
                        param("cert", "name:like:ydi"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name", is("skydiving")));
    }

    @Test
    void checkFindAllShouldReturnStatus200() throws Exception {
        mvc.perform(get("/api/certificates/all")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(5));
    }


    @Test
    void checkGetByIdShouldReturnCertificateAndStatus200() throws Exception {
        mvc.perform(get("/api/certificates/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("skydiving")));
    }

    @Test
    void checkGetByIdShouldReturnStatus404() throws Exception {
        mvc.perform(get("/api/certificates/100"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("40412")));
    }

    @Test
    void checkUpdatePriceShouldReturnPrice100AndStatus200() throws Exception {
        mvc.perform(patch("/api/certificates/{id}", 1)
                        .param("price", "100"))
                .andExpect(status().isAccepted())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.price", is(100)));
    }


}
