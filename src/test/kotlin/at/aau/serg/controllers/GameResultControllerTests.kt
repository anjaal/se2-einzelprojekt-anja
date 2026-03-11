package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup()
    {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)

        //Nur für POST notwendig
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    //Testen, ob ein GameResult korrekt über seine ID zurückgegeben wird
    @Test
    fun test_getGameResultById()
    {
        val gameResult = GameResult(1, "player1", 10, 12.5)
        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val result = controller.getGameResult(1)
        assertEquals(gameResult, result)
        verify(mockedService).getGameResult(1)
    }

    //Testen, ob alle GameResults vom Service korrekt zurückgegeben werden
    @Test
    fun test_getAllGameResults()
    {
        val gameResult = listOf(GameResult(1, "player1", 10, 12.5),
            GameResult(2, "player2", 10, 12.5))

        whenever(mockedService.getGameResults()).thenReturn(gameResult)

        val result = controller.getAllGameResults()
        assertEquals(2, result.size)
        verify(mockedService).getGameResults()
    }

    //Testen, ob ein GameResult korrekt entgegengenommen wird und ob es an den Service korrekt weitergeleitet wird
    //POST wird anders als die anderen getestet -> mit MockMvc, da bei POST-Anfragen ein JSON-RequestBody verarbeitet wird
    @Test
    fun test_addGameResult()
    {
        val gameResult = GameResult(1, "player1", 10, 12.5)
        val json = objectMapper.writeValueAsString(gameResult)

        mockMvc.perform(
            post("/game-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
        verify(mockedService).addGameResult(gameResult)
    }

    //Testen, ob ein GameResult anhand seiner ID korrekt gelöscht werden kann
    @Test
    fun test_deleteGameResult()
    {
        controller.deleteGameResult(1)
        verify(mockedService).deleteGameResult(1)
    }

    
}