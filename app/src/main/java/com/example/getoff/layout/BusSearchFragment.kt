package com.example.getoff.layout

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.getoff.R
import com.example.getoff.api.BusRouteApiService
import com.example.getoff.config.RetrofitConfig
import com.example.getoff.databinding.FragmentBusSearchBinding
import com.example.getoff.dto.FirstResponse
import com.example.getoff.dto.SecondResponse
import com.example.getoff.response.ThirdResponse
import com.example.getoff.view.LocationViewModel
import com.example.getoff.view.ShareGPSViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

private const val TAG = "BusSearchFragment_재성"
class BusSearchFragment : Fragment() {

    private val viewModel: LocationViewModel by activityViewModels()
//    private val viewModel: ShareGPSViewModel by activityViewModels()
//    val service = RetrofitConfig.retrofit.create(BusRouteApiService::class.java)

//    var cityCoderesult = -1
//    var routeIdListresult = mutableListOf<String>()
//    var busStopListresult = mutableListOf<ThirdResponse.Response.Body.Items.Item>()

//    private var busNumber: Int = -1

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

//        var latitude = 0.0
//        var longitude = 0.0

        //사용자가 위치한 도시이름(cityname) 불러오기
//        val targetCityName = getCityName(latitude, longitude)

        //사용자가 위치한 도시코드(citycode) 불러오기
//        getCityCode(targetCityName)
//        viewModel.savecityCoderesult(cityCoderesult)

//        viewModel.userCity.observe(viewLifecycleOwner, Observer { userCity ->
//            initTransportInfo(userCity, view.findViewById<EditText>(R.id.busNumEditText).text.toString().toInt())
//        })

