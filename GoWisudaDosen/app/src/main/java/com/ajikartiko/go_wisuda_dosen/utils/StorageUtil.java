package com.ajikartiko.go_wisuda_dosen.utils;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.UUID;

public class StorageUtil {
    private final FirebaseStorage storage;

    public StorageUtil() {
        storage = FirebaseStorage.getInstance();
    }

    private StorageReference getFolderUser() {
        return storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void uploadFile(final Uri uri, final String filename, final OnCompleteListener<Uri> successListener, final OnFailureListener failureListener) {
        final StorageReference ref = storage.getReference().child("test").child(filename);
        ref.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(successListener).addOnFailureListener(failureListener);
    }

    public void uploadFileWihProgress(final Uri uri, final String filename, final OnCompleteListener<Uri> successListener, final OnFailureListener failureListener){

        final StorageReference ref = storage.getReference().child("test").child(filename);
        ref.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(successListener).addOnFailureListener(failureListener);
    }

    public void uploadChatFile(final Uri uri, final OnCompleteListener<Uri> successListener, final OnFailureListener failureListener) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(uri.getPath()));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf))) baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bbytes = baos.toByteArray();
        final StorageReference ref =storage.getReference().child("test").child(UUID.nameUUIDFromBytes(bbytes).toString());
        ref.putBytes(bbytes).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl();
            Log.e("TAG", "uploadChatFile: "+ref.getDownloadUrl() );
        }).addOnProgressListener(snapshot -> {
            Log.e("TAG", "uploadChatFile: "+(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
        });
    }

    public StorageReference pathToReference(String path) {
        return storage.getReference(path);
    }
}
