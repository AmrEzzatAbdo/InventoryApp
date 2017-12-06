package com.example.amrezzat.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.amrezzat.inventoryapp.data.Contract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //widgets
    private ListView ProductesList;
    private View EmptyProductsList;
    //Adapter
    CursorAdabter Adapter;
    //projection product Query
    public final String[] ProjectionQueryProduct = {
            Contract.EntryData.ID
            , Contract.EntryData.COLUMN_NAME
            , Contract.EntryData.COLUMN_QUANTITY
            , Contract.EntryData.COLUMN_PRICE
            , Contract.EntryData.COLUMN_PICTURE
            , Contract.EntryData.COLUMN_DISCRIPTION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FLOAT BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto insert/update activity
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });
        //find widgets view
        ProductesList = (ListView) findViewById(R.id.ProductsList);
        EmptyProductsList = findViewById(R.id.EmptyView);
        ProductesList.setEmptyView(EmptyProductsList);
        Adapter = new CursorAdabter(this, getContentResolver().query(Contract.EntryData.CONTENT_URI, ProjectionQueryProduct, null, null, null));
        ProductesList.setAdapter(Adapter);
        //List item click Action
        ProductesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //get uri for product data
                Uri ProductUri = ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id);
                //get data with intent to put it in it editText position
                intent.setData(ProductUri);
                startActivity(intent);
            }
        });
        //Loader Init
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Delete_All) {
            getContentResolver().delete(Contract.EntryData.CONTENT_URI, null, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.EntryData.CONTENT_URI, ProjectionQueryProduct, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //data ready
        Adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //remove all loader data references
        Adapter.swapCursor(null);
    }
}
