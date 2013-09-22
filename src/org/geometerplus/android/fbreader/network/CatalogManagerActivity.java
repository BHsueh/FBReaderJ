/*
 * Copyright (C) 2010-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.network;

import java.util.*;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.fbreader.network.*;
import org.geometerplus.android.fbreader.covers.CoverManager;
import org.geometerplus.android.fbreader.FBReader;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
	
public class CatalogManagerActivity extends ListActivity {
	private ArrayList<Item> myAllItems = new ArrayList<Item>();
	private ArrayList<Item> mySelectedItems = new ArrayList<Item>();
	ArrayList<String> myIds = new ArrayList<String>();
	ArrayList<String> myInactiveIds = new ArrayList<String>();
	Intent returnIntent = new Intent();

	public final static String INACTIVE_IDS_LIST = "org.geometerplus.android.fbreader.network.INACTIVE_IDS_LIST";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.network_library_filter);

		final Intent intent = getIntent();
		myIds = intent.getStringArrayListExtra(FBReader.CATALOGS_ID_LIST);
		myInactiveIds = intent.getStringArrayListExtra(INACTIVE_IDS_LIST);
	}

	@Override
	protected void onStart() {
		super.onStart();

		myAllItems.clear();
		if (myIds.size() > 0) {
			myAllItems.add(new SectionItem("enabled"));
			final List<CatalogItem> cItems = new ArrayList<CatalogItem>();
			for (String id : myIds) {
				cItems.add(new CatalogItem(id, true, NetworkLibrary.Instance().getCatalogTreeByUrlAll(id)));
			}
			myAllItems.addAll(cItems);
			mySelectedItems.addAll(cItems);
		}

		if (myInactiveIds.size() > 0) {
			myAllItems.add(new SectionItem("disabled"));
			final List<CatalogItem> cItems = new ArrayList<CatalogItem>();
			for (String id : myInactiveIds) {
				cItems.add(new CatalogItem(id, false, NetworkLibrary.Instance().getCatalogTreeByUrlAll(id)));
			}
			myAllItems.addAll(cItems);
		}

                DragSortListView list = getListView();
		list.setAdapter(new CatalogsListAdapter());
                list.setDropListener(onDrop);
                list.setRemoveListener(onRemove);
		//setListAdapter(new CatalogsListAdapter());
	}

	private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    if(to <= 0){
                        to = 1;
                    }
                    DragSortListView list = getListView();
		    if(list.getInputAdapter() instanceof CatalogsListAdapter){
                    	CatalogsListAdapter myAdapter = (CatalogsListAdapter)list.getInputAdapter();
                    	Item item = myAdapter.getItem(from);
			if(item instanceof CatalogItem){
				myAdapter.remove(item);
                    		myAdapter.insert(item, to);
                    		myAdapter.reCheckAll(item, to);
                    		list.moveCheckState(from, to);
				setResultIds(item, to);
			}
		    }
                }
            }
        };

    private RemoveListener onRemove =
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                DragSortListView list = getListView();
		if(list.getInputAdapter() instanceof CatalogsListAdapter){
                	CatalogsListAdapter myAdapter = (CatalogsListAdapter)list.getInputAdapter();
                	Item item = myAdapter.getItem(which);
			if(item instanceof CatalogItem){
                		myAdapter.remove(item);
                		list.removeCheckState(which);
			}
		}
            }
        };


        @Override
        public DragSortListView getListView() {
               return (DragSortListView) super.getListView();
        }

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private static interface Item {
	}

	private static class SectionItem implements Item {
		private final String Title;

		public SectionItem(String key) {
			Title = NetworkLibrary.resource().getResource("manageCatalogs").getResource(key).getValue();
		}
	}

	private static class CatalogItem implements Item, Comparable<CatalogItem> {
		private final String Id;
		private final NetworkTree Tree;
		private boolean IsChecked;

		public CatalogItem(String id, boolean checked, NetworkTree tree) {
			Id = id;
			IsChecked = checked;
			Tree = tree;
		}

		public String getTitle() {
			return Tree.getLink().getTitle();
		}

		public String getTitleLower() {
			return getTitle().toLowerCase(Locale.getDefault());
		}

		@Override
		public int compareTo(CatalogItem another) {
			return getTitleLower().compareTo(another.getTitleLower());
		}
	}

	private void setResultIds(Item item, int index){
		if(item != null && item instanceof CatalogItem){
			CatalogItem catalogItem = (CatalogItem)item;
			if(catalogItem.IsChecked){
				int insertIndex = index <= 0 ? -1 : (index-1);
				if(mySelectedItems.contains(catalogItem)){
					mySelectedItems.remove(catalogItem);
				}
				if(insertIndex >= 0){
					mySelectedItems.add(insertIndex, catalogItem);
				}else{
					mySelectedItems.add(catalogItem);
				}
			}else{
				mySelectedItems.remove(catalogItem);
			}
			final ArrayList<String> ids = new ArrayList<String>();
			for (Item selectedItem : mySelectedItems) {
				if (selectedItem instanceof CatalogItem) {
					final CatalogItem ci = (CatalogItem)selectedItem;
					if (ci.IsChecked) {
						ids.add(ci.Id);
					}
				}
			}
			returnIntent.putStringArrayListExtra(FBReader.CATALOGS_ID_LIST, ids);
			setResult(RESULT_OK, returnIntent);
		}
	}

	private class CatalogsListAdapter extends ArrayAdapter<Item> {
		private CoverManager myCoverManager;

		public CatalogsListAdapter() {
			super(CatalogManagerActivity.this, R.layout.checkbox_item, myAllItems);
		}
                
                public void reCheckAll(Item item, int index){
                       boolean flag = false;
                       for (int i=0; i < getCount(); i++){
                           Item it = getItem(i);
                           if(it instanceof SectionItem){
                               if(i>0){
                                  if(index > i){
                                     flag = false;
                                  }else{
                                     flag = true;
                                  }
                                  break;
                               }else{
					flag = true;
			       }
                           }
                       }
                        
                       if(item != null && item instanceof CatalogItem){
				final CatalogItem catalogItem = (CatalogItem)item;
				System.out.println(catalogItem.IsChecked+" == "+flag);
                          	if(catalogItem.IsChecked != flag){
					catalogItem.IsChecked = flag;
                           	}
                       } 
                }

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			final Item item = getItem(position);

			final View view;
			if (convertView != null && item.getClass().equals(convertView.getTag())) {
				view = convertView;
			} else {
				view = getLayoutInflater().inflate(
					item instanceof SectionItem
						? R.layout.checkbox_section : R.layout.checkbox_item,
					null
				);
				view.setTag(item.getClass());
			}

			if (item instanceof SectionItem) {
				((TextView)view.findViewById(R.id.title)).setText(((SectionItem)item).Title);
			} else /* if (item instanceof CatalogItem) */ {
				final CatalogItem catalogItem = (CatalogItem)item;

				if (myCoverManager == null) {
					view.measure(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					final int coverHeight = view.getMeasuredHeight();
					myCoverManager = new CoverManager(CatalogManagerActivity.this, coverHeight * 15 / 22, coverHeight);
					view.requestLayout();
				}

				final INetworkLink link = catalogItem.Tree.getLink();
				((TextView)view.findViewById(R.id.title)).setText(link.getTitle());
				((TextView)view.findViewById(R.id.subtitle)).setText(link.getSummary());

				final ImageView coverView = (ImageView)view.findViewById(R.id.icon);
				if (!myCoverManager.trySetCoverImage(coverView, catalogItem.Tree)) {
					coverView.setImageResource(R.drawable.ic_list_library_books);
				}

				final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_item);
				checkBox.setChecked(catalogItem.IsChecked);
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						catalogItem.IsChecked = checkBox.isChecked();
						setResultIds(catalogItem, 0);
					}
				});
			}
			return view;
		}
	}
}
