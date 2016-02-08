package com.marked.vifo.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.vifo.R
import kotlinx.android.synthetic.main.fragment_contact_list.view.*

/**
 * Created by Tudor Pop on 08-Feb-16.
 */
open class RecyclerViewFragment : Fragment() {
	lateinit var recyclerView: RecyclerView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_contact_list, container, false)
		recyclerView = root.contactRecyclerView
		recyclerView.apply {
			setHasFixedSize(true)
		}
		return root
	}

}