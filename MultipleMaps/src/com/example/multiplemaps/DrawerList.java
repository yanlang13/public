package com.example.multiplemaps;

/*
 * drawerLayout的list內容
 */
public final class DrawerList {
	private DrawerList() {
	}

	public static final DrawerListDetails[] LIST = { new DrawerListDetails(
			"Layout Manage", R.drawable.drawer_layout_setting,
			LayoutManage.class) };
}
