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

package org.geometerplus.android.fbreader.network.action;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import org.geometerplus.fbreader.network.NetworkTree;
import org.geometerplus.fbreader.network.NetworkLibrary;
import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.fbreader.network.AllCatalogsActivity;
import org.geometerplus.android.fbreader.FBReader;

public class AllCatalogsAction extends RootAction {
	public AllCatalogsAction(Activity activity) {
		super(activity, ActionCode.SHOW_CATALOGS_FILTER, "allCatalogs", true);
	}

	@Override
	public void run(NetworkTree tree) {
		final NetworkLibrary library = NetworkLibrary.Instance();
		final ArrayList<String> ids = new ArrayList<String>(library.activeIds());
		final ArrayList<String> inactiveIds = new ArrayList<String>(library.linkIds());
		inactiveIds.removeAll(ids);

		OrientationUtil.startActivityForResult(
			myActivity,
			new Intent(myActivity.getApplicationContext(), AllCatalogsActivity.class)
			  .putStringArrayListExtra(FBReader.CATALOGS_ID_LIST, ids)
			  .putStringArrayListExtra(AllCatalogsActivity.INACTIVE_IDS_LIST, inactiveIds),
			  FBReader.REQUEST_ALL_CATALOGS
		);
	}
}
