package com.example.getoff.layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.getoff.R
import com.example.getoff.response.ThirdResponse
import com.example.getoff.view.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private const val TAG = "BusSearchFragment_재성"
class BusSearchFragment : Fragment() {

    private val viewModel: LocationViewModel by activityViewModels()

    var targetCityName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bus_search, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchButton = view.findViewById<ImageButton>(R.id.busSearchImageButton)
        searchButton.setOnClickListener {
            CoroutineScope(Main).launch {
                targetCityName = viewModel.getUserCityName().await()
                initTransportInfo(targetCityName, view.findViewById<EditText>(R.id.busNumEditText).text.toString().toInt())
            }
        }
    }

    private fun initTransportInfo(targetCityName: String, busNumber: Int) {
        CoroutineScope(Main).launch{
            val allBusStopList = viewModel.getTransportInfoAsync(targetCityName, busNumber).await()

            val newFragment: Fragment = BusRouteFragment.newInstance(busNumber,
                allBusStopList[0] as ArrayList<ThirdResponse.Response.Body.Items.Item>
            )
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainContainer, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}