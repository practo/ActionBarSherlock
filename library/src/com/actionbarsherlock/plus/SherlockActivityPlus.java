package com.actionbarsherlock.plus;

import android.os.Build;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockActivity;

public abstract class SherlockActivityPlus extends SherlockActivity {
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (event.getAction() == KeyEvent.ACTION_UP
					&& keyCode == KeyEvent.KEYCODE_MENU) {
				openOptionsMenu();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
