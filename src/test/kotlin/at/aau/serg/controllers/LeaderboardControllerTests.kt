package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    //Testen, ob das Leaderboard korrekt nach der Score (DESC) sortiert wird
    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        //Aufgabe 2.2.2: Wenn rank = null ist, dann wird der gesamte Leaderboard zurückgegeben
        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    //Testen, ob der Leaderboard bei gleichem Score nach kürzerer Spielzeit (ASC) sortiert wird
    @Test
    fun test_getLeaderboard_sameScore_CorrectSortingByTimeInSeconds() {
        val slowest = GameResult(1, "slowestPlayer", 20, 20.0)
        val fastest = GameResult(2, "fastestPlayer", 20, 10.0)
        val medium = GameResult(3, "mediumPlayer", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(medium, slowest, fastest))

        //Aufgabe 2.2.2: Wenn rank = null ist, dann wird der gesamte Leaderboard zurückgegeben
        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)

        //Sortiert nach kürzeste Spielzeit
        //Erwartete Reihenfolge: 10 (fastest) -> 15 (medium) -> 20 (slowest)
        assertEquals(fastest, res[0])
        assertEquals(medium, res[1])
        assertEquals(slowest, res[2])
    }

    //Aufgabe 2.2.2: Neue Tests
    //Testen, ob bei rank = 4, die oberen und unteren 3 spieler zurückgegeben werden, insgesamt 7
    @Test
    fun test_getLeaderboard_ifRank4_returnsCorrectRangeOfPlayers()
    {
        val results = listOf(
            GameResult(1, "first", 20, 20.0),
            GameResult(2, "second", 15, 10.0),
            GameResult(3, "third", 10, 15.0),
            GameResult(4, "fourth", 10, 15.0),
            GameResult(5, "fifth", 10, 15.0),
            GameResult(6, "sixth", 10, 15.0),
            GameResult(7, "seventh", 10, 15.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val result = controller.getLeaderboard(4)
        assertEquals(7, result.size)
        assertEquals("first", result[0].playerName)
        assertEquals("seventh", result[6].playerName)
    }

    //Testen, ob bei rank = 1, nur die ersten 4 Spieler zurückgegeben werden
    @Test
    fun test_getLeaderboard_ifRank1_returnsFirstFourPlayers()
    {
        val results = listOf(
            GameResult(1, "first", 20, 20.0),
            GameResult(2, "second", 15, 10.0),
            GameResult(3, "third", 10, 15.0),
            GameResult(4, "fourth", 10, 15.0),
            GameResult(5, "fifth", 10, 15.0),
            GameResult(6, "sixth", 10, 15.0),
            GameResult(7, "seventh", 10, 15.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val result = controller.getLeaderboard(1)

        assertEquals(4, result.size)
        assertEquals("first", result[0].playerName)
        assertEquals("fourth", result[3].playerName)
    }

    //Testen, ob beim letzten rank, die letzten 4 spieler zurückgegeben werden
    @Test
    fun test_getLeaderboard_ifRankLast_returnsLastFourPlayers()
    {
        val results = listOf(
            GameResult(1, "first", 20, 20.0),
            GameResult(2, "second", 15, 10.0),
            GameResult(3, "third", 10, 15.0),
            GameResult(4, "fourth", 10, 15.0),
            GameResult(5, "fifth", 10, 15.0),
            GameResult(6, "sixth", 10, 15.0),
            GameResult(7, "seventh", 10, 15.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val result = controller.getLeaderboard(7)

        assertEquals(4, result.size)
        assertEquals("fourth", result[0].playerName)
        assertEquals("seventh", result[3].playerName)
    }

    //Aufgabe 2.2.2: Fehlermeldung-Tests
    //Testen, ob ein Fehler geworfen wird, wenn rank < 1
    @Test
    fun test_getLeaderboard_ifRankIsSmallerThanRange_throwsABadRequestError()
    {
        val results = listOf(GameResult(1, "first", 20, 20.0))

        whenever(mockedService.getGameResults()).thenReturn(results)

        //Erwarten, dass eine BAD_REQUEST-Exception geworfen wird
        val exception = kotlin.test.assertFailsWith<ResponseStatusException> {controller.getLeaderboard(0)}

        //und prüfen, ob der Statuscode dann ein 400 - BAD_REQUEST ist
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    //Testen, ob ein Fehler geworfen wird, wenn rank > size
    @Test
    fun test_getLeaderboard_ifRankIsLargerThanRange_throwsABadRequestError()
    {
        val results = listOf(GameResult(1, "first", 20, 20.0))

        whenever(mockedService.getGameResults()).thenReturn(results)

        //Erwarten, dass eine BAD_REQUEST-Exception geworfen wird
        val exception = kotlin.test.assertFailsWith<ResponseStatusException> {controller.getLeaderboard(5)}

        //und prüfen, ob der Statuscode dann ein 400 - BAD_REQUEST ist
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }
}