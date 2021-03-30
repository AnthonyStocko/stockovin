package net.stockovin;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {
    private List<String> Name_Filter;
    List<LinearLayout> l_linearviewList = new ArrayList<>();
    private FilterAdapter.Listener callback;
    private RequestManager glide;

    public interface Listener {
        void onClickDisplayFilter(View view, int position);
    }

    public FilterAdapter(List<String> FilterList, RequestManager glide, FilterAdapter.Listener callback) {
        this.Name_Filter = FilterList;
        this.glide = glide;
        this.callback = callback;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_filter, parent, false);

        return new FilterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        holder.NameFilter.setText(Name_Filter.get(position));

        holder.updateWithFilter( l_linearviewList, this.glide, this.callback);

        holder.l_linearview.setBackgroundResource(R.drawable.back_filter);

        if(holder.NameFilter.getText().equals("Type")){
            holder.l_linearview.setBackgroundResource(R.drawable.back_filter_white);
        }
        if(holder.NameFilter.getText().equals("Contenant")){
            holder.l_linearview.setBackgroundResource(R.drawable.back_filter_white);
        }
        if(holder.NameFilter.getText().equals("Garde")){
            holder.l_linearview.setBackgroundResource(R.drawable.back_filter_white);
        }
        if(holder.NameFilter.getText().equals("Année")){
            holder.l_linearview.setBackgroundResource(R.drawable.back_filter_white);
        }
        if(holder.NameFilter.getText().equals("Note")){
            holder.l_linearview.setBackgroundResource(R.drawable.back_filter_white);
        }


    }

    private LayoutInflater mInflater;

    @Override
    public int getItemCount() {
        return Name_Filter.size();
    }



}