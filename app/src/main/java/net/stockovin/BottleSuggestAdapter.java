package net.stockovin;


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

class BottleSuggestAdapter extends RecyclerView.Adapter<BottleSuggestViewHolder> implements Filterable{
    private List<BottleSuggest> BottleList;
    private List<BottleSuggest> BottleListFull;
    private List<String> nameCategorie;
    private List<String> nameUser;

    // 1 - Create interface for callback
    public interface Listener {
        void onClickDisplayBottle(int position);
        boolean onLongClickDisplayBottle(View view, int position);
    }

    // 2 - Declaring callback
    private Listener callback;
    private RequestManager glide;

    BottleSuggestAdapter(List<BottleSuggest> exampleList, List<String> p_nameCategorie, List<String> p_nameUser, RequestManager glide, Listener callback){
        this.BottleList = exampleList;
        this.nameCategorie = p_nameCategorie;
        this.nameUser = p_nameUser;

        BottleListFull = new ArrayList<>(exampleList);
        //nameCategorieFull = new ArrayList<>(p_nameCategorie);

        this.glide = glide;
        this.callback = callback;
    }

    @NonNull
    @Override
    public BottleSuggestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_bottle_suggest, parent, false);

        //icon bottle
        ImageView imag = itemView.findViewById(R.id.botSuggestListview_images);
        imag.setImageResource(R.drawable.icon_bottle);

        return new BottleSuggestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BottleSuggestViewHolder holder, int position) {

        // Remplissage des info du Holder
        String troncName;
        BottleSuggest currentBottle = BottleList.get(position);
        String currrentNameCategorie = nameCategorie.get(position);
        String currrentUserName = nameUser.get(position);

        holder.id.setText(String.valueOf(currentBottle.getId()));

        troncName = currentBottle.getName();
        if(troncName.length()>20) troncName = troncName.substring(0,20);
        holder.name.setText(troncName);

        holder.domain.setText(currentBottle.getDomain());
        holder.year.setText(String.valueOf(currentBottle.getYear()));
        holder.type.setText(currentBottle.getType());
        holder.categorie.setText(currrentNameCategorie);
        holder.note.setText(String.valueOf(currentBottle.getNote()));
        holder.container.setText(currentBottle.getContainer());
        //holder.qte.setText(String.valueOf(currentBottle.getQte()));
        holder.userName.setText(currrentUserName);

        holder.updateWithBottle(BottleList.get(position), this.glide, this.callback);

        if(holder.type.getText().equals("Champagne"))
        {
            holder.buttonCreationBotBackChamp.setVisibility(View.VISIBLE);
            holder.buttonCreationBotBack.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }
        if(holder.type.getText().equals("Blanc"))
        {
            holder.buttonCreationBotBackWhite.setVisibility(View.VISIBLE);
            holder.buttonCreationBotBack.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }
        if(holder.type.getText().equals("Rosé"))
        {
            holder.buttonCreationBotBackRose.setVisibility(View.VISIBLE);
            holder.buttonCreationBotBack.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
        }
        if(holder.type.getText().equals("Rouge"))
        {
            holder.buttonCreationBotBack.setVisibility(View.VISIBLE);
            holder.buttonCreationBotBackChamp.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackWhite.setVisibility(View.INVISIBLE);
            holder.buttonCreationBotBackRose.setVisibility(View.INVISIBLE);
        }

    }

    private LayoutInflater mInflater;

    @Override
    public Filter getFilter() {
        return bottleFilter;
    }

    private Filter bottleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BottleSuggest> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(BottleListFull);
            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                //boolean si déja dans le filtre
                boolean add = false;
                for (BottleSuggest item : BottleListFull) {
                    //filtre nom
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                        add =true; //Si cet item correspond a la recherche on met true pour les prochain item
                    }
                    //filtre domaine
                    if (item.getDomain().toLowerCase().contains(filterPattern)) {
                        if(add == false){ //On ajoute seulement s'il n'ait pas déja
                            filteredList.add(item);
                            add =true;
                        }
                    }
                    //filtre type
                    if (item.getType().toLowerCase().contains(filterPattern)) {
                        if(add == false){
                            filteredList.add(item);
                            add =true;
                        }
                    }
                    //filtre année
                    if ((Integer.toString(item.getYear()).toLowerCase().contains(filterPattern))) {
                        if(add == false){
                            filteredList.add(item);
                            add =true;
                        }
                    }
                    //filtre commentaire
                    if (item.getComment().toLowerCase().contains(filterPattern)) {
                        if(add == false){
                            filteredList.add(item);
                            add =true;
                        }
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
            BottleList.clear();
            BottleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public Filter getFilterType() {
        return bottleFilterType;
    }

    private Filter bottleFilterType = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BottleSuggest> filteredList1 = new ArrayList<>();
            List<BottleSuggest> filteredList2 = new ArrayList<>();
            List<BottleSuggest> filteredList3 = new ArrayList<>();
            List<BottleSuggest> filteredList4 = new ArrayList<>();
            List<BottleSuggest> filteredList5 = new ArrayList<>();

            String tmpconstraint = (String) constraint;
            String tmpType =  tmpconstraint.substring(tmpconstraint.indexOf("|1|")+3, tmpconstraint.indexOf("|2|"));
            String tmpContainer =  tmpconstraint.substring(tmpconstraint.indexOf("|2|")+3, tmpconstraint.indexOf("|3|"));
            String tmpGarde =  tmpconstraint.substring(tmpconstraint.indexOf("|3|")+3, tmpconstraint.indexOf("|4|"));
            String tmpYear =  tmpconstraint.substring(tmpconstraint.indexOf("|4|")+3, tmpconstraint.indexOf("|@5|"));
            String tmpNote =  tmpconstraint.substring(tmpconstraint.indexOf("|@5|")+4, tmpconstraint.indexOf("|@6|"));
            Log.d("BottleAdapter", "tmpType "+ tmpType + " tmpContainer " + tmpContainer + " tmpGarde "+ tmpGarde + " tmpYear "+ tmpYear + " tmpNote "+ tmpNote);

            if (tmpconstraint == null || tmpconstraint.length() == 0 || (tmpType.equals("null") && tmpContainer.equals("null") && tmpGarde.equals("null") && tmpYear.equals("null") && tmpNote.equals("null"))) {
                filteredList1.addAll(BottleListFull);
                filteredList2.addAll(BottleListFull);
                filteredList3.addAll(BottleListFull);
                filteredList4.addAll(BottleListFull);
                filteredList5.addAll(BottleListFull);
            } else {

                String filterPattern1 = tmpType.toLowerCase().trim();
                String filterPattern2 = tmpContainer.toLowerCase().trim();
                String filterPattern3 = tmpGarde.toLowerCase().trim();
                String filterPattern4 = tmpYear.toLowerCase().trim();
                String filterPattern5 = tmpNote.toLowerCase().trim();

                // Type
                //---------------------------------------------------
                for (BottleSuggest item : BottleListFull) {
                    //filtre type
                    if (item.getType().toLowerCase().contains(filterPattern1)) {
                        filteredList1.add(item);
                        //continue;
                    }
                }

                if(filterPattern1.equals("null")) {
                    if (filteredList1.size() == 0) filteredList1 = BottleListFull;
                }


                // Container
                //---------------------------------------------------
                for (BottleSuggest item : filteredList1) {
                    if (item.getContainer().toLowerCase().contains(filterPattern2)) {
                        filteredList2.add(item);
                        //continue;
                    }
                }

                if(filterPattern2.equals("null")) {
                    if (filteredList2.size() == 0){
                        filteredList2 = filteredList1;
                    }
                }

                // GARDE
                //---------------------------------------------------
                for (BottleSuggest item : filteredList2) {
                    if (item.getGarde().toLowerCase().contains(filterPattern3)) {
                        filteredList3.add(item);
                        //continue;
                    }
                }

                if(filterPattern3.equals("null")) {
                    if (filteredList3.size() == 0){
                        filteredList3 = filteredList2;
                    }
                }

                // Year
                //---------------------------------------------------
                for (BottleSuggest item : filteredList3) {
                    if ((Integer.toString(item.getYear()).toLowerCase().contains(filterPattern4))) {
                        filteredList4.add(item);
                        //continue;
                    }
                }

                if(filterPattern4.equals("null")) {
                    if (filteredList4.size() == 0){
                        filteredList4 = filteredList3;
                    }
                }

                // Note
                //---------------------------------------------------
                for (BottleSuggest item : filteredList4) {
                    if ((Integer.toString(item.getNote()).toLowerCase().contains(filterPattern5))) {
                        filteredList5.add(item);
                        //continue;
                    }
                }

                if(filterPattern5.equals("null")) {
                    if (filteredList5.size() == 0){
                        filteredList5 = filteredList4;
                    }
                }
            }

            Log.d("BottleAdapter"," filteredList1 "+ filteredList1 + " filteredList2 "+ filteredList2 + " filteredList3 "+ filteredList3 + " filteredList4 " + filteredList4 + " filteredList5 " + filteredList5);
            FilterResults results = new FilterResults();
            results.values = filteredList5;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            BottleList.clear();
            BottleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilterContainer() {
        return bottleFilterContainer;
    }

    private Filter bottleFilterContainer = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BottleSuggest> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                Log.d("BottleAdapter","getFilterType" + BottleList);
                filteredList.addAll(BottleListFull);
            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                //boolean si déja dans le filtre
                boolean add = false;
                for (BottleSuggest item : BottleList) {

                    //filtre type
                    if (item.getContainer().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                        add =true;

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
            BottleList.clear();
            BottleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return BottleList.size();
    }

    public List<BottleSuggest> getData() {return BottleList;
    }

    public void restoreItem(BottleSuggest bot, int position) {
        BottleList.add(position, bot);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        BottleList.remove(position);
        notifyItemRemoved(position);
    }

}