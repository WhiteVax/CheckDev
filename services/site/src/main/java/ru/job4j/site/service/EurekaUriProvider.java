package ru.job4j.site.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EurekaUriProvider {

    private final DiscoveryClient discoveryClient;

    public String getUri(String serviceId) {
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance instance = list.get(0);
        return instance.getUri().toString();
    }
}
