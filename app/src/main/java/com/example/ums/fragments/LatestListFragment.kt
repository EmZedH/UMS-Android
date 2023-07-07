package com.example.ums.fragments

import androidx.fragment.app.Fragment
import com.example.ums.interfaces.Addable
import com.example.ums.interfaces.AllSelectable
import com.example.ums.interfaces.MultiDeletable
import com.example.ums.interfaces.Searchable
import com.example.ums.interfaces.SelectionClearable

abstract class LatestListFragment:
    Fragment(),
    Addable,
    Searchable,
    SelectionClearable,
    MultiDeletable,
    AllSelectable