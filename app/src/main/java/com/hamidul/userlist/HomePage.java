package com.hamidul.userlist;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomePage extends Fragment {
    RecyclerView recyclerView;
    LinearLayout progressBar;
    ArrayList<HashMap<String,String>> arrayList;
    ArrayList<HashMap<String,String>> updateArrayList;
    HashMap<String,String> hashMap;
    public static MyAdapter myAdapter;
    SearchView searchView;
    FloatingActionButton fab;
    ProgressBar onApp;
    Toast toast;
    boolean mNotMach;
    boolean eNotMach;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_page, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);
        onApp = view.findViewById(R.id.onApp);
        fab = view.findViewById(R.id.fab);

        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.data_insert);

                EditText edName = dialog.findViewById(R.id.edName);
                EditText edMobile = dialog.findViewById(R.id.edMobile);
                EditText edEmail = dialog.findViewById(R.id.edEmail);
                Button button = dialog.findViewById(R.id.button);
                TextInputLayout mobileLayout = dialog.findViewById(R.id.mobileLayout);
                TextInputLayout emailLayout = dialog.findViewById(R.id.emailLayout);
                button.setText("Save");

                TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String name = edName.getText().toString();
                        String mobile = edMobile.getText().toString();
                        String email = edEmail.getText().toString();

                        for (HashMap<String,String> item : arrayList){

                            if (item.get("mobile").equals(mobile)){
                                mobileLayout.setError("This Number Already Used");
                                mNotMach = false;
                                break;
                            }else {
                                mobileLayout.setError(null);
                                mobileLayout.setErrorEnabled(false);
                                mNotMach = true;
                            }


                        }//for loop end

                        for (HashMap<String,String> item : arrayList){

                            if (item.get("email").equals(email)){
                                emailLayout.setError("This Email Already Used");
                                eNotMach = false;
                                break;
                            } else {
                                emailLayout.setError(null);
                                emailLayout.setErrorEnabled(false);
                                eNotMach=true;
                            }

                            if (mobile.length()<11 && mobile.length()>0){
                                mobileLayout.setHelperText("Input 11 digit Number");
                            }else {
                                mobileLayout.setHelperText(null);
                                mobileLayout.setHelperTextEnabled(false);
                            }


                        }//for loop end

                        button.setEnabled(!name.isEmpty() && mobile.length()==11 && mobile.startsWith("01") && !email.isEmpty() && mNotMach && eNotMach);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (edMobile.getText().toString().length()>0){
                            if (!s.toString().startsWith("0")){
                                s.delete(0,1);
                            }
                        }
                        if (edMobile.getText().toString().length()>1) {
                            if (!s.toString().startsWith("01")){
                                s.delete(1,2);
                            }
                        }

                    }
                };

                edName.addTextChangedListener(watcher);
                edMobile.addTextChangedListener(watcher);
                edEmail.addTextChangedListener(watcher);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();

                        String name = edName.getText().toString();
                        String mobile = edMobile.getText().toString();
                        String email = edEmail.getText().toString();

                        String url = "https://smhamidulcodding.000webhostapp.com/practice/insert_data.php?n="+name+"&m="+mobile+"&e="+email;

                        onApp.setVisibility(View.VISIBLE);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadData();
                                progressBar.setVisibility(View.GONE);
                                onApp.setVisibility(View.GONE);

                                new AlertDialog.Builder(getActivity())
                                        .setTitle(name)
                                        .setMessage(response)
                                        .show();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onApp.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    }

                });

                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.getWindow().setGravity(Gravity.BOTTOM);



            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.filter(newText);
                return true;
            }
        });

        return view;
    }

    public void loadData (){



        arrayList = new ArrayList<>();

        String url = "https://smhamidulcodding.000webhostapp.com/practice/view.php";

        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                progressBar.setVisibility(View.GONE);

                arrayList = new ArrayList<>();

                for (int x=0; x<response.length(); x++){

                    try {
                        JSONObject jsonObject = response.getJSONObject(x);

                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String mobile = jsonObject.getString("mobile");
                        String email = jsonObject.getString("email");

                        hashMap = new HashMap<>();
                        hashMap.put("id",id);
                        hashMap.put("name",name);
                        hashMap.put("mobile",mobile);
                        hashMap.put("email",email);
                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                myAdapter = new MyAdapter(arrayList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{

        ArrayList<HashMap<String,String>> itemList;
        ArrayList<HashMap<String,String>> filterList;
        String queryText = "";

        public MyAdapter (ArrayList<HashMap<String,String>> itemList){
            this.itemList = itemList;
            this.filterList = new ArrayList<>(itemList);
        }

        @NonNull
        @Override
        public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.item,parent,false);

            return new myViewHolder(view);
        }

        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView tvName,tvMobile,tvEmail;
            LinearLayout selectItem;
            public myViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvMobile = itemView.findViewById(R.id.tvMobile);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                selectItem = itemView.findViewById(R.id.selectItem);

            }
        }

        @Override
        public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

            hashMap = filterList.get(position);

            String id = hashMap.get("id");
            String name = hashMap.get("name");
            String mobile = hashMap.get("mobile");
            String email = hashMap.get("email");

            if (queryText!=null && !queryText.isEmpty()){

                int starPos = name.toLowerCase().indexOf(queryText.toLowerCase());
                int endPos = starPos+queryText.length();

                if (starPos!= -1){

//                    Spannable spannable = new SpannableString(name);
//                    ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {}},new int[] {Color.BLUE});
//                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD,-1,colorStateList,null);
//                    spannable.setSpan(textAppearanceSpan,starPos,endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Spannable spannable = new SpannableString(name);
                    ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {}},new int[] {Color.parseColor("#009688")});
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.NORMAL,-1,colorStateList,null);
                    spannable.setSpan(textAppearanceSpan,starPos,endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.tvName.setText(spannable);


                }else {
                    holder.tvName.setText(name);
                }

                int numStart = mobile.toLowerCase().indexOf(queryText.toLowerCase());
                int numEnd = numStart+queryText.length();

                if (numStart!= -1){

                    Spannable spannable = new SpannableString(mobile);
                    ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{}},new int[]{Color.RED});
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null,Typeface.NORMAL,-1,colorStateList,null);
                    spannable.setSpan(textAppearanceSpan,numStart,numEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.tvMobile.setText(spannable);

                }else {
                    holder.tvMobile.setText(mobile);
                }

                int eStart = email.toLowerCase().indexOf(queryText.toLowerCase());
                int eEnd = eStart+queryText.length();

                if (eStart!= -1){

                    Spannable spannable = new SpannableString(email);
                    ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{}},new int[]{Color.YELLOW});
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null,Typeface.NORMAL,-1,colorStateList,null);
                    spannable.setSpan(textAppearanceSpan,eStart,eEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.tvEmail.setText(spannable);

                }else {
                    holder.tvEmail.setText(email);
                }



            }else {
                holder.tvName.setText(name);
                holder.tvMobile.setText(mobile);
                holder.tvEmail.setText(email);
            }






