package com.manurung.covidapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager){

    var pages: ArrayList<Fragment> = ArrayList()
    var titles: ArrayList<String> = ArrayList()


    // menentukan fragment yang akan dibuka pada posisi tertentu
    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    // judul untuk tabs
    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    fun addFragment(page: Fragment, title: String) {
        pages.add(page)
        titles.add(title)
    }


}