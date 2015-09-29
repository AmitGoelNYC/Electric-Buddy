package com.example.tyson.electricbuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SecondActivity extends AppCompatActivity{

    private String[] navtTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navtTitles = getResources().getStringArray(R.array.nav_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        int[] myDrawables = {R.drawable.ic_search_white_36dp,
                R.drawable.ic_favorite_white_36dp,
                R.drawable.ic_settings_white_36dp,
                R.drawable.ic_info_white_36dp};

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[navtTitles.length];
        for(int i = 0;i< navtTitles.length; i++){
            drawerItem[i] = new ObjectDrawerItem(myDrawables[i], navtTitles[i]);
        }
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(
                this, drawerItem);
        mDrawerList.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        selectItem(0);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new MainFragment();
                break;
            case 1:
                fragment = new FavoritesFragment();
                break;
            case 2:
                fragment = new PrefsFragment();
                break;
            case 3:
                fragment = new AboutFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            mDrawerList.setItemChecked(position, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

}
