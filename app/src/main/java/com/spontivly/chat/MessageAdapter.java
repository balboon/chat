package com.spontivly.chat;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spontivly.chat.models.SpontivlyUser;
import com.spontivly.chat.services.DatabaseService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int MESSAGE_SENT = 0;
    private static final int MESSAGE_RECEIVED = 1;
    private static final int MESSAGE_DATE = 2;
    private ArrayList<MessageItem> mMsgList;
    private MessageAdapter.OnItemClickListener mListener;
    private DatabaseService dbService;
    private SpontivlyUser mUser;

    public MessageAdapter(ArrayList<MessageItem> msgList, SpontivlyUser user) {
        mMsgList = msgList;
        mUser = user;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MessageAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
            return new SentMessageHolder(view);

        } else if (viewType == MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_message, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == MESSAGE_DATE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_message_layout, parent, false);
            return new DateMessageHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem currentItem = mMsgList.get(position);
        if (currentItem.getID() == mUser.userId) {
            return MESSAGE_SENT;
        } else if (currentItem.getUser().equals("")) {
            return MESSAGE_DATE;
        } else {
            return MESSAGE_RECEIVED;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageItem currentItem = mMsgList.get(position);
        switch (holder.getItemViewType()) {
            case MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bindNew(currentItem);
                break;
            case MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(currentItem);
                break;
            case MESSAGE_DATE:
                ((DateMessageHolder) holder).bind(currentItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mMsgList != null) {
            return mMsgList.size();
        } else {
            return -1;
        }
    }
    private class DateMessageHolder extends RecyclerView.ViewHolder {
        TextView dateTime;
        DateMessageHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.date_time);
        }

        void bind(MessageItem messageItem) {
            String currentDate = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
            String msgDate = new SimpleDateFormat("MMMM dd, yyyy").format(messageItem.getTime());
            if (currentDate.equals(msgDate)) {
                dateTime.setText("Today");
            } else {
                dateTime.setText(msgDate);
            }
        }
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView myMsg, myTime;
        SentMessageHolder(View itemView) {
            super(itemView);
            myMsg = itemView.findViewById(R.id.my_message_body);
            myTime = itemView.findViewById(R.id.my_time);
        }

        void bind(MessageItem messageItem) {
            myMsg.setText(messageItem.getMsg());
            Date date = new Date(messageItem.getTime());
            DateFormat formatter = new SimpleDateFormat("hh:mm a");
            String dateFormatted = formatter.format(date);
            myTime.setText(dateFormatted);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView theirMsg, theirName, theirTime;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            theirMsg = itemView.findViewById(R.id.their_message_body);
            theirName = itemView.findViewById(R.id.their_name);
            theirTime = itemView.findViewById(R.id.their_time);
        }

        void bind(MessageItem messageItem) {
            theirMsg.setText(messageItem.getMsg());
        }

        void bindNew(MessageItem messageItem) {
            theirMsg.setText(messageItem.getMsg());
            if (messageItem.getUser().equals(" "))
                theirName.setVisibility(View.GONE);
            theirName.setText(messageItem.getUser());
            Date date = new Date(messageItem.getTime());
            DateFormat formatter = new SimpleDateFormat("hh:mm a");
            String dateFormatted = formatter.format(date);
            theirTime.setText(dateFormatted);
        }
    }
}
