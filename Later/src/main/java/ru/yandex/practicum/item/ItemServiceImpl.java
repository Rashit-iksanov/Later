package ru.yandex.practicum.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.common.InsufficientPermissionException;
import ru.yandex.practicum.common.NotFoundException;
import ru.yandex.practicum.item.dto.*;
import ru.yandex.practicum.item.model.Item;
import ru.yandex.practicum.item.model.QItem;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final UrlMetaDataRetriever urlMetaDataRetriever;

    @Override
    public List<ItemDto> getItems(long userId) {
        return ItemMapper.mapToItemDto(repository.findByUserId(userId));
    }

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, AddItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InsufficientPermissionException("You do not have permission to perform this operation"));

        UrlMetaDataRetriever.UrlMetadata result = urlMetaDataRetriever.retrieve(request.getUrl());
        Optional<Item> maybeExistingItem = repository.findByUserAndResolvedUrl(user, result.getResolvedUrl());

        Item item;
        if (maybeExistingItem.isEmpty()) {
            item = repository.save(ItemMapper.mapToItem(result, user, request.getTags()));
        } else {
            item = maybeExistingItem.get();
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                item.getTags().addAll(request.getTags());
                repository.save(item);
            }
        }
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        repository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public List<ItemDto> getItems(GetItemRequest req) {
        QItem item = QItem.item;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(item.user.id.eq(req.getUserId()));

        if (!req.getState().equals(GetItemRequest.State.ALL)) {
            conditions.add(makeStateCondition(req.getState()));
        }
        if (!req.getContentType().equals(GetItemRequest.ContentType.ALL)) {
            conditions.add(makeContentTypeCondition(req.getContentType()));
        }
        if (req.hasTags()) {
            conditions.add(item.tags.any().in(req.getTags()));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElse(item.isNotNull());

        Sort sort = makeOrderByClause(req.getSort());
        PageRequest pageRequest = PageRequest.of(0, req.getLimit(), sort);

        return ItemMapper.mapToItemDto(repository.findAll(finalCondition, pageRequest));
    }

    @Override
    public List<ItemDto> getUserItems(String lastName) {
        return ItemMapper.mapToItemDto(repository.findItemsByLastNamePrefix(lastName));
    }

    @Override
    @Transactional
    public ItemDto changeItem(long userId, ModifyItemRequest request) {
        Optional<Item> maybeItem = getAndCheckPermissions(userId, request.getItemId());
        if (maybeItem.isPresent()) {
            Item item = maybeItem.get();
            item.setUnread(!request.isRead());

            if (request.isReplaceTags()) {
                item.getTags().clear();
            }
            if (request.hasTags()) {
                item.getTags().addAll(request.getTags());
            }
            return ItemMapper.mapToItemDto(repository.save(item));
        } else {
            throw new NotFoundException("The item with id " + request.getItemId() + " was not found");
        }
    }

    private Optional<Item> getAndCheckPermissions(long userId, long itemId) {
        Optional<Item> maybeItem = repository.findById(itemId);
        maybeItem.ifPresent(item -> {
            if (!item.getUser().getId().equals(userId)) {
                throw new InsufficientPermissionException("You do not have permission to perform this operation");
            }
        });
        return maybeItem;
    }

    private BooleanExpression makeStateCondition(GetItemRequest.State state) {
        return state.equals(GetItemRequest.State.READ) ? QItem.item.unread.isFalse() : QItem.item.unread.isTrue();
    }

    private BooleanExpression makeContentTypeCondition(GetItemRequest.ContentType contentType) {
        if (contentType.equals(GetItemRequest.ContentType.ARTICLE)) {
            return QItem.item.mimeType.eq("text");
        } else if (contentType.equals(GetItemRequest.ContentType.IMAGE)) {
            return QItem.item.mimeType.eq("image");
        } else {
            return QItem.item.mimeType.eq("video");
        }
    }

    private Sort makeOrderByClause(GetItemRequest.Sort sort) {
        return switch (sort) {
            case TITLE -> Sort.by("title").ascending();
            case SITE -> Sort.by("resolvedUrl").ascending();
            case OLDEST -> Sort.by("dateResolved").ascending();
            default -> Sort.by("dateResolved").descending();
        };
    }
}
