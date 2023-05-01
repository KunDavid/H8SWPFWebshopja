package com.example.kerteszetitermekekwebshopja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<ShopingItem> mItemList;
    private ShopingItemAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private NotificationHandler mNotificationHandler;
    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó");
        } else {
            Log.d(LOG_TAG, "Hitelesítettlen felhasználó");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ShopingItemAdapter(this, mItemList);

        mRecyclerView.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        mNotificationHandler = new NotificationHandler(this);
    }

    private void queryData(){
        mItemList.clear();
        mItems.orderBy("carted", Query.Direction.DESCENDING).limit(100).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopingItem item = document.toObject(ShopingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if(mItemList.size() == 0) {
                intializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void queryDataName(){
        mItemList.clear();
        mItems.orderBy("name", Query.Direction.ASCENDING).limit(150).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopingItem item = document.toObject(ShopingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if(mItemList.size() == 0) {
                intializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteI(ShopingItem item) {
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Sikeresen kitörölve: " + item._getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Sikertelen törlés" + item._getId(), Toast.LENGTH_LONG).show();
        });

        mNotificationHandler.send(item.getName() + " termékünk nem releváns számodra");
        queryDataName();

    }


    private void intializeData() {
        String[] itemList = getResources().getStringArray(R.array.shoping_item_name);
        String[] itemInfo = getResources().getStringArray(R.array.shoping_item_description);
        String[] itemPrice = getResources().getStringArray(R.array.shoping_item_price);

        TypedArray itemImageResource = getResources().obtainTypedArray(R.array.shoping_item_source);

        //mItemList.clear();

        for (int i = 0; i < itemList.length; i++){
            mItems.add(new ShopingItem(
                    itemList[i],
                    itemInfo[i],
                    itemPrice[i],
                    itemImageResource.getResourceId(i, 0),
                    0));
                    Log.d(LOG_TAG, " ***Hozzáadás***");

//            mItemList.add(new ShopingItem(
//                    itemList[i],
//                    itemInfo[i],
//                    itemPrice[i],
//                    itemImageResource.getResourceId(i, 0)));
        }

        itemImageResource.recycle();

        //mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                Log.d(LOG_TAG, "Logout Button Pressed");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting_button:
                Log.d(LOG_TAG, "Setting Button Pressed");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart Button Pressed");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View Button Pressed");
                if(viewRow) {
                    changeSpanCount(item, R.drawable.b_view_compact, 1);
                } else {
                    changeSpanCount(item, R.drawable.b_view_headline, 3);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alerMenuItem =menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alerMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alerMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShopingItem item){
        cartItems = (cartItems + 1);
        if(0 < cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);

        mItems.document(item._getId()).update("carted", item.getCarted() + 1)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Sikertelen megváltoztatás" + item._getId(), Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName() + " a kosaradba került");
        queryData();
    }

}