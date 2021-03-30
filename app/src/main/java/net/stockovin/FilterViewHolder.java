package net.stockovin;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class FilterViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener {
    TextView NameFilter;
    LinearLayout l_linearview;
    List<LinearLayout> g_linearviewList = new ArrayList<>();


    FilterViewHolder(View view) {
        super(view);
        NameFilter = view.findViewById(R.id.NameFilter);
        l_linearview  = view.findViewById(R.id.linearview);
        g_linearviewList.clear();

    }

    private WeakReference<FilterAdapter.Listener> callbackWeakRef;

    public void updateWithFilter (List<LinearLayout>l_linearviewList, RequestManager glide, FilterAdapter.Listener callback){
        this.l_linearview.setOnClickListener(this);
        this.callbackWeakRef = new WeakReference<FilterAdapter.Listener>(callback);
        g_linearviewList = l_linearviewList;
    }

    @Override
    public void onClick(View view) {

       /* for(LinearLayout ll_linearview : g_linearviewList){
            if(ll_linearview.getVisibility() == View.VISIBLE) {
                ll_linearview.setBackgroundResource(R.drawable.border_white);
            }
        }

        l_linearview.setBackgroundResource(R.drawable.border);*/

        FilterAdapter.Listener callback = callbackWeakRef.get();
        callback.onClickDisplayFilter(view, getAdapterPosition());
    }

    @Override
    public boolean  onLongClick(View view) {

        return true;
    }

}
