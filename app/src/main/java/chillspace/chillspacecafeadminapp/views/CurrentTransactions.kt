package chillspace.chillspacecafeadminapp.views

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.adapters.CurrentTransactionRecyclerAdapter
import chillspace.chillspacecafeadminapp.models.CompletedTransaction
import chillspace.chillspacecafeadminapp.models.CurrentTransactionClientSide
import chillspace.chillspacecafeadminapp.viewmodels.CurrentTransactionViewModel
import kotlinx.android.synthetic.main.fragment_current_transactions.*

class CurrentTransactions : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currenttransactionrecycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()

        //showing dialog to load user transactions
        val builder = AlertDialog.Builder(activity as Activity)
        val progressBar: View = layoutInflater.inflate(R.layout.progress_layout, null)
        builder.setView(progressBar)
        val dialog = builder.create()
        dialog.show()

        val currentTransactionViewModel = ViewModelProviders.of(this).get(CurrentTransactionViewModel::class.java)

        val transactionListLiveData : LiveData<ArrayList<CurrentTransactionClientSide>> = currentTransactionViewModel.getCurrentTransactionLiveData()

        transactionListLiveData.observe(this, Observer {
            currenttransactionrecycler.adapter = CurrentTransactionRecyclerAdapter(it)

            dialog.dismiss()
        })
    }
}
