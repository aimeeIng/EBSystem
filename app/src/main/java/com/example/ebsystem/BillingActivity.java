package com.example.ebsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<BillingModel> billingList;
    ArrayList<BillingModel> searchList;
    ImageView add;

    private RecyclerView.LayoutManager manager;
    private MyAdapter mAdapter;
    private ProgressBar progressBar;
    private SearchView searchView;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_billing);


        recyclerView = findViewById(R.id.service_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        add = findViewById(R.id.addBilling);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillingActivity.this, AddBillingActivity.class);
                startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        billingList = new ArrayList<>();
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList=new ArrayList<>();
                if(query.length()>0){
                    for (int i = 0; i < billingList.size(); i++){
                        if(billingList.get(i).getTitle().toUpperCase().contains(query.toUpperCase())){
                            BillingModel employee = new BillingModel();
                            employee.setTitle(billingList.get(i).getTitle());
                            employee.setDescription(billingList.get(i).getDescription());

                            searchList.add(employee);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BillingActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    MyAdapter dAdapt = new MyAdapter(BillingActivity.this,searchList);
                    recyclerView.setAdapter(dAdapt);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        fetchService();

    }

    private void filterList(String text) {
        List<BillingModel> filteredList = new ArrayList<>();
        for (BillingModel item : billingList){
            if(item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if(filteredList.isEmpty()){
            Toast.makeText(BillingActivity.this, "No data found", Toast.LENGTH_SHORT).show();
        }else{
            mAdapter.setFilteredList(filteredList);
        }
    }

    private void fetchService() {

        String url = "http://192.168.1.69/db_retrieve.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0 ; i < response.length() ; i ++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String overview = jsonObject.getString("description");

                                BillingModel billing = new BillingModel(title ,overview);
                                billingList.add(billing);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MyAdapter adapter = new MyAdapter(BillingActivity.this , billingList);

                            recyclerView.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BillingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

}