package negative;

import org.junit.jupiter.api.*;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceNegativeTests {

    private PlayerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PlayerServiceImpl(); // Создаем новый экземпляр сервиса перед каждым тестом
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));  // Удаляем JSON-файл после каждого теста, чтобы тесты были изолированы
    }

    @Test
    @DisplayName("Удаление несуществующего игрока вызывает ошибку")
    void DeleteNonExistentPlayer() {
        assertThrows(NoSuchElementException.class,
                () -> service.deletePlayer(10), "Должно выбрасываться исключение при удалении несуществующего игрока");
    }

    @Test
    @DisplayName("Создание дубликата (имя занято)")
    void CreateDuplicate() {
        service.createPlayer("Player1"); // Создаем игрока
        assertThrows(IllegalArgumentException.class,
                () -> service.createPlayer("Player1"),
                "Должно выбрасываться исключение, если имя уже занято");
    }

    @Test
    @DisplayName("Получить игрока по несуществующему ID вызывает ошибку")
    void GetNonExistentPlayerById() {
        assertThrows(NoSuchElementException.class,
                () -> service.getPlayerById(10),
                "Должно выбрасываться исключение, если игрок с таким ID не существует");
    }

    @Test // Нет реализации для этой проверки
    @DisplayName("Создание игрока с пустым ником вызывает ошибку")
    void CreatePlayerWithEmptyNickname() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPlayer(""),
                "Должно выбрасываться исключение при попытке создать игрока с пустым ником");
    }

    @Test // Нет реализации для этой проверки
    @DisplayName("Начисление отрицательного количества очков вызывает ошибку")
    void AddNegativePoints() {
        int playerId = service.createPlayer("Player1"); // Создаем игрока
        assertThrows(IllegalArgumentException.class,
                () -> service.addPoints(playerId, -50),
                "Должно выбрасываться исключение при попытке начислить отрицательные очки");
    }

    @Test
    @DisplayName("Начисление очков несуществующему игроку вызывает ошибку")
    void AddPointsToNonExistentPlayer() {
        assertThrows(NoSuchElementException.class,
                () -> service.addPoints(10, 50),
                "Должно выбрасываться исключение, если игрока с указанным ID не существует");
    }

    @Test // как будто бы эта проверка не для PlayerServiceImpl и возможно не имеет смысла
    @DisplayName("Начисление очков без указания ID вызывает ошибку")
    void AddPointsWithoutIdError() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addPoints(0, 50),
                "Должно выбрасываться исключение, если не указан ID игрока");
    }

    @Test
    @DisplayName("Загрузка данных из другого JSON-файла")
    void LoadDataFromAnotherJson() throws IOException {
        Files.writeString(Path.of("./data.json"), "[]"); // Заменяем JSON-файл пустым массивом
        assertTrue(service.getPlayers().isEmpty(), "При загрузке пустого JSON-файла список игроков должен быть пустым");
    }

    @Test // Вообще наверное этот тест надо бы удалить так как он не имеет смысла
    @DisplayName("Начисление дробного числа баллов вызывает ошибку")
    void AddFractionalPointsError() {
        int playerId = service.createPlayer("Player1"); // Создаем игрока
        assertThrows(IllegalArgumentException.class,
                () -> service.addPoints(playerId, (int) 1.5), // Преобразуем дробное значение в int для проверки
                "Должно выбрасываться исключение при попытке начислить дробные очки");
    }

    @Test
    @DisplayName("Проверка, что дубликатов в JSON-файле нет")
    void NoDuplicatesInJson() throws IOException {
        service.createPlayer("Player1"); // Создаем игрока
        service.createPlayer("Player2"); // Создаем второго игрока
        Files.writeString(Path.of("./data.json"), "[{\"id\":1,\"nick\":\"Player1\"},{\"id\":1,\"nick\":\"Player1\"}]");
        assertEquals(2, service.getPlayers().size(),
                "В тестовой системе не должно быть дублирующихся записей");
    }

    @Test // Нет реализации для этой проверки
    @DisplayName("Проверить ошибку при создании игрока с ником длиной 16 символов")
    void CreatePlayerWith16Chars() {
        // Arrange: создаем сервис и ник длиной 16 символов
        PlayerService playerService = new PlayerServiceImpl();
        String nickname = generateNickname(16); // Никнейм длиной 16 символов

        // Act & Assert: проверяем, что создается исключение с ожидаемым сообщением
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> playerService.createPlayer(nickname)
        );
        String expectedErrorMessage = "Nickname can not be larger than 15 symbols!";
        assertEquals(expectedErrorMessage, exception.getMessage(), "Сообщение об ошибке не совпадает с ожидаемым");
    }

    private String generateNickname(int length) {
        return "x".repeat(length); // Генерирует строку из 'x' указанной длины
    }
}
