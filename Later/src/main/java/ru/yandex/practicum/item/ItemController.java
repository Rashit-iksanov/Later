package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.item.dto.*;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(
            @RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "unread") String state,
            @RequestParam(name = "contentType", defaultValue = "all") String contentType,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "tags", required = false) List<String> tags
    ) {
        return itemService.getItems(GetItemRequest.of(userId, state, contentType, sort, limit, tags));
    }

    @GetMapping(params = "lastName")
    public List<ItemDto> get(@RequestParam(name = "lastName") String lastName) {
        return itemService.getUserItems(lastName);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Long userId, @RequestBody AddItemRequest request) {
        return itemService.addNewItem(userId, request);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId, @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping
    public ItemDto modifyItem(@RequestHeader("X-Later-User-Id") long userId, @RequestBody ModifyItemRequest request) {
        return itemService.changeItem(userId, request);
    }
}
