package net.stockovin;


import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.lang.ref.WeakReference;

class BottleSuggestViewHolder extends RecyclerView.ViewHolder   implements View.OnClickListener, View.OnLongClickListener  {
    TextView id, name, domain, year, type, categorie, container, note, userName;
    //TextView qte;
    //ImageView imagButtonMoins, imagButtonPlus;
    ImageView buttonCreationBotBack, buttonCreationBotBackWhite, buttonCreationBotBackChamp, buttonCreationBotBackRose;
    LinearLayout imagButtonbottle;


    BottleSuggestViewHolder(View view) {
        super(view);
        id = view.findViewById(R.id.Bottleid);
        name = view.findViewById(R.id.Bottlename);
        domain = view.findViewById(R.id.Bottledomaine);
        year = view.findViewById(R.id.Bottleyear);
        type = view.findViewById(R.id.Bottletype);
        categorie = view.findViewById(R.id.Bottlecategorie);
        //qte = view.findViewById(R.id.qteBottle);
        note = view.findViewById(R.id.idNote);
        container = view.findViewById(R.id.idBottleContainer);
        /*imagButtonMoins = itemView.findViewById(R.id.imgBotMoins);
        imagButtonPlus = itemView.findViewById(R.id.imgBotPlus);*/
        imagButtonbottle = itemView.findViewById(R.id.bottleViewLigne);
        buttonCreationBotBack = itemView.findViewById(R.id.buttonCreationBotBack);
        buttonCreationBotBackWhite = itemView.findViewById(R.id.buttonCreationBotBackWhite);
        buttonCreationBotBackChamp = itemView.findViewById(R.id.buttonCreationBotBackChamp);
        buttonCreationBotBackRose = itemView.findViewById(R.id.buttonCreationBotBackRose);
        userName = view.findViewById(R.id.TextViewUserSuggest);

    }

    private WeakReference<BottleSuggestAdapter.Listener> callbackWeakRef;

    public void updateWithBottle (BottleSuggest bottle, RequestManager glide, BottleSuggestAdapter.Listener callback){

        /*this.imagButtonMoins.setOnClickListener(this);
        this.imagButtonPlus.setOnClickListener(this);*/
        this.imagButtonbottle.setOnClickListener(this);
        this.imagButtonbottle.setOnLongClickListener(this);
        this.callbackWeakRef = new WeakReference<BottleSuggestAdapter.Listener>(callback);
    }

    @Override
    public void onClick(View view) {

        BottleSuggestAdapter.Listener callback = callbackWeakRef.get();
        if (callback != null)
        {
            /*if(view.getId() == R.id.imgBotPlus){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgBotPlus).startAnimation(alpha);

                l_qte = Integer.valueOf(qte.getText().toString());

                if(l_qte<30) {
                    l_qte = l_qte + 1;
                    qte.setText(Integer.toString(l_qte));

                    callback.onClickIncreaseBottle(Integer.valueOf(id.getText().toString()), l_qte);
                }
            }
            if(view.getId() == R.id.imgBotMoins) {

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgBotMoins).startAnimation(alpha);

                l_qte = Integer.valueOf(qte.getText().toString());

                if(l_qte>0) {
                    l_qte = l_qte - 1;
                    qte.setText(Integer.toString(l_qte));
                    callback.onClickDesincreaseBottle(Integer.valueOf(id.getText().toString()), l_qte);
                }
            }*/
            if(view.getId() == R.id.bottleViewLigne){
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);

                imagButtonbottle.startAnimation(alpha);
                callback.onClickDisplayBottle(getAdapterPosition());
            }
        }
    }

    @Override
    public boolean  onLongClick(View view) {

        BottleSuggestAdapter.Listener callback = callbackWeakRef.get();
        if(view.getId() == R.id.bottleViewLigne)   callback.onLongClickDisplayBottle(view, getAdapterPosition());

        return true;
    }
}