//            holder.tvName.setText(name);
//            holder.tvMobile.setText(mobile);
//            holder.tvEmail.setText(email);

            holder.selectItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    updateArrayList = new ArrayList<>(arrayList);

                    for (HashMap<String,String> up : arrayList){

                        if (up.get("id").equals(id)){
                            updateArrayList.remove(up);
                        }

                    }

                    new AlertDialog.Builder(getContext())
                            .setTitle("ID : "+id)
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final Dialog update = new Dialog(getActivity());
                                    update.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    update.setContentView(R.layout.data_insert);

                                    EditText edName = update.findViewById(R.id.edName);
                                    EditText edMobile = update.findViewById(R.id.edMobile);
                                    EditText edEmail = update.findViewById(R.id.edEmail);
                                    Button button = update.findViewById(R.id.button);
                                    TextInputLayout mobileLayout = update.findViewById(R.id.mobileLayout);
                                    TextInputLayout emailLayout = update.findViewById(R.id.emailLayout);

                                    button.setText("Update");
                                    edName.setText(name);
                                    edMobile.setText(mobile);
                                    edEmail.setText(email);


                                    TextWatcher watcher = new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            String name = edName.getText().toString();
                                            String mobile = edMobile.getText().toString();
                                            String email = edEmail.getText().toString();

                                            for (HashMap<String,String> item : updateArrayList){
                                                if (item.get("mobile").equals(mobile)){
                                                    mobileLayout.setError("This Number Already Used");
                                                    mNotMach = false;
                                                    break;
                                                }else {
                                                    mobileLayout.setError(null);
                                                    mobileLayout.setErrorEnabled(false);
                                                    mNotMach = true;
                                                }
                                            }

                                            for (HashMap<String,String> item : updateArrayList){
                                                if (item.get("email").equals(email)){
                                                    emailLayout.setError("This Email Already Used");
                                                    eNotMach = false;
                                                    break;
                                                }else {
                                                    emailLayout.setError(null);
                                                    emailLayout.setErrorEnabled(false);
                                                    eNotMach = true;
                                                }
                                            }

                                            button.setEnabled(!name.isEmpty() && !mobile.isEmpty() && !email.isEmpty() && mNotMach && eNotMach);

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    };

                                    edName.addTextChangedListener(watcher);
                                    edMobile.addTextChangedListener(watcher);
                                    edEmail.addTextChangedListener(watcher);

                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            update.cancel();

                                            String name = edName.getText().toString();
                                            String mobile = edMobile.getText().toString();
                                            String email = edEmail.getText().toString();

                                            String url = "https://smhamidulcodding.000webhostapp.com/practice/update.php?n="+name+"&m="+mobile+"&e="+email+"&id="+id;

                                            onApp.setVisibility(View.VISIBLE);

                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    loadData();
                                                    progressBar.setVisibility(View.GONE);
                                                    onApp.setVisibility(View.GONE);

                                                    toastMessage("Updated");
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    onApp.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                            requestQueue.add(stringRequest);
                                        }

                                    });

                                    update.show();
                                    update.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    update.getWindow().setWindowAnimations(R.style.DialogAnimation);
                                    update.getWindow().setGravity(Gravity.BOTTOM);

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Are you sure !")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    onApp.setVisibility(View.VISIBLE);
                                                    String url = "https://smhamidulcodding.000webhostapp.com/practice/delete.php?id="+id;

                                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            loadData();
                                                            progressBar.setVisibility(View.GONE);
                                                            onApp.setVisibility(View.GONE);
                                                            toastMessage("ID : "+id+" Deleted");
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            onApp.setVisibility(View.GONE);
                                                            toastMessage(error.toString());
                                                        }
                                                    });

                                                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                    requestQueue.add(stringRequest);


                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            })
                            .show();

                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }

        public void filter (String query){
            query = query.toLowerCase();
            filterList.clear();

            if (query.isEmpty()){
                filterList.addAll(itemList);
                queryText = "";
                fab.setVisibility(View.VISIBLE);
            }else {

                fab.setVisibility(View.GONE);
                queryText = query.toString();

                for (HashMap<String,String> item : itemList){
                    if ( item.get("name").toLowerCase().contains(query) || item.get("mobile").toLowerCase().contains(query) || item.get("email").toLowerCase().contains(query) ){
                        filterList.add(item);
                    }
                }
            }

            notifyDataSetChanged();

        }



    }

    private void toastMessage (String message){
        if (toast!=null) toast.cancel();
        toast = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
        toast.show();
    }

}