package net.stockovin;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

class UserAdapter extends RecyclerView.Adapter<UserViewHolder> implements Filterable{
    private List<User> userList;
    private List<User> userListFull;
    private List<Integer> friendList;
    Context ctx;

    // 1 - Create interface for callback
    public interface Listener {
        void onClickAddUserFirend(int id);
        void onClickRefuseUserFirend(int id);
        void onClickAckUserFirend(int id);
        void onClickSuppUserFirend(int id);
        void onClickRetUserFirend(int id);
    }

    // 2 - Declaring callback
    private Listener callback;
    private RequestManager glide;

    UserAdapter(List<User> exampleList, List<Integer> friendList, Context ctx, RequestManager glide, Listener callback){
        this.userList = exampleList;
        this.friendList = friendList;
        userListFull = new ArrayList<>(exampleList);
        this.glide = glide;
        this.callback = callback;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_user, parent, false);

        //icon bottle
        ImageView imag = itemView.findViewById(R.id.botListview_images);
        imag.setImageResource(R.drawable.ic_user3);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {

        User user = SharedPrefManager.getInstance(this.ctx).getUser();

        // Remplissage des info du Holder
        String troncName;
        String troncNameCave;
        User currentUser = userList.get(position);
        int currrentfriend = friendList.get(position);

        holder.id.setText(String.valueOf(currentUser.getId()));

        troncName = currentUser.getUsername();
        if(troncName.length()>20) troncName = troncName.substring(0,20);
        holder.name.setText(troncName);

        troncNameCave = currentUser.getcaveName();
        if(troncNameCave.length()>20) troncNameCave = troncNameCave.substring(0,20);
        holder.cave.setText(troncNameCave);

        Log.d("UserAdapter","currrentfriend "+ currrentfriend);
        if(currrentfriend == 1) {
                Log.d("UserAdapter","currentUser.getId()1 "+ currentUser.getId());
                holder.id.setText(String.valueOf(currentUser.getId()));
                holder.bottleViewLigne.setBackgroundResource(android.R.color.darker_gray);

                holder.imgUserRetBack.setVisibility(View.VISIBLE);
                holder.imgUserRet.setVisibility(View.VISIBLE);

                holder.imgUserRefuseBack.setVisibility(View.INVISIBLE);
                holder.imgUserRefuse.setVisibility(View.INVISIBLE);
                holder.imagButtonAddBack.setVisibility(View.INVISIBLE);
                holder.imagButtonAdd.setVisibility(View.INVISIBLE);
                holder.imgUserAckBack.setVisibility(View.INVISIBLE);
                holder.imgUserAck.setVisibility(View.INVISIBLE);
                holder.imgUserSuppBack.setVisibility(View.INVISIBLE);
                holder.imgUserSupp.setVisibility(View.INVISIBLE);
        }

        if(currrentfriend == 3) {
            Log.d("UserAdapter","currentUser.getId()2 "+ currentUser.getId());
            holder.bottleViewLigne.setBackgroundResource(android.R.color.holo_green_dark);

            holder.imgUserAckBack.setVisibility(View.VISIBLE);
            holder.imgUserAck.setVisibility(View.VISIBLE);
            holder.imgUserRefuseBack.setVisibility(View.VISIBLE);
            holder.imgUserRefuse.setVisibility(View.VISIBLE);

            holder.imagButtonAddBack.setVisibility(View.INVISIBLE);
            holder.imagButtonAdd.setVisibility(View.INVISIBLE);
            holder.imgUserSuppBack.setVisibility(View.INVISIBLE);
            holder.imgUserSupp.setVisibility(View.INVISIBLE);
            holder.imgUserRetBack.setVisibility(View.INVISIBLE);
            holder.imgUserRet.setVisibility(View.INVISIBLE);
        }

        if(currrentfriend == 0) {
            holder.bottleViewLigne.setBackgroundResource(android.R.color.background_light);
            holder.imagButtonAddBack.setVisibility(View.VISIBLE);
            holder.imagButtonAdd.setVisibility(View.VISIBLE);

            holder.imgUserRefuseBack.setVisibility(View.INVISIBLE);
            holder.imgUserRefuse.setVisibility(View.INVISIBLE);
            holder.imgUserAckBack.setVisibility(View.INVISIBLE);
            holder.imgUserAck.setVisibility(View.INVISIBLE);
            holder.imgUserSuppBack.setVisibility(View.INVISIBLE);
            holder.imgUserSupp.setVisibility(View.INVISIBLE);
            holder.imgUserRetBack.setVisibility(View.INVISIBLE);
            holder.imgUserRet.setVisibility(View.INVISIBLE);
        }

        if(currrentfriend == 2) {
            holder.bottleViewLigne.setBackgroundResource(android.R.color.holo_green_light);
            holder.imgUserSuppBack.setVisibility(View.VISIBLE);
            holder.imgUserSupp.setVisibility(View.VISIBLE);

            holder.imagButtonAddBack.setVisibility(View.INVISIBLE);
            holder.imagButtonAdd.setVisibility(View.INVISIBLE);
            holder.imgUserRefuseBack.setVisibility(View.INVISIBLE);
            holder.imgUserRefuse.setVisibility(View.INVISIBLE);
            holder.imgUserAckBack.setVisibility(View.INVISIBLE);
            holder.imgUserAck.setVisibility(View.INVISIBLE);
            holder.imgUserRetBack.setVisibility(View.INVISIBLE);
            holder.imgUserRet.setVisibility(View.INVISIBLE);

        }

        holder.updateWithUser(userList.get(position), this.glide, this.callback);

    }

    private LayoutInflater mInflater;

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userListFull);
            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                //boolean si déja dans le filtre
                boolean add = false;
                for (User item : userListFull) {
                    //filtre nom
                    if (item.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                        add =true; //Si cet item correspond a la recherche on met true pour les prochain item
                    }
                    add = false; // on remet le boolean à false pour la prochaine itération de la boucle
                }
            }
            FilterResults results = new FilterResults();

            results.values = filteredList;

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<User> getData() {return userList;
    }

    public void restoreItem(User user, int position) {
        userList.add(position, user);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

}