package com.pshetye.typeanything;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	AlertDialog.Builder alert;
	DatabaseHelper db;
	MyNote note;
	List<MyNote> notes;
	EditText et_Note;
	ListView listview;
	TextView title, description;
	Toast t;
	MenuItem act_Save, act_Edit, act_Delt, act_Share;
	public static int orig_pos;
	public static long old_pos, note_pos;
	public static enum app_states{VIEW, EDIT, DELETE, NEW, SELECTED};
	public static app_states app_state;
	List<MyNote> mynotes;
	MyListAdapter myladapter;
	
	public static boolean doDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        myladapter = null;
        
        db = new DatabaseHelper(this);

		et_Note = (EditText) findViewById(R.id.et_Note);
        listview = (ListView) findViewById(R.id.list);
        app_state = app_states.VIEW;
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
            	et_Note.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
        
        refreshNotes();
        
        et_Note.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (app_state == app_states.SELECTED) {
					changeMenuBtnVisibility(false);
					setSelectionTransparent();
		  		  	et_Note.setFocusableInTouchMode(true);
		  		  	showSoftKeyboard(MainActivity.this, v);
				}
				if (app_state != app_states.EDIT)
					app_state = app_states.NEW;
				return false;
			}
		});
        
        listview.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		  setSelectionTransparent();
		  		  	
        		  title = (TextView) view.findViewById(R.id.theNote);
 				  description = (TextView) view.findViewById(R.id.theDate); 
 				  
 				  title.setBackgroundColor(Color.GRAY);
 				  description.setBackgroundColor(Color.GRAY);
 				  
	        	  orig_pos = position;
        		  if (app_state == app_states.NEW && !et_Note.getText().toString().isEmpty()) {
        			  alert = new AlertDialog.Builder(MainActivity.this);
        			  alert.setMessage(R.string.str_new_pop);
      				  alert.setPositiveButton(R.string.str_btn_save, new DialogInterface.OnClickListener() {
      					  public void onClick(DialogInterface dialog, int whichButton) {
      						  note = new MyNote((int)System.currentTimeMillis(), et_Note.getText().toString());
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
        				  if (et_Note.getText().toString().equals(db.getNote(note_pos).getNote())) {
        					  note_pos = mynotes.get(orig_pos).getID();
        					  et_Note.setText("");
    	    				  app_state = app_states.SELECTED;
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
		      						  et_Note.setText(mynotes.get(orig_pos).getNote());
		      						  refreshNotes();    						  
		      					  }
		      				  });
		      				  alert.show(); 
        				  }
        			  }
        		  } else {
        			  app_state = app_states.SELECTED;
        			  hideSoftKeyboard(MainActivity.this, view);
        			  note_pos = mynotes.get(orig_pos).getID();
        		  }
        		  changeMenuBtnVisibility(true);
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
    		changeMenuBtnVisibility(false);
  		  	refreshNotes();
  		  	app_state = app_states.VIEW;
  		  	et_Note.setFocusableInTouchMode(true);
  		  	setSelectionTransparent();
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
    	    		setSelectionTransparent();
    				app_state = app_states.VIEW;
    			} else {
        			moveTaskToBack(true);
        		}
    		} else {
    			setSelectionTransparent();
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
        act_Share = menu.findItem(R.id.action_Share);
        act_Save = menu.findItem(R.id.action_save);
        
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
		    		showToast(String.valueOf(System.currentTimeMillis()));
		    		note = new MyNote((int)System.currentTimeMillis(), et_Note.getText().toString());
		    		db.addNote(note);
	    		}
	    		showToast("Saved");
		    	et_Note.setText("");
	    		refreshNotes();
	    		changeMenuBtnVisibility(false);
      		  	app_state = app_states.VIEW;	
      		  	et_Note.setFocusableInTouchMode(true);
  			    hideSoftKeyboard(MainActivity.this, et_Note);
	    		break;
	    	case R.id.action_Edit:
	    		changeMenuBtnVisibility(false);
      		    et_Note.setText(db.getNote(note_pos).getNote());
	    		refreshNotes();
	    		et_Note.setFocusableInTouchMode(true);
	    		app_state = app_states.EDIT;
	    		break;
	    	case R.id.action_Delete:
	    		et_Note.setFocusableInTouchMode(true);
				app_state = app_states.DELETE;
	    		alert = new AlertDialog.Builder(this);
	    		alert.setMessage(R.string.str_delete_pop);
	    		alert.setPositiveButton(R.string.str_btn_del, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				db.deleteNote(new MyNote(note_pos,et_Note.getText().toString()));
	    				refreshNotes();
	    				changeMenuBtnVisibility(false);
	          		  	app_state = app_states.VIEW;
	    			}
	    		});
	    		alert.setNegativeButton(R.string.str_btn_canc, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				refreshNotes();
						app_state = app_states.VIEW;
						changeMenuBtnVisibility(false);
	    			}
	    		});
	    		alert.show();
	    		break;
	    	case R.id.action_Share:
	    		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	    	    sharingIntent.setType("text/plain");
	    	    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "From - Type Anything");
	    	    sharingIntent.putExtra(Intent.EXTRA_TEXT, db.getNote(note_pos).getNote());
	    	    startActivity(Intent.createChooser(sharingIntent, "Choose option"));
	    		break;
    	}
    	return true;
    }
    
    private void refreshNotes() {
    	setSelectionTransparent();
    	
    	List<MyNote> notes = db.getAllNotes();
    	    	
    	mynotes = db.getAllNotes();
    	
    	if (myladapter != null) {
    		myladapter.clear();
    		myladapter.addAll(notes);
    	} else {
    		myladapter = new MyListAdapter(this,
	    	        android.R.layout.simple_list_item_activated_1, notes);
    	}
    	listview.setAdapter(myladapter);
    	listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    
    private void showToast(String ToastText){
    	if (t != null)
			t.cancel();
		t = Toast.makeText(this, ToastText, Toast.LENGTH_SHORT);
		t.show();
    }
    
    private static void hideSoftKeyboard (Activity activity, View view) {
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
    
    private static void showSoftKeyboard (Activity activity, View view) {
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.showSoftInput(view, 0);
	}
    
    private void changeMenuBtnVisibility(boolean visibility) {
		  act_Edit.setVisible(visibility);
		  act_Delt.setVisible(visibility);
		  act_Share.setVisible(visibility);
		  act_Save.setVisible(!visibility);
    }
    
    private void setSelectionTransparent() {
	  	if (title != null)
	  		title.setBackgroundColor(Color.TRANSPARENT);
	  	if (description != null)
	  		description.setBackgroundColor(Color.TRANSPARENT);
	  	title = null;
	  	description = null;    	
    }
    
    private class MyListAdapter extends ArrayAdapter<MyNote> {
		 private List<MyNote> notes;
		 
		 public MyListAdapter(Context context, int textViewResourceId, List<MyNote> notes) {
			 super(context, textViewResourceId, notes);
			 this.notes = notes;
		 }

		 public View getView(int position, View convertView, ViewGroup parent) {
			 View v = convertView;

			 if (v == null) {
				 LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 v = vi.inflate(R.layout.listview, null);
			 }

			 MyNote note = notes.get(position);

			 if (note != null) {             
				 TextView title = (TextView) v.findViewById(R.id.theNote);
				 TextView description = (TextView) v.findViewById(R.id.theDate);
				 if (title != null) {
					 title.setText(note.getNote());
				 }
				 if (description != null) {
					 description.setText(String.valueOf(note.getDate()));
				 }
				 title = null;
				 description = null;
			 }

			 return v;
		 }
	}
}
