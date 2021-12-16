package pt.ubi.di.pdm.teste;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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

    // JOIN MESSAGE or PART MESSAGE or QUIT MESSAGE
    switch (mData.get(position).getMessage_type()){
      case "UJ" : mData.get(position).setMsg("<- " + mData.get(position).getUser()[1] + " joined");
        return R.layout.join_leave_recycler_view_row;
      case "UQ" : mData.get(position).setMsg("<- " + mData.get(position).getUser()[1] + " quited");
        return R.layout.join_leave_recycler_view_row;
      case "UP" : mData.get(position).setMsg("<- " + mData.get(position).getUser()[1] + " left");
        return R.layout.join_leave_recycler_view_row;
    }

    //LAST ELEMENT
    if(position == mData.size() -1){
      return R.layout.user_recycler_view_row;
    }

    // CHANNEL MESSAGE or USER MESSAGE
    switch (mData.get(position).getMessage_type()){
      // Check if previous user is equals to the current
      case "C"  :
      case "NS" :
      case "UM" : if (mData.get(position).getUser()[0].equals(mData.get(position + 1).getUser()[0])){
                    if (mData.get(position).getMessage_type().equals("NS")) {
                      if(!mData.get(position).getMsg().contains("Notice: "))
                        mData.get(position).setMsg("Notice: "  + mData.get(position).getMsg());
                      else
                        mData.get(position).setMsg(mData.get(position).getMsg());
                      return R.layout.notices_recycler_view_row;
                    }
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
    else if(viewType == R.layout.notices_recycler_view_row)
    {
      view = mInflater.inflate(R.layout.notices_recycler_view_row, parent, false);
      holder = new NoticeViewHolder(view);
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
      ((MessageViewHolder) holder).messageTextView.setText(Html.fromHtml(ap.getMsg()));
    }

    else if (holder instanceof UserViewHolder) {
      ((UserViewHolder) holder).userTextView.setText(ap.getUser()[0]);
      //Alterar cor de texto de utilizador aqui
      ((UserViewHolder) holder).hourTextView.setText(ap.getHour());
    }

    else if (holder instanceof EnterLeaveViewHolder) {
      if(mData.get(position).getAction().equals("JOIN"))
        ((EnterLeaveViewHolder) holder).elTextView.setTextColor(Color.GREEN);
      else
        ((EnterLeaveViewHolder) holder).elTextView.setTextColor(Color.RED);

      ((EnterLeaveViewHolder) holder).elTextView.setText(ap.getMsg());
    }

    else if (holder instanceof NoticeViewHolder) {
      ((NoticeViewHolder) holder).noticeTextView.setText(ap.getMsg());
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

  public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView noticeTextView;

    NoticeViewHolder(View itemView) {
      super(itemView);
      noticeTextView = itemView.findViewById(R.id.notice_message);
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
