package com.android.statussavvy.base


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding

public abstract class BaseFragment<B : ViewBinding> : Fragment() {
    protected lateinit var mActivity: FragmentActivity
    protected var rootView: View? = null
    protected lateinit var binding: B

    protected var isFirstLoad = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //rootView = inflater.inflate(setLayoutId()!!, container, false)
        binding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false)

        rootView=binding.root
        //ButterKnife.bind(this, rootView!!)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initM()
    }

    protected abstract  fun initM()

    protected abstract fun setLayoutId(): Int


    protected open fun goActivity(activityClass: Activity, bundle: Bundle? = null) {
        if (bundle != null)
            mActivity.startActivity(Intent(mActivity, activityClass::class.java).putExtras(bundle))
        else
            mActivity.startActivity(Intent(mActivity, activityClass::class.java))
    }

    protected open fun showMsg(message: String) {
        Toast.makeText(mActivity, "" + message, Toast.LENGTH_SHORT).show()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity){
            mActivity=context
        }
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}