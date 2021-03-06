package edu.mit.moneyManager.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
    public static final String TAG = "DATABASE ADAPTER";
    public static final String UNCATEGORIZED = "uncategorized";
    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "data";
    private static final String EXPENSES_TABLE = "expenses";
    private static final String CATEGORIES_TABLE = "categories";

    private static final String ROW_ID = "_id";

    // categories columns
    private static final String CATEGORY_ROW_ID = "_id";
    private static final String NAME_COLUMN = "name";
    private static final String TOTAL_COLUMN = "total";
    private static final String REMAINING_COLUMN = "remaining";

    // expenses columns
    private static final String DATE_COLUMN = "date";
    private static final String AMOUNT_COLUMN = "amount";
    private static final String CATEGORY_COLUMN = "category";

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CATEGORIES_TABLE + " ("
                    + CATEGORY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NAME_COLUMN + " TEXT NOT NULL, " + TOTAL_COLUMN
                    + " REAL NOT NULL, " + REMAINING_COLUMN
                    + " REAL NOT NULL);");

            // db.execSQL("CREATE TABLE " + EXPENSES_TABLE
            // + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE_COLUMN
            // + " TEXT NOT NULL, " + AMOUNT_COLUMN + " REAL NOT NULL, "
            // + CATEGORY_COLUMN + " TEXT NOT NULL, " + "FOREIGN KEY("
            // + CATEGORY_COLUMN + ") REFERENCES " + CATEGORIES_TABLE
            // + "(" + NAME_COLUMN + "));");

            db.execSQL("CREATE TABLE " + EXPENSES_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE_COLUMN
                    + " TEXT NOT NULL, " + AMOUNT_COLUMN + " REAL NOT NULL, "
                    + CATEGORY_COLUMN + " TEXT NOT NULL);");

        }

        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            /*
             * if (!db.isReadOnly()) { // Enable foreign key constraints
             * db.execSQL("PRAGMA foreign_keys=ON;"); }
             */
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
            onCreate(db);

        }

    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx
     *            the Context within which to work
     */
    public DatabaseAdapter(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public DatabaseAdapter open() throws SQLException {
        mDbHelper = new DbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    // category methods
    /**
     * 
     * @param category
     * @return rowId of category added or -1 if failed to insert
     */
    public long addCategory(Category category) {
        Log.i(TAG, "adding category");
        ContentValues initialValues = new ContentValues();

        initialValues.put(NAME_COLUMN, category.getName());
        initialValues.put(TOTAL_COLUMN, category.getTotal());
        initialValues.put(REMAINING_COLUMN, category.getRemaining());

        return mDb.insert(CATEGORIES_TABLE, null, initialValues);
    }

    /**
     * updates category name, total and remaining
     * @param name
     * @param newname
     * @param newamt
     * @return
     */
    public boolean updateCategory(String name, String newname, double newamt) {
        Category category = this.getCategory(name);
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(NAME_COLUMN, newname);
        updatedValues.put(TOTAL_COLUMN, newamt);
        updatedValues.put(REMAINING_COLUMN, newamt - category.getTotal() + category.getRemaining());
//        Cursor cursor = mDb.query(CATEGORIES_TABLE,
//                new String[] { CATEGORY_ROW_ID }, NAME_COLUMN + "=\'" + name
//                        + "\'", null, null, null, null);
        boolean success = false;
        if (mDb.update(CATEGORIES_TABLE, updatedValues, NAME_COLUMN + "=\'"
                + name + "\'", null) == 1) {
            success = true;
            ContentValues newCat = new ContentValues();
            newCat.put(CATEGORY_COLUMN, newname);
            
            int num = mDb.update(EXPENSES_TABLE, newCat, CATEGORY_COLUMN + "=\'" + name + "\'", null);
            Log.i(TAG, "updated " + num + " expense rows");
        }
        return success;
    }

    /**
     * 
     * @param name
     * @return true if category exists, false if not
     */
    public boolean categoryExist(String name) {
        Cursor cursor = mDb.query(CATEGORIES_TABLE, new String[] {
                CATEGORY_ROW_ID, NAME_COLUMN }, NAME_COLUMN + "=\'" + name
                + "\'", null, null, null, null);
        return (cursor.getCount() > 0);
    }

    /**
     * 
     * @param name
     * @return Category, null if doesn't exist
     */
    public Category getCategory(String name) {
        Cursor cursor = mDb.query(CATEGORIES_TABLE, new String[] {
                CATEGORY_ROW_ID, NAME_COLUMN, TOTAL_COLUMN, REMAINING_COLUMN },
                NAME_COLUMN + "=\'" + name + "\'", null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            double total = Double.parseDouble(cursor.getString(2));
            double remaining = Double.parseDouble(cursor.getString(3));
            return new Category(name, total, remaining);
        }
        return null;
    }

    // TODO
    /**
     * Query for all expenses in <name> category. Update all expenses from the
     * <name> category to UNCATEGORIZED, then removes category
     * 
     * @param name
     * @return True if deleted, False otherwise
     */
    public boolean removeCategory(String name) {
        Cursor cursor = mDb.query(EXPENSES_TABLE, new String[] { ROW_ID },
                CATEGORY_COLUMN + "=\'" + name + "\'", null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(CATEGORY_COLUMN, UNCATEGORIZED);
            mDb.update(EXPENSES_TABLE, values, ROW_ID + "=" + id, null);

        }
        return mDb.delete(CATEGORIES_TABLE, NAME_COLUMN + "=\'" + name + "\'",
                null) > 0;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<Category>();
        Cursor cursor = mDb.query(CATEGORIES_TABLE, new String[] {
                CATEGORY_ROW_ID, NAME_COLUMN, TOTAL_COLUMN, REMAINING_COLUMN },
                null, null, null, null, NAME_COLUMN);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            if (!name.equalsIgnoreCase(UNCATEGORIZED)) {
                double total = Double.parseDouble(cursor.getString(2));
                double remaining = Double.parseDouble(cursor.getString(3));
                categories.add(new Category(name, total, remaining));
            }
            cursor.moveToNext();
        }
        return categories;
    }

    /**
     * 
     * @return Total allocated for the categories
     */
    public double getCategoriesTotal() {
        List<Category> categories = this.getCategories();
        double total = 0.0;
        for (Category category : categories) {
            total += category.getTotal();
        }
        return total;
    }

    public double getTotalRemaining() {
        List<Category> categories = this.getCategories();
        double remaining = 0.0;
        for (Category category : categories) {
            remaining += category.getRemaining();
        }
        return remaining;
    }

    /**
     * 
     * @return List of all category names in database
     */
    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<String>();
        Cursor cursor = mDb.query(CATEGORIES_TABLE,
                new String[] { NAME_COLUMN }, null, null, null, null,
                NAME_COLUMN);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return names;
    }

    // expense methods
    /**
     * Create a new row with Expense. If row is successfully created return the
     * new rowId for that note, otherwise return a -1 to indicate failure.
     * 
     * Subtract expense amount from category remaining amt
     * 
     * Precondition: category name exists
     * 
     * @param expense
     * @return rowId or -1 if failed
     */
    public long addExpense(Expense expense) {
        Log.i(TAG, "adding expense");
        String category = expense.getCategory();

        // subtract expense amount from category remaining amt and update
        // category
        Cursor cursor = mDb.query(CATEGORIES_TABLE,
                new String[] { REMAINING_COLUMN }, NAME_COLUMN + "=\'"
                        + category + "\'", null, null, null, null);
        cursor.moveToFirst();
        double curRemaining = Double.parseDouble(cursor.getString(0));
        cursor.close();
        ContentValues newRemainingAmt = new ContentValues();
        newRemainingAmt.put(REMAINING_COLUMN,
                curRemaining - expense.getAmount());
        mDb.update(CATEGORIES_TABLE, newRemainingAmt, NAME_COLUMN + "=\'"
                + category + "\'", null);

        ContentValues initialValues = new ContentValues();
        initialValues.put(DATE_COLUMN, expense.getDate());
        initialValues.put(AMOUNT_COLUMN, expense.getAmount());
        initialValues.put(CATEGORY_COLUMN, expense.getCategory());

        return mDb.insert(EXPENSES_TABLE, null, initialValues);
    }

    /**
     * 
     * @param categoryName
     * @return List of expenses in this category
     */
    public List<Expense> getExpenses(String categoryName) {
        List<Expense> expenses = new ArrayList<Expense>();
        Cursor cursor = mDb.query(EXPENSES_TABLE, new String[] { ROW_ID,
                DATE_COLUMN, AMOUNT_COLUMN, CATEGORY_COLUMN }, CATEGORY_COLUMN
                + "=\'" + categoryName + "\'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = Integer.parseInt(cursor.getString(0));
            String date = cursor.getString(1);
            double amount = Double.parseDouble(cursor.getString(2));
            String category = cursor.getString(3);
            expenses.add(new Expense(amount, date, category, id));
            cursor.moveToNext();
        }
        cursor.close();
        return expenses;
    }

    /**
     * Updates an expense
     * 
     * @param expense
     * @return
     */
    public boolean updateExpense(Expense expense) {

        Cursor cursor = mDb.query(EXPENSES_TABLE, new String[] { ROW_ID,
                DATE_COLUMN, AMOUNT_COLUMN, CATEGORY_COLUMN }, ROW_ID + "="
                + expense.getId(), null, null, null, null);
        cursor.moveToFirst();
        double oldAmt = Double.parseDouble(cursor.getString(2));
        String oldCategory = cursor.getString(3);
        cursor.close();

        // add oldamt from old category remaining
        Category old = this.getCategory(oldCategory);
        ContentValues oldCategoryUpdate = new ContentValues();
        oldCategoryUpdate.put(REMAINING_COLUMN, old.getRemaining() + oldAmt);
        mDb.update(CATEGORIES_TABLE, oldCategoryUpdate, NAME_COLUMN + "=\'"
                + oldCategory + "\'", null);

        // subtract newamt from new category remaining
        Category newCat = this.getCategory(expense.getCategory());
        ContentValues newCategoryUpdate = new ContentValues();
        newCategoryUpdate.put(REMAINING_COLUMN,
                newCat.getRemaining() - expense.getAmount());
        mDb.update(CATEGORIES_TABLE, newCategoryUpdate, NAME_COLUMN + "=\'"
                + expense.getCategory() + "\'", null);

        // update expense row
        ContentValues values = new ContentValues();
        values.put(DATE_COLUMN, expense.getDate());
        values.put(AMOUNT_COLUMN, expense.getAmount());
        values.put(CATEGORY_COLUMN, expense.getCategory());
        return mDb.update(EXPENSES_TABLE, values,
                ROW_ID + "=" + expense.getId(), null) == 1;
    }

    /**
     * removes expense row from database, adds expense amt back to
     * category.remaining Precondition: category exists
     * 
     * @param expense
     * @return
     */
    public boolean removeExpense(Expense expense) {
        // add expense amt to category remaining amt
        Category category = this.getCategory(expense.getCategory());
        ContentValues update = new ContentValues();
        update.put(REMAINING_COLUMN,
                category.getRemaining() + expense.getAmount());
        mDb.update(CATEGORIES_TABLE, update,
                NAME_COLUMN + "=\'" + expense.getCategory() + "\'", null);

        return mDb.delete(EXPENSES_TABLE, ROW_ID + "=" + expense.getId(), null) > 0;
    }
}
