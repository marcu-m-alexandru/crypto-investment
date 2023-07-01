package com.faptic.cryptoinvestment;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class CryptoInvestmentControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void importData_givenWrongDateFormat_mustReturn500() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/statistics/highest-normalized-range-day")
            .param("localDate", "07/01/2023"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void importData_givenValidDate_mustSucceed() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/statistics/highest-normalized-range-day")
            .param("localDate", "2022-01-01"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(Matchers.blankOrNullString()));
  }
}
