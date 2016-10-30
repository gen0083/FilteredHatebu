package jp.gcreate.product.filteredhatebu.api;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

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

    @Inject
    public MockInterceptor(@ApplicationContext Context context) {
        this.context = context;
        categoryUrl = Pattern.compile("hotentry/.+\\.rss");
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        Timber.d("url:%s path:%s", url, url.encodedPath());
        if (FeedsBurnerClienet.BASE_URL.contains(url.host())) {
            return mockedFeedsBurnerResponse(chain.request());
        }
        if (HatenaClient.BASE_URL.contains(url.host())) {
            if (url.encodedPath().equals("/entry/json/") || url.encodedPath().equals("/entry/jsonlite/")) {
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
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/xml"), openFile("mock_hatebu_hotentry.rss")))
                .build();
    }

    private Response mockedHatebuCategory(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/xml"), openFile("mock_hatebu_hotentry_category.rss")))
                .build();
    }

    private Response mockedHatebuEntry(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/json"), openFile("mock_hatebu_entry.json")))
                .build();
    }

    private Response mockedHatebuEntryNoComment(Request request) throws IOException {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok")
                .body(ResponseBody.create(MediaType.parse("application/json"), openFile("mock_hatebu_entry_no_comment.json")))
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
        InputStream stream = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        stream.close();
        return sb.toString();
    }
}
