package net.stockovin;


import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

class CategorieAdapter extends RecyclerView.Adapter<CategorieViewHolder> {
    private List<Categorie> CategoriesList;
    private List<Integer> g_nbBotCatList;
    List<LinearLayout> l_linearviewList = new ArrayList<>();
    int position;
    boolean premierPassage = true;

    private CategorieAdapter.Listener callback;
    private RequestManager glide;

    public interface Listener extends FilterAdapter.Listener {
        void onClickDisplayCategorie(int position);
        boolean onLongClickCategorie(View view, int position);
    }

    public CategorieAdapter(List<Categorie> CategoriesList, List<Integer> p_nbBotCatList, int p_position, RequestManager glide, CategorieAdapter.Listener callback) {
        this.CategoriesList = CategoriesList;
        this.g_nbBotCatList = p_nbBotCatList;
        this.glide = glide;
        this.callback = callback;
        this.position = p_position;
    }

    @NonNull
    @Override
    public CategorieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_items, parent, false);

        ImageView imag = itemView.findViewById(R.id.imagesCat);
        imag.setImageResource(R.drawable.icon_region);

        return new CategorieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategorieViewHolder holder, final int position) {
        String troncName;
        Categorie cat = CategoriesList.get(position);
        Integer l_nbBotCat = g_nbBotCatList.get(position);


        troncName = cat.getName();
        if(troncName.length()>13) troncName = troncName.substring(0,13);
        holder.name.setText(troncName);
        holder.tv_nbBottleCategorie.setText(Integer.toString(l_nbBotCat));

        if (holder.name.getText().equals("+")) {
            holder.tv_nbBottleCategorie.setVisibility(View.INVISIBLE);
            holder.imgCat.setVisibility(View.INVISIBLE);
            holder.name.setVisibility(View.INVISIBLE);
            holder.l_linearview.setVisibility(View.INVISIBLE);
            holder.imgCatPlus.setVisibility(View.VISIBLE);
            holder.imgCatBack.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgCatBack.setVisibility(View.INVISIBLE);
            holder.imgCatPlus.setVisibility(View.INVISIBLE);
            holder.l_linearview.setVisibility(View.VISIBLE);
            holder.tv_nbBottleCategorie.setVisibility(View.VISIBLE);
            holder.imgCat.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.VISIBLE);
            /*if((this.position == -1 && holder.name.getText().equals("Tout")) || (this.position == -2 && CategoriesList.size()-2 == position)) holder.l_linearview.setBackgroundResource(R.drawable.border);
            else holder.l_linearview.setBackgroundResource(R.drawable.border_white);*/
            if(premierPassage == true) {
                premierPassage = false;
                if(holder.name.getText().equals("Tout")) holder.l_linearview.setBackgroundResource(R.drawable.border);
            }
        }

        l_linearviewList.add(holder.l_linearview);
        holder.updateWithCategorie(CategoriesList.get(position), l_linearviewList, this.glide, this.callback);
    }


    @Override
    public int getItemCount() {
        return CategoriesList.size();
    }


}