package com.ajikartiko.go_wisuda_dosen;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajikartiko.go_wisuda_dosen.model.Message;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.FirestoreUtil;
import com.ajikartiko.go_wisuda_dosen.utils.MessageType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private User otherUser;
    private String currentChatId;
    private TextInputEditText textInput;
    private RecyclerView recyclerView;
    private final FirestoreUtil firestoreUtil = new FirestoreUtil();
    private ListenerRegistration messagesListenerRegistration;
    private ChatAdapter mAdapter;
    private static final int PICK_PDF_FILE = 2;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new CustomProgressDialog(this);
        FloatingActionButton sendButton = findViewById(R.id.fab_send_message);
        FloatingActionButton pickFile = findViewById(R.id.fab_send_image);
        textInput = findViewById(R.id.editText_message);
        mAdapter = new ChatAdapter(item -> {
            if (item instanceof Uri) {
                openFile((Uri) item);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        otherUser = (User) getIntent().getSerializableExtra("OTHER_USER");
        if (otherUser != null) {
            initToolbar();
            sendButton.setOnClickListener(v -> {
                String text = textInput.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    Message message = new Message(otherUser.getUserId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Calendar.getInstance().getTime(), text, MessageType.TEXT);
                    firestoreUtil.sendMessage(message, currentChatId, task -> {
                        if (task.isSuccessful()) {
                            textInput.setText(null);
                        }
                    });
                } else {
                    Toast.makeText(this, "pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            });
            pickFile.setOnClickListener(this::pickFile);
        }
    }

    public void openFile(Uri uri) {
        String mime = getContentResolver().getType(uri);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.setClipData(ClipData.newRawUri("Files", uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, null));
    }

    private void pickFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/*");
        String[] data = {"application/pdf", "application/msword", "application/ms-doc", "application/doc", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, data);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestoreUtil.getOrCreateChatChannels(otherUser.getUserId(), docId -> {
            currentChatId = docId;
            messagesListenerRegistration = firestoreUtil.addChatMessagesListener(currentChatId, this::updateRecyclerView);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                ContentResolver contentResolver = this.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(contentResolver.getType(uri));
                String displayName = String.valueOf(System.currentTimeMillis()).concat(".").concat(type);
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    progressDialog.show();
                    StorageReference ref = FirebaseStorage.getInstance()
                            .getReference()
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(displayName.replace("/", "-").replace(" ", "-"));
                    ref.putFile(uri).addOnProgressListener(snapshot -> {
                        progressDialog.progressBar.setProgress(Math.toIntExact((100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount())));
                        progressDialog.titleDialog.setText(getResources().getString(R.string.uploading));
                    }).addOnSuccessListener(taskSnapshot -> {
                        Message message = new Message(otherUser.getUserId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Calendar.getInstance().getTime(), ref.getPath(), MessageType.FILE);
                        firestoreUtil.sendMessage(message, currentChatId, task -> {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                            }
                        });
                    });
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            if (otherUser != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle((TextUtils.isEmpty(otherUser.getName())) ? "chat" : otherUser.getName());
                getSupportActionBar().setLogo(R.drawable.ic_people);
                if (!TextUtils.isEmpty(otherUser.getImage())) {
                    RequestOptions requestOptions = new RequestOptions().override(75, 75).diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(com.ajikartiko.go_wisuda_dosen.ChatActivity.this).asDrawable().apply(requestOptions).load(otherUser.getImage()).error(R.drawable.user).fallback(R.drawable.user).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            getSupportActionBar().setLogo(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            getSupportActionBar().setLogo(placeholder);
                        }
                    });
                } else {
                    getSupportActionBar().setLogo(R.drawable.ic_people);
                }
            }
        }
    }

    private void updateRecyclerView(List<Message> messages) {
        mAdapter.submitList(messages);
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesListenerRegistration != null) {
            messagesListenerRegistration.remove();
        }
    }
}