package com.example.ums

import androidx.fragment.app.Fragment
import com.example.ums.listener.AddListener
import com.example.ums.listener.SearchListener

abstract class AddableSearchableFragment: Fragment(),AddListener, SearchListener