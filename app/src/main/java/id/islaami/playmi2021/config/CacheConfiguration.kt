package id.islaami.playmi2021.config

import android.content.Context

import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Book
import io.paperdb.Paper

/**
 * Paper is a fast NoSQL-like storage for Java/Kotlin objects on Android with automatic schema migration support.
 * See: https://github.com/pakoito/RxPaper2
 */

object CacheLibrary {
    fun init(context: Context) = RxPaperBook.init(context)
}

class Cache<T> {
    private val book: Book = Paper.book()

    fun load(key: String): T = book.read(key)

    fun save(key: String, anyObject: T): T =
        book.write(key, anyObject).run { anyObject }
}