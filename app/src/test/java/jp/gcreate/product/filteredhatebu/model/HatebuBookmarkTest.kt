package jp.gcreate.product.filteredhatebu.model

import com.squareup.moshi.Moshi
import jp.gcreate.product.filteredhatebu.api.response.HatebuEntry
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Copyright 2017 G-CREATE
 */
class HatebuBookmarkTest {

    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        moshi = Moshi.Builder().build()
    }

    @Test
    @Throws(Exception::class)
    fun decode() {
        val file = File(javaClass.classLoader!!.getResource("mock_hatebu_entry.json").file)
        val source = file.source().buffer()
        val adapter = moshi.adapter(HatebuEntry::class.java)
        val entry = adapter.fromJson(source)
        val (user, _, _, comment) = entry!!.bookmarks[0]
        assertThat(comment, `is`("test"))
        assertThat(user, `is`("test"))
        source.close()
    }

    @Test fun `get comments`() {
        val file = File(javaClass.classLoader!!.getResource("hatebu_comments.json").file)
        val source = file.source().buffer()
        val adapter = moshi.adapter(HatebuEntry::class.java)
        val entry = adapter.fromJson(source)
        entry?.bookmarks?.forEach(::println) ?: fail("bookmarks null")
        source.close()
    }

    @Test fun `get comments cannot parse case?`() {
        val file =
            File(javaClass.classLoader!!.getResource("hatebu_comments_cannot_parse.json").file)
        val source = file.source().buffer()
        val adapter = moshi.adapter(HatebuEntry::class.java)
        val entry = adapter.fromJson(source)
        entry?.bookmarks?.forEach(::println) ?: fail("bookmarks null")
        source.close()
    }
}
