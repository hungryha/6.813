package edu.mit.moneyManager.view;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import edu.mit.moneyManager.R;

/**
 * This is the home activity.
 * 
 * First time users create a budget or view budgets shared with them.
 * 
 * Returning users can view their budget and view budgets shared with them.
 */
public class MMTabWidget extends TabActivity {
    public static final boolean NEW = true;
    private TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, HomeActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("home").setIndicator("Home",
        		res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ViewContainer.class);
        spec = tabHost.newTabSpec("view").setIndicator("View",
        		res.getDrawable(R.drawable.ic_tab_stats))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ExpenseActivity.class);
        spec = tabHost.newTabSpec("expenses").setIndicator("Expenses",
                          res.getDrawable(R.drawable.ic_tab_expenses))
                      .setContent(intent);
        tabHost.addTab(spec);

            tabHost.setCurrentTab(0);
        
        int iCnt = tabHost.getTabWidget().getChildCount();
        for(int i=0; i<iCnt; i++)
          tabHost.getTabWidget().getChildAt(i).getLayoutParams().height *= .8;
        
    }
    

}
