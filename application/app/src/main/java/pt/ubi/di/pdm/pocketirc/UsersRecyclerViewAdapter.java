package pt.ubi.di.pdm.pocketirc;

//imports
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListenerUser mClickListener;

    // data is passed into the constructor
    UsersRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = mInflater.inflate(R.layout.message_recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,int position) {
        ((UsersRecyclerViewAdapter.ViewHolder)holder).typeTextView.setText(mData.get(position));
        ((UsersRecyclerViewAdapter.ViewHolder) holder).typeTextView.setTextColor(toColor(mData.get(position)));
        ((UsersRecyclerViewAdapter.ViewHolder)holder).typeTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView typeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.chat_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClickUser(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListenerUser itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListenerUser {
        void onItemClickUser(View view, int position);
    }

    private int toColor(String arg) {
        int sum = 0;
        for(char ch : arg.toCharArray())
            sum +=ch;

        double rDouble = Double.parseDouble("0." + String.valueOf(Math.sin(sum + 1)).substring(6)) * 256;
        double gDouble = Double.parseDouble("0." + String.valueOf(Math.sin(sum + 2)).substring(6)) * 256;
        double bDouble = Double.parseDouble("0." + String.valueOf(Math.sin(sum + 3)).substring(6)) * 256;
        int r =(int)rDouble;
        int g =(int)gDouble;
        int b =(int)bDouble;

        return Color.rgb(r,g,b);
    }
}
