package com.elder.zcommonmodule.Widget.Search

import com.elder.zcommonmodule.BR
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.databinding.FragmentSearchCategoryBinding
import com.zk.library.Base.BaseFragment

class SearchCategoryFragment : BaseFragment<FragmentSearchCategoryBinding, SearchCategoryViewModel>() {

//    public static MainFragment newInstance() {
//
//        Bundle args = new Bundle();
//
//        MainFragment fragment = new MainFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
    override fun initContentView(): Int {
        return R.layout.fragment_search_category
    }

    override fun initVariableId(): Int {
        return BR.search_category_model
    }
}