package com.example.getoff.layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getoff.R
import com.example.getoff.adapter.BusStopRViewAdapter
import com.example.getoff.databinding.FragmentBusRouteBinding
import com.example.getoff.decoration.ItemDividerDecoration
import com.example.getoff.dto.BusStop
import com.example.getoff.util.RetrofitUtil
import com.example.getoff.view.ShareStationViewModel
import kotlinx.coroutines.launch

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
//    private val binding by lazy { ActivityBusRouteBinding.inflate(layoutInflater) }
    private val shareStationViewModel: ShareStationViewModel by this.activityViewModels()

    private var _binding: FragmentBusRouteBinding? = null
    private val binding get() = _binding!!

    private var busStops: ArrayList<BusStop>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()

                // 또는 방법 2: 다른 프래그먼트로 교체
                // parentFragmentManager.beginTransaction()
                //     .replace(R.id.container, AnotherFragment())
                //     .commit()
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

        viewLifecycleOwner.lifecycleScope.launch {
            RetrofitUtil.requestRoute(busNumber!!)
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

    override fun onYesButtonClick(id: Int) {
        shareStationViewModel.setDestination(busStops!![id])
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