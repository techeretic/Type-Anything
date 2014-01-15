package com.pshetye.typeanything;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	AlertDialog.Builder alert;
	DatabaseHelper db;
	MyNote note;
	EditText et_Note;
	ListView listview;
	Toast t;
	MenuItem act_Save, act_Edit, act_Delt;
	public static int orig_pos;
	public static long old_pos, note_pos;
	public static enum app_states{VIEW, EDIT, DELETE, NEW, SELECTED};
	public static app_states app_state;
	List<MyNote> mynotes;
	
	public static boolean doDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        db = new DatabaseHelper(this);

		et_Note = (EditText) findViewById(R.id.et_Note);
        listview = (ListView) findViewById(R.id.list);
        app_state = app_states.VIEW;
        
        refreshNotes();
        
        et_Note.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (app_state != app_states.EDIT)
					app_state = app_states.NEW;
				return false;
			}
		});
        
        listview.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	  orig_pos = position;
        		  if (app_state == app_states.NEW && !et_Note.getText().toString().isEmpty()) {
        			  alert = new AlertDialog.Builder(MainActivity.this);
        			  alert.setMessage(R.string.str_new_pop);
      				  alert.setPositiveButton(R.string.str_btn_save, new DialogInterface.OnClickListener() {
      					  public void onClick(DialogInterface dialog, int whichButton) {
      						  note = new MyNote(System.currentTimeMillis(), et_Note.getText().toString());
      						  db.addNote(note);
      						  showToast("Saved");
      						  et_Note.setText("");
      						  refreshNotes();
      		        		  note_pos = mynotes.get(orig_pos).getID();
      					  }
      				  });
      				  alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
      					  public void onClick(DialogInterface dialog, int whichButton) {
      		        		  note_pos = mynotes.get(orig_pos).getID();  
      						  et_Note.setText("");
      						  refreshNotes();    						  
      					  }
      				  });
      				  alert.show();
        		  } else 
        		  if (app_state == app_states.EDIT) {   			
        			  if (et_Note.getText().toString().isEmpty()) {
        				  showToast("Please type something");
	    				  et_Note.setFocusableInTouchMode(true);
        			  } else {
	        			  alert = new AlertDialog.Builder(MainActivity.this);
	        			  alert.setMessage(R.string.str_new_pop);
	      				  alert.setPositiveButton(R.string.str_btn_save, new DialogInterface.OnClickListener() {
	      					  public void onClick(DialogInterface dialog, int whichButton) {
	      						  db.updateNote(new MyNote(note_pos, et_Note.getText().toString()));
	      						  showToast("Updated");
	      						  et_Note.setText("");
	      						  refreshNotes();
	      		        		  note_pos = mynotes.get(orig_pos).getID();
	      					  }
	      				  });
	      				  alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
	      					  public void onClick(DialogInterface dialog, int whichButton) {
	      		        		  note_pos = mynotes.get(orig_pos).getID();  
	      						  et_Note.setText("");
	      						  refreshNotes();    						  
	      					  }
	      				  });
	      				  alert.show(); 
        			  }
        		  } else {
        			  app_state = app_states.SELECTED;
        			  hideSoftKeyboard(MainActivity.this, view);
        			  note_pos = mynotes.get(orig_pos).getID();
        		  }
        		  act_Edit.setVisible(true);
        		  act_Delt.setVisible(true);
        		  et_Note.setFocusableInTouchMode(false);
        	  }
        });
    }
    
    public void onPause() {
    	super.onPause();
		this.finish();
    }
    
    public void onFinish() {
		db.close();
    }
    
    public void onBackPressed(){
    	if (app_state == app_states.SELECTED) {
    		act_Edit.setVisible(false);
  		  	act_Delt.setVisible(false);
  		  	refreshNotes();
  		  	app_state = app_states.VIEW;
  		  	et_Note.setFocusableInTouchMode(true);
    	} else {
    		if (app_state == app_states.EDIT) {
    			if (!et_Note.getText().toString().equals(mynotes.get(orig_pos).getNote()) && 
    					!et_Note.getText().toString().isEmpty()) {
    				alert = new AlertDialog.Builder(this);
    				alert.setMessage(R.string.str_back_pop);
    				alert.setPositiveButton(R.string.str_btn_save, new DialogInterface.OnClickListener() {
    	    			public void onClick(DialogInterface dialog, int whichButton) {
    	    				db.updateNote(new MyNote(note_pos, et_Note.getText().toString()));
    		    			moveTaskToBack(true);
    	    			}
    	    		});
    	    		alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
    	    			public void onClick(DialogInterface dialog, int whichButton) {
    	    				moveTaskToBack(true);
    	    			}
    	    		});
    	    		alert.show();
    				app_state = app_states.VIEW;
    			} else {
        			moveTaskToBack(true);
        		}
    		} else {
    			moveTaskToBack(true);
    		}
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
	    		if (app_state == app_states.SELECTED && !et_Note.getText().toString().isEmpty()) {
	    			showToast("Nothing to save");
	    			refreshNotes();
	    			app_state = app_states.VIEW;
	    			et_Note.setFocusableInTouchMode(true);
	    			break;
	    		}
	    		if (app_state == app_states.EDIT) {	    			
	    			if (et_Note.getText().toString().isEmpty()) {
	    				showToast("Please type something");
	    				et_Note.setFocusableInTouchMode(true);
		    			break;
		    		}
	    			app_state = app_states.VIEW;
	    			if (et_Note.getText().toString().equals(mynotes.get(orig_pos).getNote())) {
	    				showToast("Nothing has changed");
	    				refreshNotes();
	    				et_Note.setText("");
	    				et_Note.setFocusableInTouchMode(true);
	    				break;
	    			}
	    			db.updateNote(new MyNote(note_pos, et_Note.getText().toString()));
	    		} else {
		    		if (et_Note.getText().toString().isEmpty()) {
		    			showToast("Please type something");
		    			et_Note.setFocusableInTouchMode(true);
		    			break;
		    		}
		    		note = new MyNote(System.currentTimeMillis(), et_Note.getText().toString());
		    		db.addNote(note);
	    		}
	    		showToast("Saved");
		    	et_Note.setText("");
	    		refreshNotes();
	    		act_Edit.setVisible(false);
      		  	act_Delt.setVisible(false);
      		  	app_state = app_states.VIEW;	
      		  	et_Note.setFocusableInTouchMode(true);
  			    hideSoftKeyboard(MainActivity.this, et_Note);
	    		break;
	    	case R.id.action_Edit:
				app_state = app_states.EDIT;
      		    et_Note.setText(db.getNote(note_pos).getNote());
	    		refreshNotes();
	    		et_Note.setFocusableInTouchMode(true);
	    		break;
	    	case R.id.action_Delete:
				app_state = app_states.DELETE;
	    		alert = new AlertDialog.Builder(this);
	    		alert.setMessage(R.string.str_delete_pop);
	    		alert.setPositiveButton(R.string.str_btn_del, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				db.deleteNote(new MyNote(note_pos,et_Note.getText().toString()));
	    				refreshNotes();
	    	    		act_Edit.setVisible(false);
	          		  	act_Delt.setVisible(false);
	          		  	app_state = app_states.VIEW;
	    			}
	    		});
	    		alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				refreshNotes();
						app_state = app_states.VIEW;
	    			}
	    		});
	    		alert.show();
	    		break;
    	}
    	return true;
    }
    
    private void refreshNotes() {
    	List<String> list = db.getAllStringNotes();
    	
    	mynotes = db.getAllNotes();
    	
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
    
    public static void hideSoftKeyboard (Activity activity, View view) {
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
}
