package com.ajikartiko.gowisuda;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.ajikartiko.gowisuda.model.Message;
import com.ajikartiko.gowisuda.utils.MessageType;
import com.ajikartiko.gowisuda.utils.OnItemClickListener;
import com.ajikartiko.gowisuda.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY;
import static com.ajikartiko.gowisuda.utils.Utils.humanReadableByteCountBin;

public class ChatAdapter extends ListAdapter<Message, ChatAdapter.ChatViewHolder> {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private final OnItemClickListener listener;

    public ChatAdapter(OnItemClickListener listener) {
        super(Message.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = getItem(position);
        if (message != null) {
            holder.bindTo(message, listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) ? RIGHT : LEFT;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView textMessage, textDate;
        AppCompatImageButton download;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.message_root);
            textMessage = itemView.findViewById(R.id.showmessage);
            textDate = itemView.findViewById(R.id.message_time);
            download = itemView.findViewById(R.id.fileImage);
        }

        public void bindTo(Message message, final OnItemClickListener listener) {
            setLayoutGravity();
            if (message.getMessageType() == MessageType.FILE) {
                String[] fileNames = message.getContent().split("/");
                Context context = textMessage.getContext();
                File fileDir = new File(context.getFilesDir(), "docs");
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                File file = new File(fileDir, fileNames[fileNames.length - 1]);
                if (file.exists()) {
                    download.setImageResource(R.drawable.ic_file_copy);
                    textMessage.setText(fileNames[fileNames.length - 1]);
                } else {
                    download.setImageResource(R.drawable.ic_download);
                    StorageReference ref = FirebaseStorage.getInstance().getReference(message.getContent());

                    ref.getMetadata().addOnSuccessListener(storageMetadata ->
                            textMessage.setText(Html.fromHtml(context.getResources().getString(R.string.chat_msg, storageMetadata.getName(), humanReadableByteCountBin(storageMetadata.getSizeBytes())), FROM_HTML_MODE_LEGACY))
                    );
                }
                download.setVisibility(View.VISIBLE);
                download.setOnClickListener(v -> {
                    if (file.exists()) {
                        listener.onItemClick(FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file));
                    } else {
                        downloadFile(context, message.getContent(), file, listener);
                    }
                });
            } else {
                download.setVisibility(View.GONE);
                textMessage.setText(message.getContent());
            }
            textDate.setText(Utils.dateToString(message.getTime(), "EE, dd-MMM-yyyy HH:mm"));
        }

        private void downloadFile(Context context, String filename, File downloadFile, OnItemClickListener listener) {
            StorageReference downloadRef = FirebaseStorage.getInstance().getReference().child(filename);
            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
            progressDrawable.setColorSchemeColors(R.color.colorPrimary);
            progressDrawable.setStrokeWidth(5);
            progressDrawable.setCenterRadius(30);
            progressDrawable.start();
            downloadRef.getFile(downloadFile)
                    .addOnProgressListener(snapshot -> download.setImageDrawable(progressDrawable))
                    .addOnFailureListener(exception -> Toast.makeText(context, "downloadFile Failed " + exception.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> {
                        progressDrawable.stop();
                        if (task.isSuccessful()) {
                            download.setImageResource(R.drawable.ic_file_copy);
                            listener.onItemClick(FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", downloadFile));
                        } else {
                            download.setImageResource(R.drawable.ic_download);
                        }
                    });
        }


        private void setLayoutGravity() {
            if (getItemViewType() == LEFT) {
                mainLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START));
                mainLayout.setBackgroundResource(R.drawable.background_left);
            } else {
                mainLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END));
                mainLayout.setBackgroundResource(R.drawable.background_right);
            }
        }
    }
}
