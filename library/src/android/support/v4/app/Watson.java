package android.support.v4.app;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.Window;

import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/** I'm in ur package. Stealing ur variables. */
public abstract class Watson extends FragmentActivity implements OnCreatePanelMenuListener, OnPreparePanelListener, OnMenuItemSelectedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "Watson";

    /** Fragment interface for menu creation callback. */
    public interface OnCreateOptionsMenuListener {
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
    }
    /** Fragment interface for menu preparation callback. */
    public interface OnPrepareOptionsMenuListener {
        public void onPrepareOptionsMenu(Menu menu);
    }
    /** Fragment interface for menu item selection callback. */
    public interface OnOptionsItemSelectedListener {
        public boolean onOptionsItemSelected(MenuItem item);
    }

    private ArrayList<Fragment> mCreatedMenus;


    ///////////////////////////////////////////////////////////////////////////
    // Sherlock menu handling
    ///////////////////////////////////////////////////////////////////////////

    boolean performCreateOptionsMenu(Fragment f, Menu menu, MenuInflater inflater) {
        boolean show = false;
        if (!f.mHidden) {
            if (f.mHasMenu && f.mMenuVisible) {
                show = true;
                ((OnCreateOptionsMenuListener)f).onCreateOptionsMenu(menu, inflater);
            }
            if (f.mChildFragmentManager != null) {
                show |= dispatchCreateOptionsMenu(f.mChildFragmentManager.mAdded, menu, inflater);
            }
        }
        return show;
    }

    public boolean dispatchCreateOptionsMenu(ArrayList<Fragment> mAdded, Menu menu, MenuInflater inflater) {
        boolean show = false;
        ArrayList<Fragment> newMenus = null;
        if (mAdded != null) {
            for (int i=0; i<mAdded.size(); i++) {
                Fragment f = mAdded.get(i);
                if (f != null) {
                    if (performCreateOptionsMenu(f, menu, inflater)) {
                        show = true;
                        if (newMenus == null) {
                            newMenus = new ArrayList<Fragment>();
                        }
                        newMenus.add(f);
                    }
                }
            }
        }

        if (mCreatedMenus != null) {
            for (int i=0; i<mCreatedMenus.size(); i++) {
                Fragment f = mCreatedMenus.get(i);
                if (newMenus == null || !newMenus.contains(f)) {
                    f.onDestroyOptionsMenu();
                }
            }
        }

        mCreatedMenus = newMenus;

        return show;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean show = onCreateOptionsMenu(menu);
            show |= dispatchCreateOptionsMenu(mFragments.mAdded, menu, getSupportMenuInflater());
            return show;

        }
        return false;
    }

    boolean performPrepareOptionsMenu(Fragment f, Menu menu) {
        boolean show = false;
        if (!f.mHidden) {
            if (f.mHasMenu && f.mMenuVisible) {
                show = true;
                ((OnPrepareOptionsMenuListener)f).onPrepareOptionsMenu(menu);
            }
            if (f.mChildFragmentManager != null) {
                show |= dispatchPrepareOptionsMenu(f.mChildFragmentManager.mAdded, menu);
            }
        }
        return show;
    }

    public boolean dispatchPrepareOptionsMenu(ArrayList<Fragment> mAdded, Menu menu) {
        boolean show = false;
        if (mAdded != null) {
            for (int i=0; i<mAdded.size(); i++) {
                Fragment f = mAdded.get(i);
                if (f != null) {
                    if (performPrepareOptionsMenu(f, menu)) {
                        show = true;
                    }
                }
            }
        }
        return show;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (DEBUG) Log.d(TAG, "[onPreparePanel] featureId: " + featureId + ", view: " + view + " menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
            if (mOptionsMenuInvalidated) {
                mOptionsMenuInvalidated = false;
                menu.clear();
                onCreatePanelMenu(featureId, menu);
            }
            boolean goforit = onPrepareOptionsMenu(menu);
            goforit |= dispatchPrepareOptionsMenu(mFragments.mAdded, menu);
            return goforit && menu.hasVisibleItems();
        }
        return false;
    }

    boolean performOptionsItemSelected(Fragment f, MenuItem item) {
        if (!f.mHidden) {
            if (f.mHasMenu && f.mMenuVisible) {
                if (((OnOptionsItemSelectedListener)f).onOptionsItemSelected(item)) {
                    return true;
                }
            }
            if (f.mChildFragmentManager != null) {
                if (dispatchOptionsItemSelected(f.mChildFragmentManager.mAdded, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dispatchOptionsItemSelected(ArrayList<Fragment> mAdded, MenuItem item) {
        if (mAdded != null) {
            for (int i=0; i<mAdded.size(); i++) {
                Fragment f = mAdded.get(i);
                if (f != null) {
                    if (performOptionsItemSelected(f, item)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (DEBUG) Log.d(TAG, "[onMenuItemSelected] featureId: " + featureId + ", item: " + item);

        return onOptionsItemSelected(item) || featureId == Window.FEATURE_OPTIONS_PANEL && dispatchOptionsItemSelected(mFragments.mAdded, item);
    }

    public abstract boolean onCreateOptionsMenu(Menu menu);

    public abstract boolean onPrepareOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public abstract MenuInflater getSupportMenuInflater();
}
