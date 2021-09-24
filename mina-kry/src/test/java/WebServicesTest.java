import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.kry.MainPoller;
import se.kry.model.ServiceIdentity;
import se.kry.model.User;
import se.kry.repository.ServiceRepository;
import se.kry.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@ContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes =  MainPoller.class)
public class WebServicesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getService_test() throws Exception {
        //************************
        //          Given
        //************************
        serviceRepository.save(new ServiceIdentity("http://localhost:8080/service/get/1"));
        //************************
        //          WHEN
        //************************

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/service/get/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //************************
        //          THEN
        //************************
        assertThat(result.getResponse().getContentAsString()).contains("http://localhost:8080/service/get/1");
    }

    @Test
    public void createNewService_test() throws Exception {
        //************************
        //          Given
        //************************
        User user = new User("Mina",null);
        userRepository.save(user);
        //************************
        //          WHEN
        //************************

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/service/create/1")
                .content("{\"id\": 1,\"url\":\"http://localhost:8080/service/get/1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //************************
        //          THEN
        //************************
        assertThat(result.getResponse().getContentAsString()).contains("http://localhost:8080/service/get/1");
    }

    @Test
    public void updateService_test() throws Exception {
        //************************
        //          Given
        //************************
        serviceRepository.save(new ServiceIdentity("http://localhost:8080/service/get/1"));
        //************************
        //          WHEN
        //************************

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/service/update")
                .content("{\"id\": 1,\"url\":\"http://localhost:8080/service/get/1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //************************
        //          THEN
        //************************
        assertThat(result.getResponse().getContentAsString()).contains("http://localhost:8080/service/get/1");
    }

    @Test
    public void deleteService_test() throws Exception {
        //************************
        //          Given
        //************************
        serviceRepository.save(new ServiceIdentity("http://localhost:8080/service/get/1"));
        //************************
        //          WHEN
        //************************

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/service/delete")
                .content("{\"id\": 1,\"url\":\"http://localhost:8080/service/get/1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //************************
        //          THEN
        //************************
        assertThat(result.getResponse().getContentAsString()).contains("http://localhost:8080/service/get/1");
    }
}
