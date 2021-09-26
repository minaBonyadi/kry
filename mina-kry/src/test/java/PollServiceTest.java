import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.kry.MainPoller;
import se.kry.enumeration.Status;
import se.kry.exception.PollServiceException;
import se.kry.model.ServiceIdentity;
import se.kry.model.User;
import se.kry.repository.ServiceRepository;
import se.kry.repository.UserRepository;
import se.kry.service.PollService;
import se.kry.service.queue.BlockingQueueImpl;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ContextConfiguration
@AutoConfigureWireMock
@SpringBootTest(classes =  MainPoller.class)
public class PollServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BlockingQueueImpl blockingQueue;

    public ServiceIdentity serviceIdentity;
    public PollService pollService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        pollService = new PollService(userRepository, serviceRepository, blockingQueue);
    }

    @Test
    public void pollServiceHealthCheckForWrongURl() throws Exception {
        //************************
        //          Given
        //************************
        User user1 = new User(1001L, "Mina", Collections.singleton(new ServiceIdentity("http://localhost:8080/findServiceById/105")));
        userRepository.save(user1);
        User user2 = new User(1002L, "Kry", Collections.singleton(new ServiceIdentity("http://localhost:8080/findServiceById/103")));
        userRepository.save(user2);

        //************************
        //          WHEN
        //************************
        // Stubbing WireMock
        stubFor(get(urlEqualTo("/findServiceById/103")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain").withStatus(400)));

        stubFor(get(urlEqualTo("/findServiceById/105")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain").withStatus(400)));

        mockMvc.perform(MockMvcRequestBuilders.post("/service/poll/health-check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread thread = new Thread(() -> pollService.callService());
        thread.start();

        //************************
        //          THEN
        //************************
        await("wait_for_response_1").atMost(50, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(thread.getState()).isEqualTo(Thread.State.WAITING));
    }

    @Test
    public void pollServiceHealthCheckForRightURl() throws Exception {
        //************************
        //          Given
        //************************
        serviceIdentity = new ServiceIdentity("http://localhost:8080/service/get/102");
        User user = new User(1002, "Mina", Collections.singleton(serviceIdentity));
        userRepository.save(user);

        //************************
        //          WHEN
        //************************
        // Stubbing WireMock
        stubFor(get(urlEqualTo("/service/get/102")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain").withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.post("/service/poll/health-check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread thread = new Thread(() -> pollService.callService());
        thread.start();

        //************************
        //          THEN
        //************************
        await("wait_for_response_2").atMost(50, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertDoesNotThrow(()-> new PollServiceException(Status.FAIL + "")));
    }

    @Test
    public void pollServiceSendShutdownSignal() throws Exception {
        //************************
        //          Given
        //************************
        User user = new User(1001L, "Mina", Collections.singleton(new ServiceIdentity("http://localhost:8080/findServiceById/105")));
        userRepository.save(user);
        //************************
        //          WHEN
        //************************
        // Stubbing WireMock
        stubFor(get(urlEqualTo("/findServiceById/103")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain").withStatus(404)));

        mockMvc.perform(MockMvcRequestBuilders.post("/service/poll/health-check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread thread1 = new Thread(() -> pollService.callService());
        thread1.start();

        //************************
        //          THEN
        //************************

        Thread thread2 = new Thread(() -> pollService.callShutdownSignal());
        thread2.start();

        await("wait_for_response_1").atMost(50, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(thread2.getState()).isEqualTo(Thread.State.TERMINATED));
    }
}
