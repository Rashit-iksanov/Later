package ru.yandex.practicum.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.model.Item;
import ru.yandex.practicum.user.User;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ItemMapper {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter
            .ofPattern("yyyy.MM.dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    public static Item mapToItem(UrlMetaDataRetriever.UrlMetadata result, User user, Set<String> tags) {
        Item item = new Item();
        item.setUser(user);
        item.setUrl(result.getNormalUrl());
        item.setResolvedUrl(result.getResolvedUrl());
        item.setMimeType(result.getMimeType());
        item.setTitle(result.getTitle());
        item.setHasImage(result.isHasImage());
        item.setHasVideo(result.isHasVideo());
        item.setDateResolved(result.getDateResolved());
        item.setTags(tags != null ? tags : new HashSet<>());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .normalUrl(item.getUrl())
                .resolvedUrl(item.getResolvedUrl())
                .hasImage(item.isHasImage())
                .hasVideo(item.isHasVideo())
                .mimeType(item.getMimeType())
                .unread(item.isUnread())
                .dateResolved(item.getDateResolved() != null ? dtFormatter.format(item.getDateResolved()) : null)
                .tags(new HashSet<>(item.getTags()))
                .build();
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }
}