package dev.weekend.slashcommand.infrastructure.cache

import com.google.common.cache.CacheBuilder
import org.eu.zajc.akiwrapper.Akiwrapper
import org.eu.zajc.akiwrapper.Akiwrapper.Language.KOREAN
import org.eu.zajc.akiwrapper.Akiwrapper.Theme.CHARACTER
import org.eu.zajc.akiwrapper.AkiwrapperBuilder
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit.MINUTES

/**
 * @author Jaeguk Cho
 */

@Service
class AkinatorCache {
    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(30, MINUTES)
        .build<String, Akiwrapper>()

    fun akinatorExists(userId: String): Boolean {
        return cache.asMap().containsKey(userId)
    }

    fun getAkinator(userId: String): Akiwrapper {
        return cache.get(userId) {
            AkiwrapperBuilder().apply {
                language = KOREAN
                theme = CHARACTER
            }.build()
        }
    }

    fun deleteAkinator(userId: String) {
        cache.invalidate(userId)
    }
}
