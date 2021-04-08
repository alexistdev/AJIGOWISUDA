package com.ajikartiko.gowisuda;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT= 0;
    public static final int MSG_TYPE_RIGHT= 1;


    private Context mContext;
    private List<ChatModel> mchat;
    FirebaseUser firebaseUser;


    public MessageAdapter(Context mContext, List<ChatModel> mchat){
        this.mContext = mContext;
        this.mchat = mchat;
    }

    @NonNull
    @Override
    public MessageAdapter. ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatitem_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatitem_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chatModel=  mchat.get(position);
        holder.show_message.setText(chatModel.getMessage());

    }


    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView show_message;
        CircleImageView profileimage;

        public ViewHolder(@NonNull View itemView) {


            super(itemView);

            show_message = itemView.findViewById(R.id.showmessage);
            profileimage = itemView.findViewById(R.id.alluserimage);

        }
    }

    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mchat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }

}
