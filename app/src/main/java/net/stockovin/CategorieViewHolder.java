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

class CategorieViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener {
    TextView name;
    TextView tv_nbBottleCategorie;
    LinearLayout l_linearview;
    RecyclerView recyclerCatView;
    CardView cardView;
    List<LinearLayout> g_linearviewList = new ArrayList<>();
    ImageView imgCat, imgCatPlus, imgCatBack;
    FrameLayout fLayoutCardView;

    CategorieViewHolder(View view) {
        super(view);
        Log.d("CategorieViewHolder","CategorieViewHolder");
        name = view.findViewById(R.id.NameCat);
        tv_nbBottleCategorie = view.findViewById(R.id.nbBottleCategorie);
        recyclerCatView = view.findViewById(R.id.recyclerView);
        l_linearview  = view.findViewById(R.id.linearview);
        cardView = view.findViewById(R.id.card_view);
        imgCat = view.findViewById(R.id.imagesCat);
        imgCatPlus = view.findViewById(R.id.addCat);
        imgCatBack = view.findViewById(R.id.imgCatBack);
        fLayoutCardView  = view.findViewById(R.id.fLayoutCardView);
        g_linearviewList.clear();
    }

    private WeakReference<CategorieAdapter.Listener> callbackWeakRef;

    public void updateWithCategorie (Categorie categorie,  List<LinearLayout>l_linearviewList, RequestManager glide, CategorieAdapter.Listener callback){

        this.l_linearview.setOnClickListener(this);
        this.fLayoutCardView.setOnClickListener(this);
        this.l_linearview.setOnLongClickListener(this);
        this.callbackWeakRef = new WeakReference<CategorieAdapter.Listener>(callback);
        g_linearviewList = l_linearviewList;
    }

    @Override
    public void onClick(View view) {
        for(LinearLayout ll_linearview : g_linearviewList){
            if(ll_linearview.getVisibility() == View.VISIBLE) {
                ll_linearview.setBackgroundResource(R.drawable.border_white);
            }
        }

        l_linearview.setBackgroundResource(R.drawable.border);
        CategorieAdapter.Listener callback = callbackWeakRef.get();
        callback.onClickDisplayCategorie(getAdapterPosition());
    }

    @Override
    public boolean  onLongClick(View view) {

        CategorieAdapter.Listener callback = callbackWeakRef.get();
        callback.onLongClickCategorie(view, getAdapterPosition());
        return true;
    }

}
