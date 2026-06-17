package com.cl.sns.server.mvc;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@JsonTest
public abstract class SNSMvcBaseIntegrationTest extends SNSBaseIntegrationTest{

}
