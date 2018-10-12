package lib.kalu.glide;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

public final class LibGlideUrl extends GlideUrl {

    public LibGlideUrl(URL url) {
        super(url);
    }

    public LibGlideUrl(String url) {
        super(url);
    }

    public LibGlideUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public LibGlideUrl(String url, Headers headers) {
        super(url, headers);
    }
}
