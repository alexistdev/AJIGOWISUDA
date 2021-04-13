package com.ajikartiko.go_wisuda_dosen.utils;

import android.net.Uri;

public interface UploadListener {
    void onSuccess(Uri uri);
    void onFailure(Exception e);
}
