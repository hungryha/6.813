package edu.mit.moneyManager.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import edu.mit.moneyManager.R;
import edu.mit.moneyManager.listUtils.CategoryItemEntry;
import edu.mit.moneyManager.listUtils.EditCategoryListAdapter;
import edu.mit.moneyManager.listUtils.SummaryCategoryListAdapter;

public class ViewEditBudgetActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_budget);
        
      //top actionbar
        Button home = (Button) findViewById(R.id.home_action);
        Button view = (Button) findViewById(R.id.view_action);
        Button expenses = (Button) findViewById(R.id.expense_action);
        
        home.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        
        view.setEnabled(false);
//        view.setOnClickListener(new View.OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ViewSummaryActivity.class);
//                startActivity(intent);
//            }
//        });
        
        expenses.setOnClickListener(new View.OnClickListener() {
       
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExpenseActivity.class);
                startActivity(intent);
            }
        });
        
      //view action bar
        Button summary = (Button) findViewById(R.id.summary_action);
        Button chart = (Button) findViewById(R.id.chart_action);
        Button edit = (Button) findViewById(R.id.edit_action);
        Button share = (Button) findViewById(R.id.share_action);
        
        summary.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewSummaryActivity.class);
                startActivity(intent);
            }
        });
        
        chart.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewChartActivity.class);
                startActivity(intent);
            }
        });
        
        edit.setEnabled(false);
//        edit.setOnClickListener(new View.OnClickListener() {
//       
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ViewEditBudgetActivity.class);
//                startActivity(intent);
//            }
//        });
        
        share.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewShareActivity.class);
                startActivity(intent);
            }
        });
        

        //list adapter
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View footer = inflater.inflate(R.layout.view_edit_list_footer, null, false);
        lv.addFooterView(footer);
        
        //setting list adapter
        //samples data
        List<CategoryItemEntry> sample = new ArrayList<CategoryItemEntry>();
        sample.add(new CategoryItemEntry("Food", 500, 500));
        sample.add(new CategoryItemEntry("Books", 500, 500));
        sample.add(new CategoryItemEntry("Clothing", 900,799));
 
        Log.i("ViewEditBudgetActivity", "before adapter");
        EditCategoryListAdapter adapter = new EditCategoryListAdapter(this, (ArrayList<CategoryItemEntry>) sample);
        setListAdapter(adapter);
        

    }
}
