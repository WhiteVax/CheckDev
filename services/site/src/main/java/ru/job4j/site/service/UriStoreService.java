package ru.job4j.site.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UriStoreService {

    private final EurekaUriProvider uriProvider;


}
