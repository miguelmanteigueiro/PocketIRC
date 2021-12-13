package pt.ubi.di.pdm.teste;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Message> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MessageRecyclerViewAdapter(Context context, List<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //go through the entire list and gives each row its layout
    @Override
    public int getItemViewType(int position) {
        if(position == mData.size() -1)
        {
            return R.layout.user_recycler_view_row;
        }

        if(mData.get(position).getUser()[0].equals(mData.get(position + 1).getUser()[0])){
            return R.layout.message_recycler_view_row;
        }else{
            return R.layout.user_recycler_view_row;
        }
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
        Message ap = mData.get(position);
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).messageTextView.setText(ap.getMsg().toString());
            } else if (holder instanceof UserViewHolder) {
                ((UserViewHolder) holder).userTextView.setText(ap.getUser()[0].toString());
                ((UserViewHolder) holder).hourTextView.setText(ap.getHour());
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

    // convenience method for getting data at click position
    Message getItem(int id) {
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
