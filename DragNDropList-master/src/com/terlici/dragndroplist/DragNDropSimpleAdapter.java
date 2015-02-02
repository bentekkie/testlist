/*
 * Copyright 2012 Terlici Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.terlici.dragndroplist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.SimpleAdapter;


public class DragNDropSimpleAdapter extends SimpleAdapter implements DragNDropAdapter {
	int[] mPosition;
	int mHandler;
    List<? extends Map<String, ?>> data = null;
	public DragNDropSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int handler) {
		super(context, data, resource, from, to);

		mHandler = handler;
        setup(data.size());
        this.data = data;
	}
    public void add(){
        int[] temp = mPosition;
        mPosition = new int[temp.length+1];
        for(int x = 0; x < temp.length; x++) mPosition[x] = temp[x];
        mPosition[mPosition.length-1] = mPosition.length-1;
    }
    public void remove(int a){
        int[] temp = mPosition;
        mPosition = new int[temp.length-1];
        for(int x = 0; x < temp.length ; x++) if(x !=a) mPosition[x] = temp[x];
    }
	private void setup(int size) {
        mPosition = new int[size];
        for(int x = 0; x < size; x++) mPosition[x]=x;

	}
	
	@Override
	public View getDropDownView(int position, View view, ViewGroup group) {
		return super.getDropDownView(mPosition[position], view, group);
	}
	
	@Override
	public Object getItem(int position) {
		return super.getItem(mPosition[position]);
	}
	
	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(mPosition[position]);
	}
	
	@Override
	public long getItemId(int position) {
		return super.getItemId(mPosition[position]);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup group) {

            return super.getView(mPosition[position], view, group);

    }

	
	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(mPosition[position]);
	}

	@Override
	public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
		
	}

	@Override
	public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
        int position = mPosition[startPosition];

        if (startPosition < endPosition){
            for(int x = startPosition; x <endPosition; x++){
                View temp = parent.getChildAt(x);
                View Button = ((GridLayout)temp).getChildAt(1);
                View tempa = parent.getChildAt(x+1);
                View Buttona = ((GridLayout)tempa).getChildAt(1);
                Button = Buttona;
                Button.invalidate();
            }

        }else if (endPosition < startPosition)
            for(int i = startPosition; i > endPosition; --i){
                View temp = parent.getChildAt(i);
                View Button = ((GridLayout)temp).getChildAt(1);
                View tempa = parent.getChildAt(i-1);
                View Buttona = ((GridLayout)tempa).getChildAt(1);
                Button = Buttona;
                Button.invalidate();
            }
        View temp = parent.getChildAt(endPosition);
        View Button = ((GridLayout)temp).getChildAt(1);
        View tempa = view;
        View Buttona = ((GridLayout)tempa).getChildAt(1);
        Button = Buttona;
        Button.invalidate();
	}

	@Override
	public int getDragHandler() {
		return mHandler;
	}
}
