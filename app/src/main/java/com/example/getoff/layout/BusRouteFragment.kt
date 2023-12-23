package com.example.getoff.layout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getoff.R
import com.example.getoff.adapter.BusStopRViewAdapter
import com.example.getoff.databinding.BusstopItemBinding
import com.example.getoff.databinding.FragmentBusRouteBinding
import com.example.getoff.decoration.ItemDividerDecoration
import com.example.getoff.dto.BusStop
import com.example.getoff.response.ThirdResponse
import com.example.getoff.view.LocationViewModel


class BusRouteFragment : Fragment(), ConfirmDialogInterface {
    private val locationViewModel: LocationViewModel by activityViewModels()

    private var _binding: FragmentBusRouteBinding? = null
    private val binding get() = _binding!!

//    private var busStops: ArrayList<BusStop>? = arrayListOf()
    private var busStopList = mutableListOf<ThirdResponse.Response.Body.Items.Item>()

    private lateinit var rViewAdapter: BusStopRViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val busNumber = arguments?.getInt("busNumber")
        busStopList = arguments?.getParcelableArrayList("busStopList")!!

        val busNumberTextView = view.findViewById<TextView>(R.id.busNumber)
//        val busInfoTextView = view.findViewById<TextView>(R.id.busInfo)
        busNumberTextView.text = busNumber.toString()
//        busInfoTextView.text = busNumber.toString()

        setRouteRView()
    }

    private fun setRouteRView() {

        rViewAdapter = BusStopRViewAdapter(busStopList!!)
        binding.busRouteRecyclerView.adapter = rViewAdapter
        binding.busRouteRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding.BusRouteRecyclerView.apply { addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)) }
        binding.busRouteRecyclerView.apply { addItemDecoration(ItemDividerDecoration()) }

//        clickViewEvents()
//        busStopList!!.add(BusStop("id1", "name1", "lN1"))
        rViewAdapter.notifyItemInserted(busStopList!!.size - 1)

        rViewAdapter!!.itemClick = object : BusStopRViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                rViewAdapter.setSelectedItem(position)
                val dialog = ConfirmDialog(this@BusRouteFragment, "알람을 설정하시겠습니까?", position)
                dialog.isCancelable = false
                dialog.show(activity?.supportFragmentManager!!, "ConfirmDialog")
            }
        }
    }

//    private fun clickViewEvents() {
//        // 삭제 버튼 클릭
//        binding.deleteButton.setOnClickListener {
//            val dialog = ConfirmDialog(this, "패키지를 삭제하시겠습니까?", pkgId)
//            // 알림창이 띄워져있는 동안 배경 클릭 막기
//            dialog.isCancelable = false
//            dialog.show(this.supportFragmentManager, "ConfirmDialog")
//        }
//    }

//    override fun onStart() {
//        super.onStart()
////        val filter = IntentFilter("com.example.UPDATE_LOCATION")
//        val filter = IntentFilter().apply {
//            addAction("com.example.UPDATE_LOCATION")
//            addAction("com.example.TRIGGER_ARRIVE")
//        }
//        context?.registerReceiver(locationReceiver, filter)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        context?.unregisterReceiver(locationReceiver)
//    }

    override fun onYesButtonClick(id: Int) {
        // set alarm process
        // id로 리스트의 index 검색 목적지 버스 정류장 가져오기
        rViewAdapter.notifyDataSetChanged()
        val intent = Intent("com.example.UPDATE_DESTINATION")
        intent.putParcelableArrayListExtra("bus_stop_list", busStopList as java.util.ArrayList<out Parcelable>)
        intent.putExtra("destination_longitude", busStopList[id].gpslong)
        intent.putExtra("destination_latitude", busStopList[id].gpslati)
        context?.sendBroadcast(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(busNumber: String, busStopList: ArrayList<BusStop>) =
            BusRouteFragment().apply {
                arguments = Bundle().apply {
                    putString("busNumber", busNumber)
                    putParcelableArrayList("busStopList", busStopList)
                }
            }
    }
}