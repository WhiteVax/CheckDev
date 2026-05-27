package ru.job4j.site.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.util.RestAuthCall;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "notification";
    private static final String DIRECT = "/messages/";

    public void delete(String token, int messageId) {
        new RestAuthCall(
                String.format("%s%sdelete/%d", uriProvider.getUri(SERVICE_ID), DIRECT, messageId))
                .delete(token, "");
    }

    public void setRead(String token, int messageId) {
        new RestAuthCall(
                String.format("%s%smessages/setRead/%d", uriProvider.getUri(SERVICE_ID), DIRECT, messageId))
                .put(token, "");
    }

    public void setReadAll(String token, int userId) {
        new RestAuthCall(
                String.format("%s%ssetReadAll/%d", uriProvider.getUri(SERVICE_ID), DIRECT, userId))
                .put(token, "");
    }
}
