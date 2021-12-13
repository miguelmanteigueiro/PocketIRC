package pt.ubi.di.pdm.teste;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MessageIRC> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MessageRecyclerViewAdapter(Context context, List<MessageIRC> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //go through the entire list and gives each row its layout
    @Override
    public int getItemViewType(int position) {
        //JOIN MESSAGE
        if(mData.get(position).getAction().equals("JOIN"))
        {
            mData.get(position).setMsg("<- " + mData.get(position).getUser()[0] + " joined");
            return R.layout.join_leave_recycler_view_row;
        }

        //PART MESSAGE
        if(mData.get(position).getAction().equals("PART"))
        {
            mData.get(position).setMsg("<- " + mData.get(position).getUser()[0] + " left");
            return R.layout.join_leave_recycler_view_row;
        }

        //QUIT MESSAGE
        if(mData.get(position).getAction().equals("QUIT"))
        {
            mData.get(position).setMsg("<- " + mData.get(position).getUser()[0] + " quited");
            return R.layout.join_leave_recycler_view_row;
        }

        //LAST ELEMENT
        if(position == mData.size() -1)
        {
            return R.layout.user_recycler_view_row;
        }

        //USER MESSAGES
        if(mData.get(position).getIs_user() && mData.get(position).getAction().equals("PRIVMSG")) {
            if (mData.get(position).getUser()[0].equals(mData.get(position + 1).getUser()[0]) && mData.get(position + 1).getAction().equals("PRIVMSG")) {
                return R.layout.message_recycler_view_row;
            } else {
                return R.layout.user_recycler_view_row;
            }
        }

        return R.layout.message_recycler_view_row;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        final RecyclerView.ViewHolder holder;

        //If it is a message
        if(viewType == R.layout.message_recycler_view_row)
        {
            view = mInflater.inflate(R.layout.message_recycler_view_row, parent, false);
            holder = new MessageViewHolder(view);
        }
        //If it is the user
        else if(viewType == R.layout.user_recycler_view_row)
        {
            view = mInflater.inflate(R.layout.user_recycler_view_row, parent, false);
            holder = new UserViewHolder(view);
        }
        else if(viewType == R.layout.join_leave_recycler_view_row)
        {
            view = mInflater.inflate(R.layout.join_leave_recycler_view_row, parent, false);
            holder = new EnterLeaveViewHolder(view);
        }
        //Default case
        else
        {
            view = mInflater.inflate(R.layout.message_recycler_view_row, parent, false);
            holder = new MessageViewHolder(view);
        }

        return holder;
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MessageIRC ap = mData.get(position);
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).messageTextView.setText(ap.getMsg());
            } else if (holder instanceof UserViewHolder) {
                ((UserViewHolder) holder).userTextView.setText(ap.getUser()[0]);
                //Alterar cor de texto de utilizador aqui
                ((UserViewHolder) holder).hourTextView.setText(ap.getHour());
            } else if (holder instanceof EnterLeaveViewHolder) {
                if(mData.get(position).getAction().equals("JOIN"))
                    ((EnterLeaveViewHolder) holder).elTextView.setTextColor(Color.GREEN);
                else
                    ((EnterLeaveViewHolder) holder).elTextView.setTextColor(Color.RED);

                ((EnterLeaveViewHolder) holder).elTextView.setText(ap.getMsg());
            }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageTextView;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.chat_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userTextView;
        TextView hourTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.chat_user);
            hourTextView = itemView.findViewById(R.id.chat_hour);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public class EnterLeaveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView elTextView;

        EnterLeaveViewHolder(View itemView) {
            super(itemView);
            elTextView = itemView.findViewById(R.id.el_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    MessageIRC getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}