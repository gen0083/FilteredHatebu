package jp.gcreate.product.filteredhatebu.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.simpleframework.xml.core.Persister
import java.io.File

/**
 * Copyright 2016 G-CREATE
 */
class HatebuFeedItemTest {

    @Test
    @Throws(Exception::class)
    fun equals_same_case() {
        val a = HatebuFeedItem()
        val b = HatebuFeedItem()
        // field about is required
        a.about = "a"
        b.about = "a"
        // field title is required
        a.title = "a"
        b.title = "a"
        // field link is required
        a.link = "a"
        b.link = "a"
        // field description is not required
        a.description = "a"
        b.description = "a"
        // field encoded is required
        a.encoded = "a"
        b.encoded = "a"
        // field date is required
        a.date = "a"
        b.date = "a"
        // field subject is not required
        a.subject = "a"
        b.subject = "a"
        // field count is required
        a.count = 1
        b.count = 1

        assertThat(a == b, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun equals_same_case_with_null() {
        val a = HatebuFeedItem()
        val b = HatebuFeedItem()
        // field about is required
        a.about = "a"
        b.about = "a"
        // field title is required
        a.title = "a"
        b.title = "a"
        // field link is required
        a.link = "a"
        b.link = "a"
        // field description is not required
        a.description = null
        b.description = null
        // field encoded is required
        a.encoded = "a"
        b.encoded = "a"
        // field date is required
        a.date = "a"
        b.date = "a"
        // field subject is not required
        a.subject = null
        b.subject = null
        // field count is required
        a.count = 1
        b.count = 1

        assertThat(a == b, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun equals_different_case() {
        val a = HatebuFeedItem()
        val b = HatebuFeedItem()
        // field about is required
        a.about = "a"
        b.about = "a"
        // field title is required
        a.title = "a"
        b.title = "a"
        // field link is required
        a.link = "a"
        b.link = "a"
        // field description is not required
        a.description = "a"
        b.description = "a"
        // field encoded is required
        a.encoded = "a"
        b.encoded = "ab"
        // field date is required
        a.date = "a"
        b.date = "a"
        // field subject is not required
        a.subject = "a"
        b.subject = "a"
        // field count is required
        a.count = 1
        b.count = 1

        assertThat(a == b, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun equals_different_case_with_null() {
        val a = HatebuFeedItem()
        val b = HatebuFeedItem()
        // field about is required
        a.about = "a"
        b.about = "a"
        // field title is required
        a.title = "a"
        b.title = "a"
        // field link is required
        a.link = "a"
        b.link = "a"
        // field description is not required
        a.description = null
        b.description = "a"
        // field encoded is required
        a.encoded = "a"
        b.encoded = "a"
        // field date is required
        a.date = "a"
        b.date = "a"
        // field subject is not required
        a.subject = "a"
        b.subject = "a"
        // field count is required
        a.count = 1
        b.count = 1

        assertThat(a == b, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun decode() {
        val serializer = Persister()
        val file = File(javaClass.classLoader.getResource("mock_hatebu_hotentry.rss").file)
        val feed = serializer.read(HatebuFeed::class.java, file)
        val actual = feed.itemList[0]
        assertThat(actual.title, `is`("test0-1"))
        assertThat(actual.link, `is`("https://twitter.com/gen0083"))
    }
    
    @Test fun `new condition decode xml`() {
        // あるタイミングからおそらくdc:subject（タグ）が複数出力されるようになった
        val serializer = Persister()
        val file = File(javaClass.classLoader.getResource("feedburner_hotentry.xml").file)
        val feed = serializer.read(HatebuFeed::class.java, file)
        val actual = feed.itemList[2]
        print(actual)
    }
}