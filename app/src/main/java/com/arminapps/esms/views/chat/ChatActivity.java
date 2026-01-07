package com.arminapps.esms.views.chat;

import static android.Manifest.permission.SEND_SMS;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arminapps.esms.R;
import com.arminapps.esms.adapters.ChatMessageAdapter;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;
import com.arminapps.esms.databinding.ActivityChatBinding;
import com.arminapps.esms.views.contacts.ContactsActivity;
import com.arminapps.esms.views.main.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    private ActivityChatBinding binding;
    private ChatPresenter presenter;
    String name = "";
    String phoneNumber = "";
    private List<Message> messages = new ArrayList<>();
    private ChatMessageAdapter adapter;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            int bottomInset = Math.max(systemBars.bottom, imeInsets.bottom);

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    bottomInset
            );

            return insets;
        });


        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        presenter = new ChatPresenter(this);
        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public void setup() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getIntent().getBooleanExtra("from_contacts", false))
                    startActivity(new Intent(ChatActivity.this, ContactsActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                else
                    startActivity(new Intent(ChatActivity.this, MainActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        boolean conversationLoading = getIntent().getBooleanExtra("conversation_loading", false);
        String name = getIntent().getStringExtra("name");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (conversationLoading) {
            int conversationId = getIntent().getIntExtra("conversation_id", -1);
            conversation = new Conversation(name, phoneNumber);
            conversation.setId(conversationId);
        }
        else {
            conversation = new Conversation(name, phoneNumber);
            conversation.setId(-1);
        }
        presenter.checkConversationExist(conversation);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {

            }
            else {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Permission Required")
                        .setMessage("eSMS requires permission of sending SMS and can't function properly without it.")
                        .setPositiveButton("Grant Permission", (dialog, which) -> {
                            requestPermissionLauncher.launch(SEND_SMS);
                        })
                        .setNegativeButton("Leave chat", (dialog, which) -> {
                            startActivity(new Intent(ChatActivity.this, MainActivity.class)
                                    .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, SEND_SMS) != PERMISSION_GRANTED)
            requestPermissionLauncher.launch(SEND_SMS);

        binding.textInputLayout.setEndIconOnClickListener(v -> sendMessageBtnClickAction());

        adapter = new ChatMessageAdapter(this, messages);
        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        ));
        binding.chatsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void sendMessageBtnClickAction() {
        String messageText = binding.txtChatMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            binding.txtChatMessage.setText("");
            Message message = new Message(true, messageText, new Date().getTime(), conversation.getId());
            presenter.sendSMS(conversation, message);
        }
    }

    @Override
    public void setMessages(List<Message> messages) {
        if (messages.isEmpty()) {
            binding.viewNoMessage.setVisibility(VISIBLE);
            binding.chatsRecyclerView.setVisibility(GONE);
        }
        else {
            binding.chatsRecyclerView.setVisibility(VISIBLE);
            binding.viewNoMessage.setVisibility(GONE);
            this.messages.clear();
            this.messages.addAll(messages);
            adapter.notifyDataSetChanged();
            binding.chatsRecyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void loadConversation(Conversation conversation) {
        this.conversation = conversation;
        binding.toolbar.setTitle(conversation.getName());
        binding.toolbar.setSubtitle(conversation.getPhoneNumber());
        presenter.loadMessages(conversation);
    }

    @Override
    public void messageSent(Message message) {
        if (conversation.getId() == -1) {
            Log.i("TAG", "messageSent: " + new Gson().toJson(message) + "\nconv: " + new Gson().toJson(conversation));
            conversation.setId(message.getConversationId());
        }
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
        if (messages.isEmpty()) {
            binding.viewNoMessage.setVisibility(VISIBLE);
            binding.chatsRecyclerView.setVisibility(GONE);
        }
        else {
            binding.chatsRecyclerView.setVisibility(VISIBLE);
            binding.viewNoMessage.setVisibility(GONE);
            binding.chatsRecyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void conversationRemoved() {
        startActivity(new Intent(ChatActivity.this, MainActivity.class)
                .setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Subscribe
    public void onMessageReceived(Message message) {
        if (message.getConversationId() == conversation.getId() && conversation.getId() != -1)
            messageSent(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showSecurityKeyChangingDialog(String key) {
        TextInputLayout textInputLayout = new TextInputLayout(this, null, com.google.android.material.R.attr.textInputStyle);
        TextInputEditText inputEditText = new TextInputEditText(this);
        textInputLayout.setPasswordVisibilityToggleEnabled(true);
        inputEditText.setHint("Enter security key");
        inputEditText.setText(key);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputLayout.addView(inputEditText);

        int padding = getResources().getDimensionPixelSize(R.dimen.password_dialog_padding);
        textInputLayout.setPadding(padding, 0, padding, 0);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(textInputLayout)
                .setPositiveButton("OK", (dialog, which) -> {
                    String securityKey = inputEditText.getText().toString().trim();
                    conversation.setSecurityKey(securityKey);
                    presenter.setSecurityKey(conversation, securityKey);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void securityKeySet() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_change_security_key) {
            if (conversation.getId() != -1)
                showSecurityKeyChangingDialog(conversation.getSecurityKey());
            return true;
        }
        else if (itemId == R.id.action_clear_chat) {
            if (conversation != null || conversation.getId() != -1) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("CAUTION")
                        .setMessage("Do you really want to clear this conversation? There's no way to recover it later.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.removeConversation(conversation);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}