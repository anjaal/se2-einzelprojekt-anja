package at.aau.serg.services

import at.aau.serg.models.GameResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameResultServiceTests {

    private lateinit var service: GameResultService

    @BeforeEach
    fun setup() {
        service = GameResultService()
    }

    //Testen, ob einen neue Service Instanz eine leere Liste zurückgibt
    @Test
    fun test_getGameResults_emptyList() {
        val result = service.getGameResults()

        assertEquals(emptyList<GameResult>(), result)
    }

    //Testen, ob ein neu hinzugefügtes GameResult korrekt mit ein element gespeichert wird
    @Test
    fun test_addGameResult_getGameResults_containsSingleElement() {
        val gameResult = GameResult(1, "player1", 17, 15.3)

        service.addGameResult(gameResult)
        val res = service.getGameResults()

        assertEquals(1, res.size)
        assertEquals(gameResult, res[0])
    }

    //Testen, ob ein GameResult über seine id gefunden werden kann
    @Test
    fun test_getGameResultById_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(1)

        assertEquals(gameResult, res)
    }

    //Testen, ob null zurückgegeben wird, wenn eine id nicht existiert
    @Test
    fun test_getGameResultById_nonexistentId_returnsNull() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(22)

        assertNull(res)
    }

    //Testen, ob id's automatisch vergeben werden und ob sie korrekt hochgezählt werden
    @Test
    fun test_addGameResult_multipleEntries_correctId() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)

        val res = service.getGameResults()

        assertEquals(2, res.size)

        assertEquals(gameResult1, res[0])
        assertEquals(1, res[0].id)

        assertEquals(gameResult2, res[1])
        assertEquals(2, res[1].id)
    }

    //Aufgabe 2.2.3: Zusätzliche Tests für deleteGameResult Methode, für einen 100%ige Coverage
    //Ein mal, mit wenn die ids übereinstimmen und einmal wenn sie nicht übereinstimmen
    @Test
    fun test_deleteGameResult_ifIdExists_thenReturnTrue() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        service.addGameResult(gameResult1)

        //Die automatisch vergebene id (sie wird im Kontruktor auf 0 gesetzt, da der Service sie später automatisch überschreibt)
        val id = gameResult1.id

        val deleted = service.deleteGameResult(id)
        assertEquals(true, deleted)
        assertNull(service.getGameResult(id))
    }

    @Test
    fun test_deleteGameResult_ifIdDoesntExist_thenReturnFalse() {
        val deleted = service.deleteGameResult(999)
        assertEquals(false, deleted)
    }

}