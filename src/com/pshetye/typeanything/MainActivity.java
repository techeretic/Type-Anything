package com.pshetye.typeanything;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	MenuItem act_Save, act_Edit, act_Delt;
	public static int notes;	
	public static int note_pos;
	
	public static boolean EDIT_MODE;
	public static boolean doDelete;
	public static boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        db = new DatabaseHelper(this);

		et_Note = (EditText) findViewById(R.id.et_Note);
        listview = (ListView) findViewById(R.id.list);
        
        refreshNotes();
        
        listview.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		  note_pos = position;
        		  act_Edit.setVisible(true);
        		  act_Delt.setVisible(true);
        		  selected = true;
        	  }
        });
    }
    
    public void onBackPressed(){
    	if (selected) {
    		act_Edit.setVisible(false);
  		  	act_Delt.setVisible(false);
  		  	refreshNotes();
  		  	selected = false;
    	} else {
    		moveTaskToBack(true);
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        act_Edit = menu.findItem(R.id.action_Edit);
        act_Delt = menu.findItem(R.id.action_Delete);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.action_save:
	    		if (EDIT_MODE) {	    			
	    			if (et_Note.getText().toString().isEmpty()) {
	    				showToast("Please type something");
		    			break;
		    		}
	    			db.updateNote(new MyNote(note_pos + 1, et_Note.getText().toString()));
	    			EDIT_MODE = false;
	    		} else {
		    		if (et_Note.getText().toString().isEmpty()) {
		    			showToast("Please type something");
		    			break;
		    		}
		    		notes = db.getNotesCount();
		    		note = new MyNote(notes+1, et_Note.getText().toString());
		    		db.addNote(note);
	    		}
	    		showToast("Saved");
		    	et_Note.setText("");
	    		refreshNotes();
	    		act_Edit.setVisible(false);
      		  	act_Delt.setVisible(false);
	    		break;
	    	case R.id.action_Edit:
      		    EDIT_MODE = true;
      		    et_Note.setText(db.getNote(note_pos + 1).getNote());
	    		refreshNotes();
	    		break;
	    	case R.id.action_Delete:
	    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    		alert.setMessage(R.string.str_delete_pop);
	    		alert.setPositiveButton(R.string.str_btn_del, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				db.deleteNote(new MyNote(note_pos+1,et_Note.getText().toString()));
	    				refreshNotes();
	    	    		act_Edit.setVisible(false);
	          		  	act_Delt.setVisible(false);
	    			}
	    		});
	    		alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				refreshNotes();
	    			}
	    		});
	    		alert.show();
	    		break;
    	}
    	return true;
    }
    
    private void refreshNotes() {
    	List<String> list = db.getAllStringNotes();
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	        android.R.layout.simple_list_item_activated_1, list);
    	listview.setAdapter(adapter);
    	listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    
    private void showToast(String ToastText){
    	if (t != null)
			t.cancel();
		t = Toast.makeText(this, ToastText, Toast.LENGTH_SHORT);
		t.show();
    }
}
