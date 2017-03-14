package com.study.hancom.sharephototest.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An Adapter that populates a grid of 1-n columns. Unlike a standard Android GridView,
 * lists using this Adapter can label sections within the grid, and can include shorter
 * rows that do not fill each column space. Subclasses must define their sections and
 * contents.
 *
 * @author Velos Mobile
 */
/*
 * Copyright 2012 © Velos Mobile
 */
public abstract class SectionableAdapter<T> extends BaseAdapter {

    public static final int MODE_VARY_WIDTHS = 0;
    public static final int MODE_VARY_COUNT = 1;

    protected Context context;
    private LayoutInflater inflater;
    private int rowResID;
    protected int headerMenuHolderID;
    private int headerTextID;
    private int itemHolderID;
    private int colCount;
    private int resizeMode;
    private ViewGroup measuredRow;

    public SectionableAdapter(Context context, int rowLayoutID, int headerMenuHolderID, int headerTextID, int itemHolderID) {
        this(context, rowLayoutID, headerMenuHolderID, headerTextID, itemHolderID, MODE_VARY_WIDTHS);
    }

    /**
     * Constructor.
     *
     * @param rowLayoutID  layout resource ID for each row within the grid.
     * @param headerTextID     resource ID for the header element contained within the grid row.
     * @param itemHolderID resource ID for the cell wrapper contained within the grid row. This View must only contain cells.
     */
    public SectionableAdapter(Context context, int rowLayoutID, int headerMenuHolderID, int headerTextID, int itemHolderID, int resizeMode) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.rowResID = rowLayoutID;
        this.headerMenuHolderID = headerMenuHolderID;
        this.headerTextID = headerTextID;
        this.itemHolderID = itemHolderID;
        this.resizeMode = resizeMode;
        // Determine how many columns our row holds.
        View row = inflater.inflate(rowLayoutID, null);
        if (row == null)
            throw new IllegalArgumentException("Invalid row layout ID provided.");
        ViewGroup holder = (ViewGroup) row.findViewById(itemHolderID);
        if (holder == null)
            throw new IllegalArgumentException("Item holder ID was not found in the row.");
        if (holder.getChildCount() == 0)
            throw new IllegalArgumentException("Item holder does not contain any items.");
        colCount = holder.getChildCount();
    }

    /**
     * Returns the total number of items to display.
     */
    protected abstract int getDataCount();

    /**
     * Returns the number of sections to display.
     */
    protected abstract int getSectionsCount();

    /**
     * @param index the 0-based index of the section to count.
     * @return the number of items in the requested section.
     */
    protected abstract int getCountInSection(int index);

    /**
     * @param position the 0-based index of the data element in the grid.
     * @return which section this item belongs to.
     */
    protected abstract int getTypeFor(int position);

    /**
     * @param section the 0-based index of the section.
     * @return the text to display for this section.
     */
    protected abstract String getHeaderForSection(int section);

    /**
     * Populate the View and attach any listeners.
     *
     * @param cell     the inflated cell View to populate.
     * @param position the 0-based index of the data element in the grid.
     */
    protected abstract void bindView(View cell, int position);

    /**
     * Perform any row-specific customization your grid requires. For example, you could add a header to the
     * first row or a footer to the last row.
     *
     * @param row       the 0-based index of the row to customize.
     * @param rowView   the inflated row View.
     */
    protected void customizeRow(int row, View rowView) {
        // By default, does nothing. Override to perform custom actions.
    }

    protected int getColCount() {
        return colCount;
    }

    @Override
    public int getCount() {
        int totalCount = 0;
        int sectionCount = getSectionsCount();
        for (int i = 0; i < sectionCount; ++i) {
            int count = getCountInSection(i);
            if (count > 0)
                totalCount += (getCountInSection(i) - 1) / colCount + 1;
        }
        if (totalCount == 0)
            totalCount = 1;
        return totalCount;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(rowResID, parent, false);
            if (measuredRow == null && resizeMode == MODE_VARY_COUNT) {
                measuredRow = (ViewGroup) convertView;
                // In this mode, we need to learn how wide our row will be, so we can calculate
                // the number of columns to show.
                // This listener will notify us once the layout pass is done and we have our
                // measurements.
                measuredRow.getViewTreeObserver().addOnGlobalLayoutListener(layoutObserver);
            }
        }

        int realPosition = 0;
        int viewsToDraw = 0;
        int rows = 0;
        int totalCount = 0;
        int sectionsCount = getSectionsCount();

        for (int i = 0; i < sectionsCount; ++i) {
            int sectionCount = getCountInSection(i);
            totalCount += sectionCount;
            if (sectionCount > 0 && position <= rows + (sectionCount - 1) / colCount) {
                realPosition += (position - rows) * colCount;
                viewsToDraw = totalCount - realPosition;
                break;
            } else {
                if (sectionCount > 0) {
                    rows += (sectionCount - 1) / colCount + 1;
                }
                realPosition += sectionCount;
            }
        }

        int lastType = -1;

        if (realPosition > 0) {
            lastType = getTypeFor(realPosition - 1);
        }

        if (getDataCount() > 0) {
            LinearLayout headerMenu = (LinearLayout) convertView.findViewById(headerMenuHolderID);
            TextView headerText = (TextView) convertView.findViewById(headerTextID);
            int newType = getTypeFor(realPosition);
            if (newType != lastType) {
                headerMenu.setVisibility(View.VISIBLE);
                headerText.setVisibility(View.VISIBLE);
                headerText.setText(getHeaderForSection(newType));
            } else {
                headerMenu.setVisibility(View.GONE);
                headerText.setVisibility(View.GONE);
            }
        }

        customizeRow(position, convertView);

        ViewGroup itemHolder = (ViewGroup) convertView.findViewById(itemHolderID);
        for (int i = 0; i < itemHolder.getChildCount(); ++i) {
            View child = itemHolder.getChildAt(i);
            if (i < colCount && i < viewsToDraw && child != null) {
                bindView(child, realPosition + i);
                child.setVisibility(View.VISIBLE);
            } else if (child != null) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    private ViewTreeObserver.OnGlobalLayoutListener layoutObserver = new ViewTreeObserver.OnGlobalLayoutListener() {

        // The better-named method removeOnGlobalLayoutListener isn't available until a later API version.
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            if (measuredRow != null) {
                int rowWidth = measuredRow.getWidth();
                ViewGroup childHolder = (ViewGroup) measuredRow.findViewById(itemHolderID);
                View child = childHolder.getChildAt(0);
                int itemWidth = child.getWidth();
                if (rowWidth > 0 && itemWidth > 0) {
                    colCount = rowWidth / itemWidth;
                    // Make sure this listener isn't called again after we layout for the next time.
                    measuredRow.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    // The grid will now update with the correct column count.
                    notifyDataSetChanged();
                }
            }
        }
    };

}
