package net.stockovin;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class BottleActivity extends AppCompatActivity {

    public final static String CATEGORIE = "com.example.intent.example.CATEGORIE";
    public final static String CATEGORIENAME = "com.example.intent.example.CATEGORIENAME";
    public final static String MODIFY = "com.example.intent.example.BOTTLE";
    ArrayList ArrayListBottle;
    BottleAdapter myAdapter = null;
    int idcategorie;
    String categoriename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_bottle2);

        Intent i = getIntent();
        idcategorie = i.getIntExtra(CategorieActivity.CATEGORIE, 0);
        categoriename = i.getStringExtra(CategorieActivity.CATEGORIENAME);
        ArrayListBottle = new ArrayList<String>();

        findViewById(R.id.buttonCreationBot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                finish();
                bottleCreationActivity.putExtra(CATEGORIE, idcategorie );
                bottleCreationActivity.putExtra(CATEGORIENAME, categoriename );
                startActivity(bottleCreationActivity);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View vue, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, vue, menuInfo);
        MenuInflater inflater = getMenuInflater();

        // On récupère des informations sur l'item par apport à l'adaptateur
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // On récupère le fichier concerné par le menu contextuel
        // Categorie cat = mAdapter.getItem(info.position);
        // On a deux menus, s'il s'agit d'un répertoire ou d'un fichier
        inflater.inflate(R.menu.context_dir, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case R.id.deleteItem:
                // On récupère la position de l'item concerné
                break;

            case R.id.modifyItem:
                Intent bottleCreationActivity = new Intent(getApplicationContext(), BottleCreationActivity.class);
                finish();
                bottleCreationActivity.putExtra(CATEGORIE, idcategorie );
                bottleCreationActivity.putExtra(CATEGORIENAME, categoriename );
                bottleCreationActivity.putExtra(MODIFY, true);
                startActivity(bottleCreationActivity);
                break;
        }
        return super.onContextItemSelected(item);
    }

}