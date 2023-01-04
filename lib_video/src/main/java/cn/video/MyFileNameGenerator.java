package cn.video;

import android.text.TextUtils;
import android.util.Log;

import com.danikula.videocache.ProxyCacheUtils;
import com.danikula.videocache.file.FileNameGenerator;

public class MyFileNameGenerator implements FileNameGenerator {

    private static final int MAX_EXTENSION_LENGTH = 4;

    @Override
    public String generate(String url) {
        String extension = getExtension(url);
        int dotIndex = url.lastIndexOf('.');

        if (url.length() > 18 && dotIndex > 18) {
            return url.substring(dotIndex - 18);
        }
        String name = ProxyCacheUtils.computeMD5(url);
        String s = TextUtils.isEmpty(extension) ? name : name + "." + extension;
        Log.i("HttpProxyCacheServer", "MyFileNameGenerator name =" + s);

        return TextUtils.isEmpty(extension) ? name : name + "." + extension;

//        Uri uri = Uri.parse(url);
//        String videoId = uri.getQueryParameter("videoId");
//        return videoId + ".mp4";
    }

    private String getExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
                url.substring(dotIndex + 1, url.length()) : "";
    }
}
