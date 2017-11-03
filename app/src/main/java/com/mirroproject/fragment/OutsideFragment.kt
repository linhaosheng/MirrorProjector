package com.mirroproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirroproject.R

/**
 * Created by reeman on 2017/11/1.
 */
class OutsideFragment : android.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        return View.inflate(activity, R.layout.fragemt_outside, null)
    }
}
