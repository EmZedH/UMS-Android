package com.example.ums

import android.content.Context
import android.widget.SearchView

class CustomSearchView(context: Context): SearchView(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val customWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
        resources.getDimensionPixelSize(R.dimen.custom_search_view_width),
        MeasureSpec.AT_MOST
    )
        super.onMeasure(customWidthMeasureSpec, heightMeasureSpec)
    }
}