package xin.monus.checkit.forecast

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Forecast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ForecastFragment : Fragment(), ForecastContract.View {
    override lateinit var presenter: ForecastContract.Presenter

    lateinit var element1:RelativeLayout
    lateinit var element2:RelativeLayout
    lateinit var element3:RelativeLayout
    lateinit var element4:RelativeLayout
    lateinit var element5:RelativeLayout
    lateinit var element6:RelativeLayout
    lateinit var deadlineLabel: TextView

    val forecastAdapter by lazy {ForecastAdapter(requireContext(), ArrayList(0)) }
    lateinit var recyclerView: RecyclerView

    val elementList = ArrayList<RelativeLayout>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_forecast_frag, container, false)

        with(root) {
            element1 = findViewById(R.id.element1)
            element2 = findViewById(R.id.element2)
            element3 = findViewById(R.id.element3)
            element4 = findViewById(R.id.element4)
            element5 = findViewById(R.id.element5)
            element6 = findViewById(R.id.element6)

            deadlineLabel = findViewById(R.id.deadline_text)

            recyclerView = findViewById(R.id.recyclerView)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = forecastAdapter
            }
        }
        elementList.add(element1)
        elementList.add(element2)
        elementList.add(element3)
        elementList.add(element4)
        elementList.add(element5)
        elementList.add(element6)
        for (element in elementList) {
            element.setOnClickListener {
                showElement(elementList.indexOf(element))
            }
        }

        setupDate()

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    val fList1 = ArrayList<Forecast>() // 过去
    val fList2 = ArrayList<Forecast>() // 今天
    val fList3 = ArrayList<Forecast>() // +1
    val fList4 = ArrayList<Forecast>() // +2
    val fList5 = ArrayList<Forecast>() // +3
    val fList6 = ArrayList<Forecast>() // 将来

    override fun initData(forecastList: List<Forecast>) {
        val sfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sfCommon = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val sfYmd = SimpleDateFormat("yyyy-MM-dd")
        val current = Date()
        val cal = Calendar.getInstance()
        cal.time = current
        val time1 = current.time
        val today = sfYmd.parse(sfYmd.format(current))
        cal.time = today
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val time2 = cal.time.time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val time3 = cal.time.time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val time4 = cal.time.time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val time5 = cal.time.time
        // 清空缓存数据
        fList1.clear()
        fList2.clear()
        fList3.clear()
        fList4.clear()
        fList5.clear()
        fList6.clear()
        for (forecast in forecastList) {
            val deadline = forecast.deadline
            val date = try {
                sfCommon.parse(deadline)
            } catch (e: Exception) {
                sfFull.parse(deadline)
            }
            when {
                date.time < time1 -> fList1.add(forecast)
                date.time < time2 -> fList2.add(forecast)
                date.time < time3 -> fList3.add(forecast)
                date.time < time4 -> fList4.add(forecast)
                date.time < time5 -> fList5.add(forecast)
                else -> fList6.add(forecast)
            }
        }

        setTaskNum()
    }

    private fun setTaskNum() {
        val num1 = element1.findViewById<TextView>(R.id.number)
        val num2 = element2.findViewById<TextView>(R.id.number)
        val num3 = element3.findViewById<TextView>(R.id.number)
        val num4 = element4.findViewById<TextView>(R.id.number)
        val num5 = element5.findViewById<TextView>(R.id.number)
        val num6 = element6.findViewById<TextView>(R.id.number)

        if (fList1.size != 0) {
            num1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_red))
        } else {
            num1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }

        if (fList2.size != 0) {
            num2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_yellow))
        } else {
            num2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }
        if (fList3.size != 0) {
            num3.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_yellow))
        } else {
            num3.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }
        if (fList4.size != 0) {
            num4.setTextColor(Color.BLACK)
        } else {
            num4.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }
        if (fList5.size != 0) {
            num5.setTextColor(Color.BLACK)
        } else {
            num5.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }
        if (fList6.size != 0) {
            num6.setTextColor(Color.BLACK)
        } else {
            num6.setTextColor(ContextCompat.getColor(requireActivity(), R.color.forecast_date_gray))
        }

        num1.text = fList1.size.toString()
        num2.text = fList2.size.toString()
        num3.text = fList3.size.toString()
        num4.text = fList4.size.toString()
        num5.text = fList5.size.toString()
        num6.text = fList6.size.toString()
    }

    private fun setupDate() {
        val date1 = element1.findViewById<TextView>(R.id.date_label)
        val date2 = element2.findViewById<TextView>(R.id.date_label)
        val date3 = element3.findViewById<TextView>(R.id.date_label)
        val date4 = element4.findViewById<TextView>(R.id.date_label)
        val date5 = element5.findViewById<TextView>(R.id.date_label)
        val date6 = element6.findViewById<TextView>(R.id.date_label)
        date1.setText(R.string.forecast_before)
        date2.setText(R.string.forecast_today)
        date6.setText(R.string.forecast_future)

        val weekArray = arrayOf(R.string.forecast_sun, R.string.forecast_mon, R.string.forecast_tues,
                R.string.forecast_wed, R.string.forecast_thur, R.string.forecast_fri, R.string.forecast_sat)

        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        val weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1
        println("week day: $weekDay")
        date3.setText(weekArray[(weekDay+1)%7])
        date4.setText(weekArray[(weekDay+2)%7])
        date5.setText(weekArray[(weekDay+3)%7])
    }


    override fun showElement(index: Int) {
        for (element in elementList) {
            element.setBackgroundColor(Color.WHITE)
        }
        elementList[index].setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.forecast_background_gray))


        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val today = Date()
        val cal = Calendar.getInstance()
        cal.time = today

        when (index) {
            0 -> {
                deadlineLabel.setText(R.string.forecast_before)
                forecastAdapter.list = fList1
            }
            1 -> {
                deadlineLabel.text = dateFormat.format(today)
                forecastAdapter.list = fList2
            }
            2 -> {
                cal.add(Calendar.DAY_OF_MONTH, 1)
                deadlineLabel.text = dateFormat.format(cal.time)
                forecastAdapter.list = fList3
            }
            3 -> {
                cal.add(Calendar.DAY_OF_MONTH, 2)
                deadlineLabel.text = dateFormat.format(cal.time)
                forecastAdapter.list = fList4
            }
            4 -> {
                cal.add(Calendar.DAY_OF_MONTH, 3)
                deadlineLabel.text = dateFormat.format(cal.time)
                forecastAdapter.list = fList5
            }
            5 -> {
                deadlineLabel.setText(R.string.forecast_future)
                forecastAdapter.list = fList6
            }
        }


    }

    companion object {
        fun newInstance() = ForecastFragment()
    }

}