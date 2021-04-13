package com.ajikartiko.go_wisuda_dosen.utils;

import com.ajikartiko.go_wisuda_dosen.model.ChatChannel;
import com.ajikartiko.go_wisuda_dosen.model.Dosen;
import com.ajikartiko.go_wisuda_dosen.model.Message;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUtil {
    public FirebaseFirestore firestore;
    private final FirebaseAuth mAuth;

    public FirestoreUtil() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public DocumentReference userDocReference(String userId) {
        return firestore.collection("users").document(userId);
    }

    public CollectionReference chatCollection() {
        return firestore.collection("chat");
    }

    public void createOrGetUserProfile(FireStoreListener listener) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DocumentReference reference = userDocReference(firebaseUser.getUid());
        reference.get().addOnSuccessListener(documentSnapshot -> {
            User user;
            if (!documentSnapshot.exists()) {
                if (firebaseUser != null) {
                    user = new Dosen(mAuth.getCurrentUser().getUid(), "", firebaseUser.getEmail(), (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "", UserType.DOSEN);
                    Map<String, Object> mapUser = Utils.modelToMap(user);
                    mapUser.remove("userId");

                    reference.set(mapUser).addOnSuccessListener(aVoid -> {
                        user.setUserId(reference.getId());
                        listener.onSuccess(user);
                    });
                }
            } else {
                listener.onSuccess(Utils.documentToUser(documentSnapshot));
            }
        });
    }

    public void getEngagedChat(String userId) {
        DocumentReference currentUserRef = userDocReference(userId);
        currentUserRef.collection("engagedChat").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<String> userIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    userIds.add(document.getId());
                }
                firestore.collection("users").whereIn("id", userIds).get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    List<User> users = queryDocumentSnapshots1.toObjects(User.class);
                });
            }
        });
    }

    public void getUsersId(String email, DocIdListener listener) {
        firestore.collection("users").whereEqualTo("email", email).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listener.onSuccess(document.getId());
                }
            }
        });
    }

    public void getUsersByType(UserType type, OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection("users").whereEqualTo("type", type).get().addOnCompleteListener(listener);
    }

    public void getOrCreateChatChannels(String otherUser, DocIdListener listener) {
        String currentId = mAuth.getCurrentUser().getUid();
        DocumentReference currentUserRef = userDocReference(currentId);
        currentUserRef.collection("engagedChat").document(otherUser).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                listener.onSuccess((String) documentSnapshot.get("chatId"));
            } else {
                if (currentId != null) {
                    DocumentReference newChanel = chatCollection().document();
                    String[] strings = {currentId, otherUser};
                    newChanel.set(new ChatChannel(Arrays.asList(strings)));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("chatId", newChanel.getId());
                    currentUserRef.collection("engagedChat")
                            .document(otherUser)
                            .set(hashMap);

                    userDocReference(otherUser).collection("engagedChat")
                            .document(currentId)
                            .set(hashMap);

                    listener.onSuccess(newChanel.getId());
                }
            }
        });
    }

    public ListenerRegistration addChatMessagesListener(String chatId, MessagesListener listener) {
        return chatCollection().document(chatId).collection("messages").orderBy("time").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                listener.onSuccess(value.toObjects(Message.class));
            }
        });
    }

    public void getAllChat(String chatId, MessagesListener listener) {
        chatCollection().document(chatId).collection("messages").orderBy("time").get().addOnCompleteListener(task -> {
            task.isSuccessful();
            if (task.getResult() != null) {
                listener.onSuccess(task.getResult().toObjects(Message.class));
            }
        });
    }

    public void sendMessage(Message message, String chatId, OnCompleteListener<DocumentReference> listener) {
        chatCollection().document(chatId).collection("messages").add(message).addOnCompleteListener(listener);
    }
}
