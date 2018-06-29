package jp.gcreate.product.filteredhatebu.ui.common

import androidx.core.text.getSpans
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Copyright 2018 G-CREATE
 */
class UrlSpanFactoryTest {
    private val sut = UrlSpanFactory(mockk())
    
    @Test fun `URLが含まれない場合は何もしない`() {
        val comment = "サンプルコメント"
        val actual = sut.newSpannable(comment)
        val spans = actual.getSpans<AutoLinkClickableSpan>()
        assertThat(spans.size).isEqualTo(0)
    }
    
    @Test fun `URLにAutoLinkClickableSpanが設定される`() {
        val comment = "https://gcreate.jp/"
        val spannable = sut.newSpannable(comment)
        val spans = spannable.getSpans<AutoLinkClickableSpan>()
        assertThat(spans.size).isEqualTo(1)
        assertThat(spans[0].url).isEqualTo("https://gcreate.jp/")
    }
    
    @Test fun `2つ以上のURLが含まれる場合、それぞれリンクされる`() {
        val comment = "コメントに2つのURLがあるケース。https://gcreate.jpとhttps://android.gcreate.jpと"
        val spannable = sut.newSpannable(comment)
        val spans = spannable.getSpans<AutoLinkClickableSpan>()
        assertThat(spans.size).isEqualTo(2)
        assertThat(spans[0].url).isEqualTo("https://gcreate.jp")
        assertThat(spans[1].url).isEqualTo("https://android.gcreate.jp")
    }
}