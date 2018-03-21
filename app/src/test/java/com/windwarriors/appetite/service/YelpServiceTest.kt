package com.windwarriors.appetite.service

import com.windwarriors.appetite.model.Business
import com.yelp.fusion.client.models.SearchResponse
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class YelpServiceTest {
    var yelpService: YelpService = YelpService()
    val q: BlockingQueue<SearchResponse> = LinkedBlockingQueue(1)

    @Before
    fun setUp() {
        yelpService.clear()
        yelpService.mockParameters()
    }

    @After
    fun tearDown() {
    }


    @Test
    fun search() {
        val response = runSearch()
        response?.let {
            for (c in it.businesses[0].categories) {
                println("category: " + c.title)
            }
            assertTrue("Empty Yelp.search response", it.total > 0)
        }
    }

    @Test
    fun getBusiness() {
        val businessId = "the-real-mccoy-burgers-and-pizza-scarborough"
        val syncObject = Object()

        val yelpCallback = object : YelpService.Callback<Business> {
            override fun onResponse(response: Business) {
                synchronized(syncObject) {
                    syncObject.notify()
                }
            }

            override fun onFailure(t: Throwable) {
                synchronized(syncObject) {
                    syncObject.notify()
                }
            }
        }

        yelpService.getBusiness(businessId, yelpCallback)

        synchronized (syncObject){
            syncObject.wait()
            //System.out.println("business:")
            //yelpService.business.toString()
            val responseBusinessId = yelpService.business.id
            assertEquals(businessId, responseBusinessId)
        }
    }

    @Test
    fun test_isClosed() {
        yelpService.open_now(false)
        val response = runSearch()
        response?.let {
            val closedBusinesses = it.businesses.filter { business -> business.isClosed }
            val openBusinesses = it.businesses.filter { business -> !business.isClosed }
            System.out.println("closedBusinesses: " + closedBusinesses.size)
            System.out.println("openBusinesses: " + openBusinesses.size)
        }
    }


    /*
    @Test
    fun mockSyncSearch() {
        yelpService.mockParameters()

        val response = yelpService.sync_search()
        assertNotNull(response)
    }

    @Test
    fun syncGetBusiness() {
        val businessId = "the-real-mccoy-burgers-and-pizza-scarborough"
        val business = yelpService.sync_getBusiness(businessId)

        assertNotNull(business)
        assertEquals(businessId, business.id)
    }
    */

    private fun runSearch(): SearchResponse? {
        val yelpCallback = object : YelpService.Callback<SearchResponse> {
            override fun onResponse(response: SearchResponse) {
                q.add(yelpService.response)
            }

            override fun onFailure(t: Throwable) {
                fail("Yelp.search.callback.onFailure:" + t.message)
            }
        }
        yelpService.search(yelpCallback)

        return getSearch()
    }

    private fun getSearch(): SearchResponse? {
        val timeout: Long = 5
        var response: SearchResponse? = null
        try {
            response = q.poll(timeout, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            fail("Yelp.search() did not respond in " + timeout + "s!")
        }

        return response
    }
}