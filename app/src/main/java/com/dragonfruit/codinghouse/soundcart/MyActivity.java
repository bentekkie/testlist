package com.dragonfruit.codinghouse.soundcart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.DragNDropSimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;


public class MyActivity extends Activity {


    DragNDropListView list;
    DragNDropSimpleAdapter adapter;
    ArrayList<Map<String, Object>> items;
    Boolean playingAll = true;
    Runnable playAll;
    Thread t;
    Thread ta;
    static boolean gActionCall;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_item:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyActivity.this);
// Setting Dialog Title
                    alertDialog.setTitle("Confirm Delete...");
// Setting Dialog Message
                    alertDialog.setMessage("Are you sure you want to delete?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            removeButton(tpos,tparent);
                        }
                    });
// Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
// Showing Alert Message
                    alertDialog.show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Edit");
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode arg0) {
// TODO Auto-generated method stub
            mActionMode = null;
            tpos = -1;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
// TODO Auto-generated method stub
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testdragndroplist);
        ta = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    for (int x = 0; x < adapter.getCount() && playingAll; x++) {
                        final int y = x;
                        final GridLayout ga = (GridLayout) list.getChildAt(x);
                        if(ga != null) {
                            final sButton button = (sButton)ga.getChildAt(1);
                            if (button.getDeleteFlag()) {
                                button.setDeleteFlag(false);
                                while(button.getDeleteFlag()){}
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Log.e("hey", items.size() + " Items and" + y + " Index");
                                        removeButton(y, (DragNDropListView) button.getParent().getParent());

                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        playAll = new Runnable() {
            @Override
            public void run() {
                for(int x = 0; x < adapter.getCount() && playingAll; x++){
                    final sButton button = (sButton)((GridLayout)list.getChildAt(x)).getChildAt(1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(button.getState() == sButton.Playing) {
                                button.stop();
                                button.setText("Play " + button.getName());
                                button.setState(sButton.Play);
                            }
                            button.setClickable(false);
                            ((GridLayout)button.getParent()).getChildAt(0).setVisibility(View.GONE);
                            ((GridLayout)button.getParent()).getChildAt(0).invalidate();
                        }
                    });
                }
                for(int x = 0; x < adapter.getCount() && playingAll; x++){
                    View temp = list.getChildAt(x);

                    final sButton button = (sButton)((GridLayout)temp).getChildAt(1);
                    if(button != null && button.getText() != "Add") {
                        button.play();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setText("Playing " + button.getName());
                                button.setState(sButton.Playing);
                            }
                        });
                        try {
                            t.sleep(button.getLength());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                runOnUiThread(new Runnable() {
                    @Override

                    public void run() {

                        MenuItem item = menu.findItem(R.id.action_play);
                        item.setChecked(false);
                        item.setIcon(getResources().getDrawable(android.R.drawable.ic_media_play));
                    }
                });
                for(int x = 0; x < adapter.getCount() && playingAll; x++){
                    final sButton button = (sButton)((GridLayout)list.getChildAt(x)).getChildAt(1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(button.getState() == sButton.Playing) {
                                button.stop();
                                button.setText("Play " + button.getName());
                                button.setState(sButton.Play);
                            }
                            button.setClickable(false);
                            if(button.getState() != sButton.Add && button.getState() != 3) {
                                ((GridLayout) button.getParent()).getChildAt(0).setVisibility(View.VISIBLE);
                                ((GridLayout)button.getParent()).getChildAt(0).invalidate();
                            }
                        }
                    });
            }
        }};

        items = new ArrayList<Map<String, Object>>();
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", "item");
            item.put("_id", 1);
            item.put("button", new sButton(this));

            items.add(item);

        adapter = new DragNDropSimpleAdapter(this,
                items,
                R.layout.testitem,
                new String[]{"name"},
                new int[]{R.id.text},
                R.id.handler);
        list = (DragNDropListView)findViewById(R.id.list1);
        list.setDragNDropAdapter(adapter);
        ta.start();
        list.setOnItemDragNDropListener(new DragNDropListView.OnItemDragNDropListener() {
            @Override
            public void onItemDrag(DragNDropListView parent, View view, int position, long id) {



            }

            @Override
            public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
                final DragNDropListView parentf = parent;
                View temp = parent.getChildAt(endPosition);
                sButton Button = (sButton) ((GridLayout)temp).getChildAt(1);
                View tempa = view;
                sButton Buttona = (sButton)((GridLayout)tempa).getChildAt(1);
                CharSequence atext = Buttona.getText();
                int aState = (Buttona.getState());
                String aName = (Buttona.getName());
                Uri aData = (Buttona.getData());
                int aColor = (Buttona.getColor());
                CharSequence text = Button.getText();
                int State = (Button.getState());
                String Name = (Button.getName());
                Uri Data = (Button.getData());
                int Color = (Button.getColor());

                if(State != 0 && aState != 0) {
                        Button.setText(atext);
                        Button.setState(aState);
                        Button.setData(aData);
                        Button.setName(aName);
                        Button.setColor(aColor);
                        Button.setBackgroundColor(aColor);
                        Buttona.setText(text);
                        Buttona.setState(State);
                        Buttona.setData(Data);
                        Buttona.setName(Name);
                        Buttona.setColor(Color);
                        Buttona.setBackgroundColor(Color);
                        Button.invalidate();
                        Buttona.invalidate();
                }
                Log.e("Hello", "Start "+ startPosition + ((sButton)Button).getText());
                Log.e("Hello", "End "+ endPosition +((sButton)Buttona).getText());
            }
        });
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


    }

    public DragNDropListView tparent;
    public int tpos;
    protected Object mActionMode;
    public void removeButton(int position, DragNDropListView parent){
        List<sButton> buttons = new ArrayList<sButton>();
        for(int x = 0; x < parent.getChildCount(); x++){
            sButton Buttona = (sButton) ((GridLayout) parent.getChildAt(x)).getChildAt(1);
            sButton Button = new sButton(this);
            CharSequence atext = Buttona.getText();
            int aState = (Buttona.getState());
            String aName = (Buttona.getName());
            Uri aData = (Buttona.getData());
            int aColor = (Buttona.getColor());
            Button.setText(atext);
            Button.setState(aState);
            Button.setData(aData);
            Button.setName(aName);
            Button.setColor(aColor);
            Button.setBackgroundColor(aColor);
            buttons.add(Button);
        }
        Log.e("hello", buttons.size() + " buttons saved");
        if(((sButton) ((GridLayout) parent.getChildAt(position)).getChildAt(1)).getState() == 2){
            ((sButton) ((GridLayout) parent.getChildAt(position)).getChildAt(1)).stop();
        }
        items.remove(position);
        buttons.remove(position);
        adapter.notifyDataSetChanged();
        for(int x = 0; x < buttons.size(); x++){

                sButton Button = (sButton) ((GridLayout) parent.getChildAt(x)).getChildAt(1);
                sButton Buttona = buttons.get(x);
                CharSequence atext = Buttona.getText();
                int aState = (Buttona.getState());
                String aName = (Buttona.getName());
                Uri aData = (Buttona.getData());
                int aColor = (Buttona.getColor());
                Button.setText(atext);
                Button.setState(aState);
                Button.setData(aData);
                Button.setName(aName);
                Button.setColor(aColor);
            if(aState != 0) {
                Button.setBackgroundColor(aColor);
            }else{
                sButton b = new sButton(this);
                Button.setBackground(b.getBackground());
                ((GridLayout)Button.getParent()).getChildAt(0).setVisibility(View.GONE);
            }
                Button.invalidate();
        }
    }
   sButton temp;
    public void onClick(View v){
        temp = (sButton)v;
        switch(temp.getState()){
            case sButton.Add:
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Sound"), 1);
                break;
            case sButton.Play:

                temp.play();
                temp.setText("Playing " + temp.getName());
                temp.setState(sButton.Playing);
                break;
            case sButton.Playing:
                temp.stop();
                temp.setText("Play " + temp.getName());
                temp.setState(sButton.Play);
                break;

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            Uri selectedAudio = data.getData();
            MediaPlayer mediaPlayer = new MediaPlayer();
            Boolean dataOk = true;
            try {
                mediaPlayer.setDataSource(getApplicationContext(), selectedAudio);
            } catch (IOException e) {
                dataOk = false;
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to open audio", Toast.LENGTH_SHORT);
                toast.show();
            }
            if(selectedAudio != null && dataOk) {


                temp.prepareMediaPlayer(getApplicationContext(),selectedAudio);
                temp.setText("Play " + temp.getName());
                temp.setState(sButton.Play);
                colorDialog(Color.WHITE,temp);
                addButton();
                GridLayout g = (GridLayout)temp.getParent();
                g.getChildAt(0).setVisibility(View.VISIBLE);
                g.getChildAt(0).invalidate();
            }

        }
    }
    public void addButton(){
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("name", "item");
        item.put("_id", 1);
        items.add(item);
        adapter.add();
        adapter.notifyDataSetChanged();
    }
    public void colorDialog(int initialValue, final sButton button){
        int temp = initialValue;
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);
        final int test = 0;
        colorDialog.setAlphaSliderVisible(true);
        colorDialog.setTitle("Pick a Color!");
        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tempint = colorDialog.getColor();
                int[][] states = new int[][] {
                        new int[] { android.R.attr.state_enabled}, // enabled
                        new int[] { android.R.attr.state_pressed},  // pressed
                        new int[] { android.R.attr.state_selected}
                };
                int Blue =  tempint & 255;
                int Green = (tempint >> 8) & 255;
                int Red =   (tempint >> 16) & 255;
                int[] colors = new int[] {
                        Color.WHITE,
                        Color.WHITE,
                        Color.BLUE
                        //Color.rgb((Red*7)/10,(Green*7)/10,(Blue*7)/10)
                };
                Drawable d = new ColorDrawable(tempint);
                RippleDrawable rd = new RippleDrawable(new ColorStateList(states,colors),d,d);
                button.setBackground(rd);
                button.setColor(tempint);
            }
        });
        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        colorDialog.show();
    }

    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflatxe the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_play:
                if(item.isChecked()){
                    item.setIcon(getResources().getDrawable(android.R.drawable.ic_media_play));
                    item.setChecked(false);
                    playingAll = false;
                    t.interrupt();
                    for(int x = 0; x < adapter.getCount(); x++){
                        View a = list.getChildAt(x);
                        sButton button = (sButton)((GridLayout)a).getChildAt(1);
                        if(button != null && button.getState() == 2) {
                            button.stop();
                            button.setText("Play " + button.getName());
                            button.setState(sButton.Play);
                        }
                        button.setClickable(true);
                        if(button.getState() != sButton.Add && button.getState() != 3){
                            ((GridLayout)button.getParent()).getChildAt(0).setVisibility(View.VISIBLE);
                            ((GridLayout)button.getParent()).getChildAt(0).invalidate();
                        }
                        if(button == null) Log.e("Hi","null stuff yo");
                    }
                }else{
                    item.setIcon(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    item.setChecked(true);
                    playingAll = true;
                    t = new Thread(playAll);
                    t.start();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
