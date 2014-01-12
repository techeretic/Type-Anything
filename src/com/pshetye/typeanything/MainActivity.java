package com.pshetye.typeanything;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	DatabaseHelper db;
	MyNote note;
	EditText et_Note;
	ListView listview;
	Toast t;
	public static int notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        db = new DatabaseHelper(this);
        
        listview = (ListView) findViewById(R.id.listview);
        
        refreshNotes();
        
        listview.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	    Toast.makeText(getApplicationContext(),
        	      "Click ListItem Number " + position, Toast.LENGTH_LONG)
        	      .show();
        	  }
        	}); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.action_save:
	    		et_Note = (EditText) findViewById(R.id.et_Note);
	    		if (et_Note.getText().toString().isEmpty()) {
	    			if (t != null)
	    				t.cancel();
	    			t = Toast.makeText(this, "Please type something", Toast.LENGTH_SHORT);
	    			t.show();
	    			break;
	    		}
	    		notes = db.getNotesCount();
	    		note = new MyNote(notes+1, et_Note.getText().toString());
	    		db.addNote(note);
	    		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	    		et_Note.setText("");
	    		refreshNotes();
	    		break;
    	}
    	return true;
    }
    
    public void refreshNotes() {
    	List<String> list = db.getAllStringNotes();
        
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
    }
    
    private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
