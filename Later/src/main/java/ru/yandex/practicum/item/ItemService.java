package ru.yandex.practicum.item;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.item.dto.AddItemRequest;
import ru.yandex.practicum.item.dto.GetItemRequest;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ModifyItemRequest;
import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDto> getItems(long userId);
    List<ItemDto> getItems(GetItemRequest req);
    List<ItemDto> getUserItems(String lastName);

    @Transactional
    ItemDto addNewItem(Long userId, AddItemRequest request);

    @Transactional
    void deleteItem(long userId, long itemId);

    @Transactional
    ItemDto changeItem(long userId, ModifyItemRequest request);
}