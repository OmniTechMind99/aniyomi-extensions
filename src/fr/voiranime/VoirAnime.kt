package eu.kanade.tachiyomi.animeextension.fr.voiranime

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.ParsedAnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class VoirAnime : ParsedAnimeHttpSource() {

    override val name = "VoirAnime"
    override val baseUrl = "https://voiranime.com"
    override val lang = "fr"
    override val supportsLatest = true

    // Recherche / Populaire
    override fun popularAnimeSelector() = "div.bsx"
    override fun popularAnimeRequest(page: Int): Request = GET("$baseUrl/anime/page/$page/")
    override fun popularAnimeFromElement(element: Element): SAnime {
        val anime = SAnime.create()
        anime.setUrlWithoutDomain(element.select("a").attr("href"))
        anime.title = element.select("div.tt").text()
        anime.thumbnail_url = element.select("img").attr("src")
        return anime
    }
    override fun popularAnimeNextPageSelector() = "a.next.page-numbers"

    // Détails de l'anime
    override fun animeDetailsParse(document: Document): SAnime {
        val anime = SAnime.create()
        anime.title = document.select("h1.entry-title").text()
        anime.description = document.select("div.entry-content p").text()
        anime.genre = document.select("div.genxed a").joinToString { it.text() }
        return anime
    }

    // Épisodes
    override fun episodeListSelector() = "div.eplister li"
    override fun episodeFromElement(element: Element): SEpisode {
        val episode = SEpisode.create()
        episode.setUrlWithoutDomain(element.select("a").attr("href"))
        episode.name = element.select("div.epl-title").text()
        episode.episode_number = element.select("div.epl-num").text().toFloatOrNull() ?: 1f
        return episode
    }

    // Vidéos (Lecteurs)
    override fun videoListSelector() = "select#select-mirror option"
    override fun videoFromElement(element: Element): Video {
        val url = element.attr("value")
        val quality = element.text()
        return Video(url, quality, url)
    }

    override fun videoUrlParse(document: Document) = throw Exception("Not used")

    // Recherche
    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList): Request {
        return GET("$baseUrl/page/$page/?s=$query")
    }
    override fun searchAnimeSelector() = popularAnimeSelector()
    override fun searchAnimeFromElement(element: Element) = popularAnimeFromElement(element)
    override fun searchAnimeNextPageSelector() = popularAnimeNextPageSelector()

    // Dernières sorties
    override fun latestUpdatesRequest(page: Int): Request = GET("$baseUrl/latest/page/$page/")
    override fun latestUpdatesSelector() = popularAnimeSelector()
    override fun latestUpdatesFromElement(element: Element) = popularAnimeFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularAnimeNextPageSelector()
}