        val searchButton = view.findViewById<ImageButton>(R.id.busSearchImageButton)
        searchButton.setOnClickListener {
            CoroutineScope(Main).launch {
                targetCityName = viewModel.getUserCityName().toString()
            }
            initTransportInfo(targetCityName, view.findViewById<EditText>(R.id.busNumEditText).text.toString().toInt())

//            busNumber = view.findViewById<EditText>(R.id.busNumEditText).text.toString().toInt()

//            CoroutineScope(IO).launch {
//                // 동기적으로 getRouteId 호출  //도시 코드, 버스 번호(노선명)으로 노선ID 가져오기
//                val routeIdResponse = service.getRouteId(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), 10, 1,"json",
//                    cityCoderesult, busNumber)
//                var routeIdList = routeIdResponse.body()?.response?.body?.items?.item
//                for(routeId in routeIdList!!){
//                    routeIdListresult.add(routeId.routeid)
//                }
//                viewModel.saverouteIdList(routeIdListresult)
//                Log.d(TAG, "onViewCreated: " + routeIdResponse)
//
//                if (routeIdResponse.isSuccessful) {
//                    // getRouteId 성공 후 getBusStopList 호출  //도시 코드, 노선 ID 로 정류장 리스트 가져오기
//                    val busStopListResponse1 = service.getBusStopList(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), 100, 1,"json",
//                        cityCoderesult, routeIdListresult[0])
//
//                    Log.d(TAG, "onViewCreated: " + busStopListResponse1)
//                    var busStopLIst = busStopListResponse1.body()?.response?.body?.items?.item
//                    for(busStop in busStopLIst!!){
//                        Log.d(TAG, "onViewCreated: busStopLIst| " + busStop.toString())
//                        busStopListresult.add(busStop)
//                    }
//                    viewModel.savebusStopLlist(busStopListresult)
//
//                    if(routeIdListresult.size > 1 && busStopListResponse1.isSuccessful) {
//                        // getRouteId 성공 후 getBusStopList 호출  //도시 코드, 노선 ID 로 정류장 리스트 가져오기
//                        val busStopListResponse2 = service.getBusStopList(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), 100, 1,"json",
//                            cityCoderesult, routeIdListresult[1])
//
//                        Log.d(TAG, "onViewCreated: " + busStopListResponse2)
//                        var busStopLIst = busStopListResponse2.body()?.response?.body?.items?.item
//                        for(busStop in busStopLIst!!){
//                            Log.d(TAG, "onViewCreated: busStopLIst| " + busStop.toString())
//                            busStopListresult.add(busStop)
//                        }
//                        viewModel.savebusStopLlist(busStopListresult)
//
//                        if (busStopListResponse2.isSuccessful) {
//                            //정류장 리스트를 다음 화면으로 넘긴다. (busNumber, busStopList)
//                            val newFragment: Fragment = BusRouteFragment.newInstance(busNumber,
//                                busStopListresult as ArrayList<ThirdResponse.Response.Body.Items.Item>
//                            )
//                            val transaction: FragmentTransaction =
//                                requireActivity().supportFragmentManager.beginTransaction()
//                            transaction.replace(R.id.mainContainer, newFragment)
//                            transaction.addToBackStack(null)
//                            transaction.commit()
//                        }
//                    }
//                }
//            }
        }
    }

    private fun initTransportInfo(targetCityName: String, busNumber: Int) {
        var allBusStopList:MutableList<List<ThirdResponse.Response.Body.Items.Item>> = mutableListOf()
        CoroutineScope(Main).launch{
            allBusStopList = viewModel.getTransportInfo(targetCityName, busNumber)
        }

        val newFragment: Fragment = BusRouteFragment.newInstance(busNumber,
            allBusStopList[0] as ArrayList<ThirdResponse.Response.Body.Items.Item>
        )
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

//    private suspend fun getBusStopList(cityCoderesult: Int, routeIdListresult: MutableList<String>) {
//        Log.d(TAG, "^^^^^^^^^6getBusStopList: " + routeIdListresult.size)
//        val busStopListCall = service.getBusStopList(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), 100, 1,"json", cityCoderesult, routeIdListresult[0])
//        busStopListCall.enqueue(object : Callback<ThirdResponse> {
//            override fun onResponse(call: Call<ThirdResponse>, response: Response<ThirdResponse>) {
//                Log.d(null, "################ busStopListCall: " + response.body()?.response?.body?.items?.item?.size)
//
//                val routeIdList = response.body()?.response?.body?.items?.item
////                    routeIdList.let { routeIdList ->
////                        for(routeId in routeIdList!!){
////                            Log.d(TAG, "onResponse: routeId = " + routeId.routeid)
////                            routeIdListresult.add(routeId.routeid)
////                        }
////                    }
//            }
//
//            override fun onFailure(call: Call<ThirdResponse>, t: Throwable) {
//                Log.d(null, "@@@@@@@@@@@@@@@@@@@@@@@@@" + t.toString())
//            }
//        })
//    }

    //https://apis.data.go.kr/1613000/BusRouteInfoInqireService/getRouteNoList?serviceKey=weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D&numOfRows=10&pageNo=1&_type=json&cityCode=37050&routeNo=185
//    private suspend fun getRouteId(cityCoderesult: Int, busNumber: Int) {
//        val routeIDCall = service.getRouteId(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), 10, 1,"json",
//            cityCoderesult, busNumber)
//        routeIDCall.enqueue(object : Callback<SecondResponse> {
//            override fun onResponse(call: Call<SecondResponse>, response: Response<SecondResponse>) {
//                Log.d(null, "################ routeIDCall: " + response.body()?.response?.body?.items?.item?.size)
//
//                val routeIdList = response.body()?.response?.body?.items?.item
//                routeIdList.let { routeIdList ->
//                    for(routeId in routeIdList!!){
//                        Log.d(TAG, "onResponse: routeId = " + routeId.routeid)
//                        routeIdListresult.add(routeId.routeid)
//                    }
//                }
//
//                Log.d(TAG, "%%%%%%%%%%%%%%routeIDCall: routeIdListresult size = " + routeIdListresult.size)
////                Log.d(TAG, "%%%%%%%%%%%%%%onResponse: routeIdListresult" + routeIdListresult[0])
//            }
//
//            override fun onFailure(call: Call<SecondResponse>, t: Throwable) {
//                Log.d(null, "@@@@@@@@@@@@@@@@@@@@@@@@@" + t.toString())
//            }
//        })
//    }

//    private fun getCityCode(targetCityName: String) {
//        val cityCodeCall = service.getAllCityCode(URLDecoder.decode("weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"), "json")
//        cityCodeCall.enqueue(object : Callback<FirstResponse> {
//            override fun onResponse(call: Call<FirstResponse>, response: Response<FirstResponse>) {
//                Log.d(null, "################ cityCodeCall: " + response.body()?.response?.body?.items?.item?.size)
//
//                val cityList = response.body()?.response?.body?.items?.item
//                cityList.let { cities ->
//                    for(city in cities!!){
//                        Log.d(TAG, "onResponse: city.cityname = " + city.cityname.take(2) + ", city.citycode = " + city.citycode)
//                        if(city.cityname.take(2) == targetCityName) {
//                            cityCoderesult = city.citycode
//                        }
//                    }
//                }
//
//                if(cityCoderesult != -1){
//                    Log.d(TAG, "onResponse: targetCityCode = " + cityCoderesult)
//                }
//            }
//
//            override fun onFailure(call: Call<FirstResponse>, t: Throwable) {
//                Log.d(null, "@@@@@@@@@@@@@@@@@@@@@@@@@" + t.toString())
//            }
//        })
//    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        return "구미"
    }

}