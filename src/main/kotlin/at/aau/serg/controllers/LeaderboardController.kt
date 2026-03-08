package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import io.ktor.client.statement.HttpResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(private val gameResultService: GameResultService)
{
    //=> Liefert den Leaderboard sortiert zurück
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult>
    {
        //Aufgabenstellung 2.2.1: Score absteigend sortieren und bei gleichem Score, die kürzere Spielzeit zuerst
        val sorted = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        //Aufgabenstellung 2.2.2:
        //Wenn rank nicht gegeben -> gesamte Leaderboard zurückgeben
        if(rank == null)
        {
            return sorted
        }

        //Wenn rank ungültig -> HTTP 400 (Bad Request)
        if(rank < 1 || rank > sorted.size)
        {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Rank is invalid!")
        }

        //Wenn rank gegeben -> Spieler + 3 obere und 3 untere Spieler zurückgeben

        //rank fangt bei 1 und der index bei 0 an, deswegen:
        val index = rank - 1

        //Obere und untere 3 Spieler bestimmen
        val fromRank = maxOf(0, index - 3)
        val toRank = minOf(sorted.size - 1, index + 3)

        //Rechnen +1 dazu, weil subList das Ende sonst nicht mitnimmt
        return sorted.subList(fromRank, toRank + 1)
    }
}