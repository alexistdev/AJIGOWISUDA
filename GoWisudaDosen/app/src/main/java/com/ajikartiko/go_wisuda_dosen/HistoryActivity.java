package com.ajikartiko.go_wisuda_dosen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajikartiko.go_wisuda_dosen.model.History;
import com.ajikartiko.go_wisuda_dosen.model.Message;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.FirestoreUtil;
import com.ajikartiko.go_wisuda_dosen.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryActivity extends AppCompatActivity {
    private HistoryAdapter historyAdapter;
    private String chatId;
    private User otherUser;
    private final FirestoreUtil firestoreUtil = new FirestoreUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        otherUser = (User) getIntent().getSerializableExtra("OTHER_USER");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("History Bimbingan");
            getSupportActionBar().setSubtitle(otherUser.getName());
        }
        historyAdapter = new HistoryAdapter(item -> {
            if (item instanceof History) {
                Intent intent = new Intent(this, HistoryDetailActivity.class);
                intent.putExtra("OTHER_USER", otherUser);
                intent.putExtra("HISTORY_LIST", (History) item);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView_History);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(historyAdapter);

        if (otherUser != null) {
            firestoreUtil.getOrCreateChatChannels(otherUser.getUserId(), docId -> {
                chatId = docId;
                firestoreUtil.getAllChat(docId, this::updateRecyclerView);
            });
        }
    }

    private void updateRecyclerView(List<Message> messages) {
        historyAdapter.submitList(processHistory(messages));
    }

    private List<History> processHistory(List<Message> messages) {
        ArrayList<History> histories = new ArrayList<>();
        if (messages.size() > 0) {
            Map<String, List<Message>> listMap = messages.stream().collect(Collectors.groupingBy(item -> Utils.dateToString(item.getTime(), "EEEE, dd-MMMM-yyyy")));

            for (Map.Entry<String, List<Message>> entry : listMap.entrySet()) {
                History history = new History(chatId, entry.getKey(), entry.getValue());
                histories.add(history);
            }
        }
        histories.sort(Collections.reverseOrder());
        return histories;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}