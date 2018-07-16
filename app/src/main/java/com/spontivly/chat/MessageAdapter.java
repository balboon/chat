package com.spontivly.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spontivly.chat.ChatItem;
import com.spontivly.chat.MyMessageItem;
import com.spontivly.chat.R;
import com.spontivly.chat.models.SpontivlyEvent;
import com.spontivly.chat.services.DatabaseService;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgViewHolder>{
    private ArrayList<MyMessageItem> mMsgList;
    private MessageAdapter.OnItemClickListener mListener;
    private DatabaseService dbService;

    public MessageAdapter(ArrayList<MyMessageItem> msgList) {
        mMsgList = msgList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MessageAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView msg;
        public TextView name;


        public MsgViewHolder(View itemView, final MessageAdapter.OnItemClickListener listener) {
            super(itemView);
            msg = itemView.findViewById(R.id.my_message);

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
    public MessageAdapter.MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // If messenger is me
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);

        // Else
        // View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_message, parent, false);
        MessageAdapter.MsgViewHolder msgViewHolder = new MessageAdapter.MsgViewHolder(v, mListener);
        return msgViewHolder;
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        MyMessageItem currentItem = mMsgList.get(position);
        holder.msg.setText(currentItem.getMsg());
        // Get time
    }

    @Override
    public int getItemCount() {
        if (mMsgList != null) {
            return mMsgList.size();
        } else {
            return -1;
        }
    }
}
