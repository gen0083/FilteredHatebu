package jp.gcreate.product.filteredhatebu.api;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.BuildConfig;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class MockInterceptor implements Interceptor {
    private Context context;
    private Pattern categoryUrl;
    private int     count;

    @Inject
    public MockInterceptor(@ApplicationContext Context context) {
        this.context = context;
        categoryUrl = Pattern.compile("hotentry/.+\\.rss");
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        Timber.d("url:%s path:%s", url, url.encodedPath());

        // Simulate slow network on debug build
        if (BuildConfig.DEBUG) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (FeedsBurnerClienet.BASE_URL.contains(url.host())) {
            return mockedFeedsBurnerResponse(chain.request());
        }
        if (HatenaClient.BASE_URL.contains(url.host())) {
            if (url.encodedPath().equals("/entry/json/") ||
                url.encodedPath().equals("/entry/jsonlite/")) {
                if (url.query().contains("test.com/")) {
                    return mockedHatebuEntry(chain.request());
                } else {
                    return mockedHatebuEntryNoComment(chain.request());
                }
            }
            Matcher m = categoryUrl.matcher(url.encodedPath());
            if (m.find()) {
                return mockedHatebuCategory(chain.request());
            }
        }
        return mockedNotFound(chain.request());
    }

    private Response mockedFeedsBurnerResponse(Request request) throws IOException {
        // フィードの再取得を行った際に、表示中と変化がない場合はリストを更新しない
        // しかしこの挙動は、テストがしづらい（新しい記事が返ってくるかどうかは時間が経過しないと確認できない）
        // そこで、mockレスポンスは5回に1回異なるフィードを返すことで
        // 新しいフィードが取得できたときをエミュレートする
        count++;
        Response.Builder builder = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok");
        builder = (count % 5 != 0) ?
                  builder.body(ResponseBody.create(MediaType.parse("application/xml"),
                                                   openFile("mock_hatebu_hotentry.rss")))
                                   :
                  builder.body(ResponseBody.create(MediaType.parse("application/xml"),
                                                   openFile("mock_hatebu_hotentry2.rss")));
        return builder.build();
    }

    private Response mockedHatebuCategory(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/xml"),
                                          openFile("mock_hatebu_hotentry_category.rss")))
                .build();
    }

    private Response mockedHatebuEntry(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/json"),
                                          openFile("mock_hatebu_entry.json")))
                .build();
    }

    private Response mockedHatebuEntryNoComment(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/json"),
                                          openFile("mock_hatebu_entry_no_comment.json")))
                .build();
    }

    private Response mockedNotFound(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("not found")
                .body(ResponseBody.create(MediaType.parse("text/html"), "404 not found."))
                .build();
    }

    private String openFile(String filename) throws IOException {
        InputStream    stream = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String         line;
        StringBuilder  sb     = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        stream.close();
        return sb.toString();
    }
}
