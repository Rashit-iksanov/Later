package ru.yandex.practicum.note;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.item.model.Item;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemNoteMapper {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter
            .ofPattern("yyyy.MM.dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    public static ItemNoteDto mapToItemNoteDto(ItemNote itemNote) {
        return new ItemNoteDto(
                itemNote.getId(),
                itemNote.getItem().getId(),
                itemNote.getText(),
                itemNote.getDateOfNote() != null ? dtFormatter.format(itemNote.getDateOfNote()) : null,
                itemNote.getItem().getUrl()
        );
    }

    public static List<ItemNoteDto> mapToItemNoteDto(Iterable<ItemNote> itemNotes) {
        List<ItemNoteDto> dtos = new ArrayList<>();
        for (ItemNote itemNote : itemNotes) {
            dtos.add(mapToItemNoteDto(itemNote));
        }
        return dtos;
    }

    public static ItemNote mapToItemNote(ItemNoteDto itemNoteDto, Item item) {
        ItemNote itemNote = new ItemNote();
        itemNote.setItem(item);
        itemNote.setText(itemNoteDto.getText());
        return itemNote;
    }
}