package com.spontivly.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<EventChatItem> mEventChatList;
    private OnItemClickListener mListener;

    public ChatAdapter(ArrayList<EventChatItem> chatList) {
        mEventChatList = chatList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ChatViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.msgEvent);
            mTextView2 = itemView.findViewById(R.id.msgMembers);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(v, mListener);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EventChatItem currentItem = mEventChatList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getEventName());
        holder.mTextView2.setText(currentItem.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return mEventChatList.size();
    }

    public String getEvent(int position) {
        EventChatItem currentItem = mEventChatList.get(position);
        String event = currentItem.getEventName();
        return event;
    }

    public int getImage(int position) {
        EventChatItem currentItem = mEventChatList.get(position);
        int image = currentItem.getImageResource();
        return image;
    }

    public EventChatItem getEventChatItem(int position) {
        return mEventChatList.get(position);
    }

}
