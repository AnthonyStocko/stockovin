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

class UserViewHolder extends RecyclerView.ViewHolder   implements View.OnClickListener, View.OnLongClickListener  {
    TextView id, name, cave, useremail;
    ImageView   imagButtonAddBack,  imgUserRefuseBack, imgUserAckBack, imgUserSuppBack, imgUserRetBack;
    TextView imgUserAck, imagButtonAdd, imgUserRefuse, imgUserSupp, imgUserRet;
    LinearLayout bottleViewLigne;



    UserViewHolder(View view) {
        super(view);
        id = view.findViewById(R.id.userid);
        name = view.findViewById(R.id.userName);
        cave = view.findViewById(R.id.userCave);
        useremail = view.findViewById(R.id.userEmail);

        imagButtonAddBack = itemView.findViewById(R.id.imgBotAddBack);
        imagButtonAdd = itemView.findViewById(R.id.imgBotAdd);

        imgUserRefuseBack = itemView.findViewById(R.id.imgUserRefuseBack);
        imgUserRefuse = itemView.findViewById(R.id.imgUserRefuse);
        bottleViewLigne = itemView.findViewById(R.id.bottleViewLigne);

        imgUserAckBack = itemView.findViewById(R.id.imgUserAckBack);
        imgUserAck = itemView.findViewById(R.id.imgUserAck);

        imgUserSuppBack = itemView.findViewById(R.id.imgUserSuppBack);
        imgUserSupp = itemView.findViewById(R.id.imgUserSupp);

        imgUserRetBack = itemView.findViewById(R.id.imgUserRetBack);
        imgUserRet = itemView.findViewById(R.id.imgUserRet);

    }

    private WeakReference<UserAdapter.Listener> callbackWeakRef;

    public void updateWithUser (User user, RequestManager glide, UserAdapter.Listener callback){

        this.imagButtonAddBack.setOnClickListener(this);
        this.imgUserRefuseBack.setOnClickListener(this);
        this.imgUserAckBack.setOnClickListener(this);
        this.imgUserSuppBack.setOnClickListener(this);
        this.imgUserRetBack.setOnClickListener(this);
        this.callbackWeakRef = new WeakReference<UserAdapter.Listener>(callback);
    }

    @Override
    public void onClick(View view) {
        int l_qte, l_qteCat;
        // 5 - When a click happens, we fire our listener.
        UserAdapter.Listener callback = callbackWeakRef.get();
        if (callback != null)
        {
            if(view.getId() == R.id.imgBotAddBack){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgBotAddBack).startAnimation(alpha);

                callback.onClickAddUserFriend(Integer.valueOf(id.getText().toString()), useremail.getText().toString(), name.getText().toString());

            }

            if(view.getId() == R.id.imgUserRefuseBack){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgUserRefuseBack).startAnimation(alpha);

                callback.onClickRefuseUserFirend(Integer.valueOf(id.getText().toString()));

            }

            if(view.getId() == R.id.imgUserAckBack){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgUserAckBack).startAnimation(alpha);

                callback.onClickAckUserFirend(Integer.valueOf(id.getText().toString()));

            }

            if(view.getId() == R.id.imgUserSuppBack){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgUserSuppBack).startAnimation(alpha);

                callback.onClickSuppUserFirend(Integer.valueOf(id.getText().toString()));

            }

            if(view.getId() == R.id.imgUserRetBack){

                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);
                view.findViewById(R.id.imgUserRetBack).startAnimation(alpha);

                callback.onClickRetUserFirend(Integer.valueOf(id.getText().toString()));

                Log.d("UserViewHolder", "onClick  " );

            }
           /*
            if(view.getId() == R.id.bottleViewLigne){
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(500);

                imagButtonbottle.startAnimation(alpha);
                callback.onClickDisplayBottle(getAdapterPosition());
            }*/
        }
    }

    @Override
    public boolean  onLongClick(View view) {
        int l_qte;
        // 5 - When a click happens, we fire our listener.
        UserAdapter.Listener callback = callbackWeakRef.get();
        //if(view.getId() == R.id.bottleViewLigne)   callback.onLongClickDisplayBottle(view, getAdapterPosition());

        return true;
    }
}
