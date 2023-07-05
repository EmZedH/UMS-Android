package com.example.ums.fragments

import androidx.fragment.app.Fragment
import com.example.ums.interfaces.Addable
import com.example.ums.interfaces.MultiDeleteListener
import com.example.ums.interfaces.Searchable
import com.example.ums.interfaces.SelectAllListener
import com.example.ums.interfaces.SelectionClearable

abstract class LatestListFragment:
    Fragment(),
    Addable,
    Searchable,
    SelectionClearable,
    MultiDeleteListener,
    SelectAllListener