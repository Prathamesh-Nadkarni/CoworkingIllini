package com.example.coworkingillini;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private TextView txtView;

    private List<Message> messages;
    private MessageAdapter adapter;
    private FirebaseDatabase database;

    private String name;
    ImageView chat_header_results;
    Bundle bundle;
    String key, otherUsername;
    private boolean isFirstInKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
        createNotificationChannel();

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        txtView = findViewById(R.id.chat_header_profile_name);

        database = FirebaseDatabase.getInstance();
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        Intent intent1 = getIntent();
        bundle = intent1.getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            name = bundle.getString("Username");
            String[] usernames = key.split(":");
            otherUsername = usernames[0].equals(name) ? usernames[1] : usernames[0];
            determineKeyPosition();
        }
        chat_header_results = findViewById(R.id.chat_header_results);
        chat_header_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, Chat_results.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        txtView.setText(otherUsername);

        DatabaseReference chatsRef = database.getReference("chats");
        loadMessages(chatsRef);

        sendButton.setOnClickListener(v -> sendMessage(chatsRef));
    }

    private void determineKeyPosition() {
        if (key != null && key.contains(":")) {
            String[] parts = key.split(":");
            if (parts.length == 2) {
                isFirstInKey = parts[0].equals(name);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat_notifications";
            String channelName = "Chat Notifications";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("Notification", "Notification channel created");
        }
    }

    private void showNotification(String messageContent) {
        Log.d("Notification", "Showing notification for: " + messageContent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "chat_notifications")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Messages")
                .setContentText(messageContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

    private void loadMessages(DatabaseReference chatsRef) {

        DatabaseReference messagesRef = chatsRef.child(key).child("messages");
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    messagesRef.setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        chatsRef.child(key).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                boolean hasUnreadMessages = false;
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String rawMessage = messageSnapshot.getValue(String.class);
                    if (rawMessage != null && rawMessage.contains(":")) {
                        String[] parts = rawMessage.split(":", 2);
                        if (parts.length == 2) {
                            char messageType = parts[0].charAt(1);
                            String messageContent = parts[1].trim();

                            boolean isReceived = (isFirstInKey && messageType == 'B') ||
                                    (!isFirstInKey && messageType == 'A');
                            Message message = new Message(messageContent, isReceived);
                            messages.add(message);

                            if (isReceived && !message.isRead()) {
                                hasUnreadMessages = true;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);

                if (hasUnreadMessages) {
                    showNotification("You have new unread messages");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Failed to fetch messages: " + error.getMessage());
            }
        });
    }

    private void sendMessage(DatabaseReference chatsRef) {
        String messageContent = messageEditText.getText().toString().trim();
        if (!messageContent.isEmpty()) {
            String newMessageKey = chatsRef.child(key).child("messages").push().getKey();
            if (newMessageKey != null) {
                String prefix = isFirstInKey ? "0A:" : "0B:";
                String formattedMessage = prefix + messageContent;

                chatsRef.child(key).child("messages").child(newMessageKey).setValue(formattedMessage);
            }

            messages.add(new Message(messageContent, false));
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
            messageEditText.setText("");
        }
    }

    private void markMessagesAsRead() {
        for (Message message : messages) {
            if (message.isReceived() && !message.isRead()) {
                message.setRead(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        markMessagesAsRead();
    }
}