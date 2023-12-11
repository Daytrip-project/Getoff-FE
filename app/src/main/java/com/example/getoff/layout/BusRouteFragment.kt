package com.example.getoff.layout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getoff.R
import com.example.getoff.adapter.BusStopRViewAdapter
import com.example.getoff.databinding.FragmentBusRouteBinding
import com.example.getoff.decoration.ItemDividerDecoration
import com.example.getoff.dto.BusStop
import com.example.getoff.view.ShareGPSViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BusRouteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BusRouteFragment : Fragment(), ConfirmDialogInterface {
    private val shareGPSViewModel: ShareGPSViewModel by viewModels()

    private var _binding: FragmentBusRouteBinding? = null
    private val binding get() = _binding!!

    private var busStops: ArrayList<BusStop>? = arrayListOf()

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            shareGPSViewModel.updateLocation(longitude, latitude)

//            val arriveTrigger = intent.getBooleanExtra("arrive_trigger", false)
//            if (arriveTrigger) {
//                val intent = Intent(activity, AlarmActivity::class.java)
//                startActivity(intent)
//            }
        }
    }

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

        val busNumber = arguments?.getString("busNumber")

        val busNumberTextView = view.findViewById<TextView>(R.id.busNumber)
        busNumberTextView.text = busNumber

        // observe GPS data
//        busInfoTV = view.findViewById<TextView>(R.id.busInfo)
        shareGPSViewModel.locationData.observe(viewLifecycleOwner) { location ->
            // 텍스트뷰 업데이트
//            busInfoTV?.text = "Longitude: ${location.first}, Latitude: ${location.second}"
        }

        setRouteRView()
    }

//    override fun onStart() {
//        super.onStart()
//        activity?.let { GpsUtil(it).requestLocation() } // 위치 권한 요청
//        GpsUtil.getLocation()
//    }

    private fun setRouteRView() {

        val rViewAdapter = BusStopRViewAdapter(busStops!!)
        binding.busRouteRecyclerView.adapter = rViewAdapter
        binding.busRouteRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding.busRouteRecyclerView.apply { addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)) }
        binding.busRouteRecyclerView.apply { addItemDecoration(ItemDividerDecoration()) }

//        clickViewEvents()
        busStops!!.add(BusStop("id1", "name1", "lN1"))
        rViewAdapter.notifyItemInserted(busStops!!.size - 1)

        rViewAdapter!!.itemClick = object : BusStopRViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
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

    override fun onStart() {
        super.onStart()
//        val filter = IntentFilter("com.example.UPDATE_LOCATION")
        val filter = IntentFilter().apply {
            addAction("com.example.UPDATE_LOCATION")
            addAction("com.example.TRIGGER_ARRIVE")
        }
        context?.registerReceiver(locationReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(locationReceiver)
    }

    override fun onYesButtonClick(id: Int) {
        // set alarm process
        // id로 리스트의 index 검색 목적지 버스 정류장 가져오기
        val intent = Intent("com.example.UPDATE_DESTINATION")
        intent.putExtra("destination_longitude", 25.123456789)
        intent.putExtra("destination_latitude", 10.123456789)
        context?.sendBroadcast(intent)

//        shareGPSViewModel.updateArriveTrigger()
//        shareGPSViewModel.arriveTrigger.observe(viewLifecycleOwner) { arriveTrigger ->
//            if (arriveTrigger) {
//                val intent = Intent(activity, AlarmActivity::class.java)
//                startActivity(intent)
//            }
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BusRouteFragment.
         */
        @JvmStatic
        fun newInstance(busNumber: String) =
            BusRouteFragment().apply {
                arguments = Bundle().apply {
                    putString("busNumber", busNumber)
                }
            }
    }
}