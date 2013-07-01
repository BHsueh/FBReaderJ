/*
 * Copyright (C) 2013 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.fbreader.widget;

import android.appwidget.*;
import android.content.*;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.fbreader.book.XMLSerializer;
import org.geometerplus.zlibrary.ui.android.R;

public class FBReaderBookWidget extends AppWidgetProvider {
	private static Book myBook;
	public static final String ACTION_WIDGET_RECEIVER = "ActionWidgetReceiver";
	public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";
	public static final String BOOK_KEY = "fbreader.book";
	
	@Override
	public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteViews.setProgressBar(R.id.progress, 100, 50, false);
		remoteViews.setTextViewText(R.id.percent_progress, "50%");
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		final String action = intent.getAction();
		
	    if (ACTION_WIDGET_RECEIVER.equals(action)) {
			myBook = new XMLSerializer().deserializeBook(intent.getStringExtra("book"));
	    	if (myBook != null) {
				context.startActivity(
						new Intent(ACTION_OPEN_BOOK)
							.putExtra(BOOK_KEY, SerializerUtil.serialize(myBook))
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				);
			}
	    }
	    super.onReceive(context, intent);
	}
}