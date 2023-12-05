package com.example.getoff.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getoff.R
import com.example.getoff.adapter.BusStopRViewAdapter
import com.example.getoff.dao.BusStop
import com.example.getoff.databinding.FragmentBusRouteBinding
import com.example.getoff.decoration.ItemDividerDecoration

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
    private var _binding: FragmentBusRouteBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

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

        val busStops = ArrayList<BusStop>()
        busStops.add(BusStop("홍길동", "here"))
        busStops.add(BusStop("홍길동", "here"))

        val adapter = BusStopRViewAdapter(busStops)
        binding.busRouteRecyclerView.adapter = adapter
        binding.busRouteRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding.busRouteRecyclerView.apply { addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)) }
        binding.busRouteRecyclerView.apply { addItemDecoration(ItemDividerDecoration()) }

//        clickViewEvents()

        adapter.itemClick = object : BusStopRViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val dialog = ConfirmDialog(this@BusRouteFragment, "알람을 설정하시겠습니까?", position)
                // 알림창이 띄워져있는 동안 배경 클릭 막기
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
//        deletePackageApiCall(id)
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment BusRouteFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            BusRouteFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}