package jp.gcreate.product.filteredhatebu.api;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gcreate.product.filteredhatebu.BuildConfig;
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil;
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
    private final Context context;
    private final Pattern categoryUrl;
    private int count;

    public MockInterceptor(Context context) {
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

        if (FeedsBurnerClient.BASE_URL.contains(url.host())) {
            // 総合ページのモックフィードを返す
            return mockedFeedsBurnerResponse(chain.request());
        }
        if (HatenaClient.BASE_URL.contains(url.host())) {
            // HatebuFeedDetailActivity
            if (url.encodedPath().equals("/entry/json/") ||
                    url.encodedPath().equals("/entry/jsonlite/")) {
                if (url.query().contains("test.com/")) {
                    // コメントありのページを返す（test.comはモックに2件だけ用意されている）
                    return mockedHatebuEntry(chain.request(), "mock_hatebu_entry.json");
                } else if(url.query().contains("http://www.test4.com/")) {
                    // コメントが許可されていないページ（Bookmarksがnull）
                    return mockedHatebuEntry(chain.request(), "mock_hatebu_entry_disallow_comment.json");
                } else {
                    // それ以外はコメントなしを返す
                    return mockedHatebuEntry(chain.request(), "mock_hatebu_entry_no_comment.json");
                }
            }
            // カテゴリのモックフィードを返す
            Matcher m = categoryUrl.matcher(url.encodedPath());
            if (m.find()) {
                return mockedHatebuCategory(chain.request());
            }
        }

        // favicon
        if (FaviconUtil.FAVICON_URL.contains(url.host())) {
            return mockedFaviconWithParameter(chain.request(), url.queryParameter("url"));
        }
        return mockedNotFound(chain.request());
    }

    private Response mockedFeedsBurnerResponse(Request request) throws IOException {
        // フィードの再取得を行った際に、表示中と変化がない場合はリストを更新しない
        // しかしこの挙動は、テストがしづらい（新しい記事が返ってくるかどうかは時間が経過しないと確認できない）
        // そこで、mockレスポンスは5回に1回異なるフィードを返すことで
        // 新しいフィードが取得できたときの挙動を確認する
        count++;
        Response.Builder builder = createBaseResponseOk(request);
        builder = (count % 5 != 0) ?
                  builder.body(ResponseBody.create(MediaType.parse("application/xml"),
                                                   openFile("mock_hatebu_hotentry.rss")))
                                   :
                  builder.body(ResponseBody.create(MediaType.parse("application/xml"),
                                                   openFile("mock_hatebu_hotentry2.rss")));
        return builder.build();
    }

    private Response mockedHatebuCategory(Request request) throws IOException {
        return createBaseResponseOk(request)
                .body(ResponseBody.create(MediaType.parse("application/xml"),
                                          openFile("mock_hatebu_hotentry_category.rss")))
                .build();
    }

    private Response mockedHatebuEntry(Request request, String jsonFile) throws IOException {
        return createBaseResponseOk(request)
                .body(ResponseBody.create(MediaType.parse("application/json"),
                                          openFile(jsonFile)))
                .build();
    }

    private Response mockedFaviconWithParameter(Request request, String parameter) throws IOException {
        String[] params = {"test", "test2", "test4", "test5", "test6", "test8", "test9", "test10",
                           "test11", "test12", "test13", "test14", "test15", "test16", "test17",
                           "test18", "test19", "test20", "test21", "test22", "test23", "test24",
                           "test25", "test26", "test27", "test28", "test29", "test30"};
        for (String param : params) {
            if (parameter.contains(param + ".com")) return mockedFaviconWithFileName(request, param + ".png");
        }
        return mockedNotFound(request);
    }

    private Response mockedFaviconWithFileName(Request request, String fileName) throws IOException {
        return createBaseResponseOk(request)
                .body(ResponseBody.create(MediaType.parse("image/png"), openImageFile(fileName)))
                .build();
    }

    private Response.Builder createBaseResponseOk(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("ok");
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

    private byte[] openImageFile(String fileName) throws IOException {
        InputStream inputStream   = context.getAssets().open(fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bytes              = new byte[1024];
        while(inputStream.read(bytes) > 0) {
            out.write(bytes);
        }
        byte[] image = out.toByteArray();
        out.close();
        inputStream.close();
        return image;
    }
}
