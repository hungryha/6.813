package edu.mit.moneyManager.view;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.moneyManager.R;

/**
 * This is the home activity.
 * 
 * First time users create a budget or view budgets shared with them.
 * 
 * Returning users can view their budget and view budgets shared with them.
 */
public class HomeActivity extends Activity {
    public static boolean NEW = true;
    public static String VIEWINGOTHER = "";
    private TabHost tabhost;
    private TextView welcome;
    private Button create;
    private Button viewBtn;
    private Button expenseBtn;
    private Button shareBtn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        VIEWINGOTHER="";
        tabhost = ((TabActivity)getParent()).getTabHost();

        welcome = (TextView) findViewById(R.id.welcome);
        create = (Button) findViewById(R.id.create_budget);
        viewBtn = (Button) findViewById(R.id.view_budget);
        expenseBtn = (Button) findViewById(R.id.enter_expense);
        shareBtn = (Button) findViewById(R.id.share_budget);
        if (!NEW){
            welcome.setText("You have $225 remaining this month");
            create.setVisibility(View.GONE);
            expenseBtn.setVisibility(View.VISIBLE);
            viewBtn.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.VISIBLE);

            
        }
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tabhost.setCurrentTab(1);
               //TODO: get the summary view in view tab
//                tabhost.getTabWidget().getChildAt(1).getTab
                tabhost = ((TabActivity)getParent()).getTabHost();
                tabhost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
                tabhost.setCurrentTab(1);
            }
        });
        expenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabhost.setCurrentTab(2);
            }
        });
        ExpandableListView budgetsView = (ExpandableListView) findViewById(R.id.sharedBudgets);
        final ExpandableListAdapter adapter = new BudgetExpandableListAdapter();
        budgetsView.setAdapter(adapter);
        
        budgetsView.setOnChildClickListener(new OnChildClickListener(){
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                VIEWINGOTHER = (String) adapter.getChild(groupPosition, childPosition);
                TextView username = (TextView) getParent().findViewById(R.id.username);

                username.setText(VIEWINGOTHER);
                TabHost tabhost = ((TabActivity)getParent()).getTabHost();
                tabhost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
                tabhost.setCurrentTab(1);
                return true;
            }
            
        });
        
        
        create.setOnClickListener(new View.OnClickListener(){
            
            @Override
            public void onClick(View v){
                if (NEW){
//                Intent intent = new Intent(v.getContext(), ViewEditBudgetActivity.class);
//                startActivity(intent);
//                Intent intent = new Intent(v.getContext(), MMTabWidget.class);
//                intent.putExtra("tabIndex", 1);
//                startActivity(intent);
                	TabHost tabhost = ((TabActivity)getParent()).getTabHost();
                    tabhost.getTabWidget().getChildTabViewAt(2).setEnabled(true);
                    tabhost.setCurrentTab(2);
                }
                else{
//                    Intent intent = new Intent(v.getContext(), ExpenseActivity.class);
//                    Intent intent = new Intent(v.getContext(), MMTabWidget.class);
//                    intent.putExtra("tabIndex", 2);
//                    startActivity(intent);
                    TabHost tabhost = ((TabActivity)getParent()).getTabHost();
                    tabhost.getTabWidget().getChildTabViewAt(2).setEnabled(true);
                    tabhost.setCurrentTab(2);
                }
            }
            
        });  
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("HomeActivity", "Calling onResume()");

        if (!NEW){
            welcome.setText("You have $225 remaining this month");
            create.setVisibility(View.GONE);
            expenseBtn.setVisibility(View.VISIBLE);
            viewBtn.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.VISIBLE);
        }

    }
    class BudgetExpandableListAdapter extends BaseExpandableListAdapter{
        private String[] groups = {"Shared Budgets"};
        private String[][] children = {{ "LukeSkywalker's Budget",
            "PrincessLeia's Budget", "R2D2's Budget"}};
        
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = this.getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return children[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        
        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(HomeActivity.this);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(75, 0, 0, 0);
            return textView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            TextView textView = this.getGenericView();
            textView.setText("Budgets Shared With You:");

            return textView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition,
                int childPosition) {
            return true;
        }
        
    }
}
