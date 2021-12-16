package pt.ubi.di.pdm.teste;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ubi.di.pdm.teste.ChatRoom;

public class ChannelsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MessageRecyclerViewAdapter.ItemClickListener{
    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private MessageRecyclerViewAdapter messageAdapter;

    // data is passed into the constructor
    ChannelsRecyclerViewAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).typeTextView.setText(mData.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mData.get(holder.getAdapterPosition()));

                //Update the current chat
                ChatRoom.chatName = mData.get(holder.getAdapterPosition());

                // Update message Adapter
                messageAdapter = new MessageRecyclerViewAdapter(v.getContext(), ChatRoom.channels_messageList.get(ChatRoom.chatName));
                ChatRoom.messageRecyclerView.setAdapter(messageAdapter);
                messageAdapter.setClickListener(ChannelsRecyclerViewAdapter.this);

                ChatRoom.toolbar.setTitle(ChatRoom.chatName);
                ChatRoom.drawerLayout.closeDrawer(ChatRoom.leftDrawer);

                //Empty the user List
                ChatRoom.channelUserList.clear();

                //Update userList
                ChatRoom.cmd.names(ChatRoom.chatName);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemClick(View view, int position) {
        System.out.println("Pixotas");
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.chat_message);
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

}
