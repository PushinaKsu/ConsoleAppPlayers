package positive;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServicePositiveTests {

    private PlayerService service;
    private int playerId;

    @BeforeEach
    void setUp() {
        service = new PlayerServiceImpl(); // Создаем новый экземпляр сервиса перед каждым тестом
        playerId = service.createPlayer("Player1"); // Создаем игрока
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));  // Удаляем JSON-файл после каждого теста, чтобы тесты были изолированы
    }

    @Test
    @DisplayName("Добавить игрока и проверить его наличие в списке")
    void AddPlayerAndCheck() {
        // Получаем информацию об игроке по его ID
        Player player = service.getPlayerById(playerId);

        // Проверяем корректность данных созданного игрока
        assertNotNull(player, "Созданный игрок не должен быть null");
        assertEquals(playerId, player.getId(), "ID созданного игрока должен совпадать с полученным ID");
        assertEquals("Player1", player.getNick(), "Имя игрока должно совпадать с заданным");
        assertTrue(player.isOnline(), "Созданный игрок должен быть в статусе 'online'");
        assertEquals(0, player.getPoints(), "Очки нового игрока должны быть равны 0");

        // Получаем полный список игроков
        Collection<Player> players = service.getPlayers();

        // Проверяем, что в списке есть ровно один игрок и это наш созданный игрок
        assertEquals(1, players.size(), "В списке должен быть только один игрок");
        assertTrue(players.stream().anyMatch(p -> p.getId() == playerId),
                "Игрок с заданным ID должен присутствовать в списке");
    }

    @Test
    @DisplayName("Добавить игрока, удалить игрока, проверить его отсутствие в списке")
    void AddAndDeletePlayer() {
        service.deletePlayer(playerId); // Удаляем игрока

        Collection<Player> players = service.getPlayers(); // Получаем список игроков
        assertEquals(0, players.size(), "Список должен быть пустым после удаления игрока");
    }

    @Test
    @DisplayName("Добавить игрока при отсутствии JSON-файла")
    void AddPlayerWithoutJson() {
        Collection<Player> players = service.getPlayers(); // Получаем список игроков
        assertEquals(1, players.size(), "Должен быть добавлен один игрок");
    }

    @Test
    @DisplayName("Добавить игрока при наличии JSON-файла")
    void AddPlayerWithExistingJson() {
        service.createPlayer("Player2"); // Добавляем второго игрока

        Collection<Player> players = service.getPlayers(); // Получаем список игроков
        assertEquals(2, players.size(), "Должны быть добавлены два игрока");
    }

    @DisplayName("Начислить баллы существующему игроку")
    @ParameterizedTest
    @ValueSource(ints = {1,50,99,100})
    public void AddPointsToUser(int scoreToAdd) {
        service.addPoints(playerId,scoreToAdd);
        int points = service.getPlayerById(playerId).getPoints();
        assertEquals(scoreToAdd,points);
    }

    @Test
    @DisplayName("Добавить очки поверх существующих")
    void AddPointsOnTop() {
        service.addPoints(playerId, 50); // Начисляем первые очки
        int newScore = service.addPoints(playerId, 50); // Начисляем еще 50 очков

        assertEquals(100, newScore, "Общий счет должен быть равен 100");
    }

    @Test
    @DisplayName("Добавить игрока и получить игрока по ID")
    void GetPlayerById() {
        Player player = service.getPlayerById(playerId); // Получаем игрока по ID

        assertNotNull(player, "Игрок должен быть найден");
        assertEquals("Player1", player.getNick(), "Никнейм должен совпадать");
    }

    @Test
    @DisplayName("Проверка сохранения данных в файл")
    void DataIsSavedToFile() throws IOException {
        assertTrue(Files.exists(Path.of("./data.json")), "JSON-файл должен быть создан");
    }

    @Test
    @DisplayName("Проверка корректности загрузки JSON-файла")
    void DataIsLoadedCorrectlyJson() {
        Collection<Player> players = service.getPlayers(); // Загружаем игроков из файла

        assertEquals(1, players.size(), "Должен быть загружен один игрок");
        assertEquals("Player1", players.iterator().next().getNick(), "Данные игрока должны совпадать");
    }

    @Test
    @DisplayName("Проверить уникальность ID игроков")
    void UniqueId() {
        service.createPlayer("Player2");
        service.createPlayer("Player3");
        service.deletePlayer(2); // Удаляем игрока с ID 2
        int newPlayerId = service.createPlayer("Player4"); // Создаем нового игрока

        assertEquals(4, newPlayerId, "ID нового игрока должен быть уникальным");
    }

    @Test
    @DisplayName("Запросить список игроков при отсутствии JSON-файла")
    void GetPlayersWithoutJsonFile() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
        PlayerServiceImpl service = new PlayerServiceImpl();
        Collection<Player> players = service.getPlayers(); // Получаем список игроков

        assertTrue(players.isEmpty(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Проверить создание игрока с ником длиной 15 символов")
    void CreatePlayerWith15Chars() {
        String nickname = generateNickname(); // Никнейм длиной 15 символов

        // Act: создаем игрока и получаем его из сервиса
        int playerId = service.createPlayer(nickname);
        Player player = service.getPlayerById(playerId);

        // Assert: проверяем, что игрок создан с правильным ником
        assertEquals(nickname, player.getNick(), "Никнейм игрока должен быть длиной 15 символов");
    }

    private String generateNickname() {
        return "x".repeat(15); // Генерирует строку из 'x' указанной длины
    }
}

