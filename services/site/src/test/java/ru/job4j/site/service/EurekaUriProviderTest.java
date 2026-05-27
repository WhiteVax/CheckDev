package ru.job4j.site.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class EurekaUriProviderTest {

    @Mock
    private DiscoveryClient discoveryClient;

    private EurekaUriProvider uriProvider;

    @BeforeEach
    void setUp() {
        discoveryClient = Mockito.mock(DiscoveryClient.class);
        uriProvider = new EurekaUriProvider(discoveryClient);
    }

    @Test
    void whenGetUri() throws URISyntaxException {
        ServiceInstance serviceInstance = Mockito.mock(ServiceInstance.class);
        List<ServiceInstance> serviceInstances = Collections.singletonList(serviceInstance);
        Mockito.when(discoveryClient.getInstances("serviceId")).thenReturn(serviceInstances);
        Mockito.when(serviceInstance.getUri()).thenReturn(new java.net.URI("https://example.com"));
        String uri = uriProvider.getUri("serviceId");
        assertThat("https://example.com", is(uri));
    }
}
