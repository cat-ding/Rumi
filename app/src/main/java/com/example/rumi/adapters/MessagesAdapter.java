package com.example.rumi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumi.R;
import com.example.rumi.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    public static final String TAG = "MessagesAdapter";

    private Context context;
    private List<Message> messages;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    public void addAllMessage(List<Message> messagesList) {
        messages.addAll(messagesList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage, tvRelativeTime;
        RelativeLayout relativeLayout;
        String currId;
        RelativeLayout.LayoutParams messageParams;
        RelativeLayout.LayoutParams timeParams;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            messageParams = (RelativeLayout.LayoutParams)tvMessage.getLayoutParams();
            timeParams = (RelativeLayout.LayoutParams)tvRelativeTime.getLayoutParams();

            currId = firebaseAuth.getCurrentUser().getUid();
        }

        public void bind(Message message) {
            if (message.getUserId().equals(currId)) {
                timeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                timeParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
                messageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                messageParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
                tvMessage.setBackgroundResource(R.drawable.message_border);
                tvMessage.setTextColor(Color.WHITE);
            } else {
                timeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                timeParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
                messageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                messageParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
                tvMessage.setBackgroundResource(R.drawable.message_received_border);
                tvMessage.setTextColor(Color.BLACK);
            }
            tvRelativeTime.setLayoutParams(timeParams);
            tvRelativeTime.setText(message.getRelativeTime());
            tvMessage.setPadding(30, 20, 30, 20);
            tvMessage.setLayoutParams(messageParams);
            tvMessage.setText(message.getBody());
        }
    }
}
