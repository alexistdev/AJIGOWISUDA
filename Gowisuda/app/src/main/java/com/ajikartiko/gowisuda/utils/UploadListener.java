package com.ajikartiko.gowisuda.utils;

import android.net.Uri;

public interface UploadListener {
    void onSuccess(Uri uri);
    void onFailure(Exception e);
}
