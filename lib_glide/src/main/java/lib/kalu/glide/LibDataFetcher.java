package lib.kalu.glide;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class LibDataFetcher implements DataFetcher<InputStream> {

    private final OkHttpClient client;
    private final GlideUrl url;
    private InputStream obtain;
    private ResponseBody responseBody;
    private volatile boolean isCancelled;

    public LibDataFetcher(OkHttpClient client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {

        // step1
        final Request.Builder requestBuilder = new Request.Builder().url(url.toStringUrl());
        // step2
        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        // step3
        final Request request = requestBuilder.build();
        if (isCancelled) {
            callback.onLoadFailed(new IOException("已取消"));
        }

        try {

            final Response response = client.newCall(request).execute();
            responseBody = response.body();

            if (!response.isSuccessful() || responseBody == null) {
                throw new IOException("Request failed with code: " + response.code());
            }

            obtain = ContentLengthInputStream.obtain(responseBody.byteStream(), responseBody.contentLength());
            callback.onDataReady(obtain);

        } catch (Exception e) {
            Log.e("kalu", e.getMessage(), e);
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            if (obtain != null) {
                obtain.close();
            }
            if (responseBody != null) {
                responseBody.close();
            }
        } catch (IOException e) {
            Log.e("kalu", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}