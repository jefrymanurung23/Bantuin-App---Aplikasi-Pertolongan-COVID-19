package com.manurung.covidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class InfoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mcontext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_info, container, false)
        // Inflate the layout for this fragment
        editSearch = v.findViewById<EditText>(R.id.edtSearch)
        viewInfo = v.findViewById<View>(R.id.recyclerViewInfo) as RecyclerView

        //daftar gejala yang akan ditampilkan
        viewInfo.layoutManager = LinearLayoutManager(mcontext, RecyclerView.VERTICAL, false)

        val url = "https://corona.lmao.ninja/v2/countries/"
        val request = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                infoModelsList.clear()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val countryName = jsonObject.getString("country")
                        val cases = jsonObject.getString("cases")
                        val todayCases = jsonObject.getString("todayCases")
                        val deaths = jsonObject.getString("deaths")
                        val todayDeaths = jsonObject.getString("todayDeaths")
                        val recovered = jsonObject.getString("recovered")
                        val active = jsonObject.getString("active")
                        val critical = jsonObject.getString("critical")
                        val jsonObject2 = jsonObject.getJSONObject("countryInfo")
                        val flagUrl = jsonObject2.getString("flag")
                        Log.d("country", countryName)
                        infoModelsList.add(InfoModel(flagUrl, countryName, cases, todayCases, deaths, todayDeaths, recovered, active, critical))
                    }

                    displayList.addAll(infoModelsList)

                    myInfoAdapter = InfoAdapter(displayList)
                    viewInfo.adapter = myInfoAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(mcontext, error.message, Toast.LENGTH_SHORT).show()
            })
        val requestQueue = Volley.newRequestQueue(mcontext)
        requestQueue.add(request)

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s!!.isNotEmpty()) {
                    displayList.clear()
                    val search = s.toString().toLowerCase(Locale.getDefault())
                    infoModelsList.forEach {
                        if (it.country.toLowerCase(Locale.getDefault()).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    viewInfo.adapter!!.notifyDataSetChanged()
                } else {
                    displayList.clear()
                    displayList.addAll(infoModelsList)
                    if (viewInfo.adapter != null)
                        viewInfo.adapter!!.notifyDataSetChanged()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) activity!!.finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        lateinit var v: View
        lateinit var editSearch: EditText
        lateinit var viewInfo: RecyclerView

        var infoModelsList = ArrayList<InfoModel>()
        var displayList = ArrayList<InfoModel>()

        lateinit var myInfoAdapter: InfoAdapter

        lateinit var mcontext: Context
    }

    private fun fetchData() {

    }
}
