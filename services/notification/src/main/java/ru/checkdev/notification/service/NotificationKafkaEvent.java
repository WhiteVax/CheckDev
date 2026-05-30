package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationKafkaEvent {
    private NotificationKafkaEventType type;
    private String payload;
}
