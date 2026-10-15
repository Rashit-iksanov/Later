package ru.yandex.practicum.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional // Автоматический откат транзакции после каждого теста
class UserServiceImplTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository; // Внедряем репозиторий для проверок вместо сырого EntityManager

    @Test
    void saveUser() {
        // given
        UserDto userDto = makeUserDto("some@email.com", "Пётр", "Иванов");

        // when
        UserDto savedUserDto = service.saveUser(userDto);

        // then
        // Проверяем, что сервис вернул корректный DTO
        assertThat(savedUserDto.getId(), notNullValue());
        assertThat(savedUserDto.getEmail(), equalTo(userDto.getEmail()));

        // Проверяем, что данные реально сохранились в БД через репозиторий
        User savedEntity = userRepository.findById(savedUserDto.getId()).orElseThrow();

        assertAll("User entity should match DTO",
                () -> assertThat(savedEntity.getId(), notNullValue()),
                () -> assertThat(savedEntity.getFirstName(), equalTo(userDto.getFirstName())),
                () -> assertThat(savedEntity.getLastName(), equalTo(userDto.getLastName())),
                () -> assertThat(savedEntity.getEmail(), equalTo(userDto.getEmail())),
                () -> assertThat(savedEntity.getState(), equalTo(userDto.getState())),
                () -> assertThat(savedEntity.getRegistrationDate(), notNullValue())
        );
    }

    @Test
    void getAllUsers() {
        // given
        List<UserDto> sourceUsers = List.of(
                makeUserDto("ivan@email.com", "Ivan", "Ivanov"),
                makeUserDto("petr@email.com", "Petr", "Petrov"),
                makeUserDto("vasilii@email.com", "Vasilii", "Vasiliev")
        );

        // Сохраняем пользователей напрямую через сервис (или репозиторий)
        for (UserDto user : sourceUsers) {
            service.saveUser(user);
        }

        // when
        List<UserDto> targetUsers = service.getAllUsers();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));

        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("firstName", equalTo(sourceUser.getFirstName())),
                    hasProperty("lastName", equalTo(sourceUser.getLastName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setState(UserState.ACTIVE);
        return dto;
    }
}
