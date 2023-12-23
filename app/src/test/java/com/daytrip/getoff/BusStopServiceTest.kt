package com.daytrip.getoff

import com.daytrip.getoff.dto.User
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BusStopServiceTest {

    private lateinit var service: BusStopService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BusStopService::class.java)
    }

    @Test
    fun `get bus route test`() = runBlocking {
        // Mock response
        val mockResponse = MockResponse()
        mockResponse.setBody("""
            {
                "BusRouteSeoul": [{"id": "123", "name": "Station 1", "locationName": "location 1"}, {"id": "234", "name": "Station 2", "locationName": "location 2"}]
            }
        """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Perform request
        val responseBody  = service.getBusRouteSeoul("10")

        // Assert response
        assert(responseBody != null)
        assert(responseBody.BusRouteSeoul?.size == 2)
    }

    @Test
    fun `get boarding point test`() = runBlocking {
        // Mock response
        val mockResponse = MockResponse()
        mockResponse.setBody("""
            {
                "boardingPoint": {"id": "123", "name": "Station 1", "locationName": "location 1"}
            }
        """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Perform request
        val user = User(26.1f, 22.2f)
        val responseBody  = service.getBoardingPoint("10", 26.1f, 22.2f)

        // Assert response
        assert(responseBody != null)
        assert(responseBody.boardingPoint?.id == "123")
        assert(responseBody.boardingPoint?.name == "Station 1")
        assert(responseBody.boardingPoint?.locationName == "location 1")
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
