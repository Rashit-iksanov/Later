package ru.yandex.practicum.item;

import ru.yandex.practicum.common.LaterApplicationException;

public class ItemRetrieverException extends LaterApplicationException {
    public ItemRetrieverException(String message) {
        super(message);
    }

    public ItemRetrieverException(String message, Throwable cause) {
        super(message, cause);
    }
}
