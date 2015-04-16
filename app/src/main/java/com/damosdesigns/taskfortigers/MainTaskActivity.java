package com.damosdesigns.taskfortigers;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.damosdesigns.taskfortigers.contentprovider.TaskContentProvider;
import com.damosdesigns.taskfortigers.db.TaskTable;


public class MainTaskActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT =1;
//    private static final int DELETE_ID =  Menu.FIRST + 1;


    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_task);

        ListView lv = getListView();
        lv.setDividerHeight( 2 );
        fillData();
        registerForContextMenu( lv );

    }

    private void fillData(){
        // fields retrieved from the database must include
        // the _id column for the adapter to work

        String[] from = new String[] { TaskTable.COLUMN_SUMMARY };

        int[] to = new int[] { R.id.label };

        getLoaderManager().initLoader( 0, null , this );
        mAdapter = new SimpleCursorAdapter(this, //context
                                           R.layout.task_row,  //row layout
                                           null, //cursor being adapted
                                           from, //data to use
                                           to, //which Views to display that data in
                                           0 ); //for some other shit Dave doesnt remember

        setListAdapter( mAdapter );


    }

    @Override
    public Loader<Cursor> onCreateLoader( int id, Bundle arg ){
        String[] projection = new String[] { TaskTable.COLUMN_ID,TaskTable.COLUMN_SUMMARY };
        CursorLoader cursorLoader =  new CursorLoader( this,
                                                       TaskContentProvider.CONTENT_URI,
                                                       projection,
                                                       null, null, null );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data ) {
        mAdapter.swapCursor( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //data are no longer available, therefore delete the existing cursor
        mAdapter.swapCursor( null );
    }

    public void addTaskButtonHandler( View v ){
        Intent i = new Intent( this, TaskDetailsActivity.class ); //explicit intent
        startActivity( i );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
