package com.example.getoff.layout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.getoff.dto.BusStop
import com.example.getoff.response.ThirdResponse
import com.example.getoff.view.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private const val TAG = "BusSearchFragment_재성"
class BusSearchFragment : Fragment() {

    private val locationViewModel: LocationViewModel by activityViewModels()

    var targetCityName: String = ""

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            locationViewModel.updateLocation(latitude, longitude)
        }
    }

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
                targetCityName = context?.let { it -> locationViewModel.getUserCityName(it).await() }.toString()
                initTransportInfo(targetCityName, view.findViewById<EditText>(R.id.busNumEditText).text.toString())
            }
        }
    }

    private fun initTransportInfo(targetCityName: String, busNumber: String) {
        CoroutineScope(Main).launch{
            val allBusStopList = locationViewModel.getTransportInfoAsync(targetCityName, busNumber).await()

            var newFragment: Fragment
            if (targetCityName.contains("서울", ignoreCase = true)) {
                newFragment = BusRouteFragment.newInstance(busNumber,
                    allBusStopList[0] as ArrayList<BusStop>
                )

                createTransaction(newFragment)
            } else {
                CoroutineScope(Main).launch {
                    val userStations = context?.let { it -> locationViewModel.getUserStation(it, allBusStopList).await() }
                    newFragment = BusRouteFragment.newInstance(busNumber,
                        combineRoutes(userStations!![0], userStations[1], allBusStopList[0], allBusStopList[1]) as ArrayList<BusStop>
                    )

                    createTransaction(newFragment)
                }
            }
        }
    }

    private fun createTransaction(newFragment: Fragment) {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun combineRoutes(userStation1: BusStop, userStation2: BusStop, route1: List<BusStop>, route2: List<BusStop>): List<BusStop> {
        val index1 = route1.indexOf(userStation1)
        val index2 = route2.indexOf(userStation2)

        val after1 = if (index1 != -1 && index1 < route1.size - 1) route1.subList(index1 + 1, route1.size) else emptyList()
        val after2 = if (index2 != -1 && index2 < route2.size - 1) route2.subList(index2 + 1, route2.size) else emptyList()

        val combinedList = after1 + after2.reversed()

        return combinedList
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("com.example.UPDATE_LOCATION")
        context?.registerReceiver(locationReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(locationReceiver)
    }

}