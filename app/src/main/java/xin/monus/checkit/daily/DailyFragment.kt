package xin.monus.checkit.daily

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.*
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.TextView
import com.baoyz.widget.PullRefreshLayout
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.daily.dailyEdit.DailyEditActivity
import xin.monus.checkit.daily.stepCounter.config.Constant
import xin.monus.checkit.daily.stepCounter.service.StepService
import xin.monus.checkit.data.entity.Daily
import xin.monus.checkit.login.UserProfile


/**
 * @author wu
 * @date   2017/12/6
 */
class DailyFragment : Fragment(), DailyContract.View, Handler.Callback {


    //循环取当前时刻的步数中间的时间间隔
    private var TIME_INTERVAL = 500L
    //控件
    lateinit var stepNumberShow: TextView

    private val userMessage by lazy { UserProfile.getUser(activity) }
    lateinit var messenger : Messenger
    private val mGetReplyMessenger = Messenger(Handler(this))
    lateinit var delayHandler : Handler

    //以bind形式开启service，故有ServiceConnection接受回调
    var serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name : ComponentName?) {

        }

        override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
            try {
                messenger = Messenger(service)
                val msg = Message.obtain(null, Constant.MSG_FROM_CLIENT)
                msg.replyTo = mGetReplyMessenger
                messenger.send(msg)
            } catch (e : RemoteException) {
                e.printStackTrace()
            }
        }

    }

    //接收从后台回调的步数
    override fun handleMessage(msg : Message): Boolean {
        when(msg.what) {
            Constant.MSG_FROM_SERVER -> {
                //更新步数
                val stepNumber = msg.data.getInt("step")
                stepNumberShow.text = energy(stepNumber).toString()
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL)
            }
            Constant.REQUEST_SERVER -> {
                try {
                   val msg = Message.obtain(null, Constant.MSG_FROM_CLIENT)
                    msg.replyTo = mGetReplyMessenger
                    messenger.send(msg)
                } catch (e : RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    private fun energy(step : Int) : Float {
        val weight = userMessage.weight * 0.57
        val height = userMessage.height * 0.43
        val step = step * 0.5
        return if (weight + height + step - 108.44 < 0) {
            0f
        } else {
            (weight + height + step - 108.44).toFloat()
        }
        //return (weight + height + step - 108.44).toFloat()
    }

    interface ItemClickedListener{
        fun getID(itemID: Int)
        fun itemComplete(itemID: Int)
        fun itemDelete(itemID: Int)
    }

    override lateinit var presenter: DailyContract.Presenter

    lateinit var recycleView: SwipeMenuRecyclerView

    private val itemClickListener = object : DailyFragment.ItemClickedListener{
        override fun getID(itemID: Int) {
            val intent = Intent(context, DailyEditActivity::class.java)
            println(itemID)
            intent.putExtra("ID", itemID)
            startActivity(intent)
        }

        override fun itemComplete(itemID: Int) {
            presenter.completeButtonListener(itemID)
        }

        override fun itemDelete(itemID: Int) {
            println("delete project id: $itemID")

            presenter.deleteItem(itemID)
        }
    }

    private val listAdapter by lazy { DailyListAdapter(context, ArrayList(0), itemClickListener) }

    private lateinit var pullRefreshLayout: PullRefreshLayout

    private var isTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_daily_frag, container, false)

        with(root) {
            recycleView = findViewById(R.id.daily_item_list)
            pullRefreshLayout =findViewById(R.id.dailyPullRefreshLayout)
            pullRefreshLayout.setOnRefreshListener {
                pullRefreshLayout.setRefreshing(true)
                presenter.loadItems()
            }
            stepNumberShow = findViewById(R.id.step_number)
            delayHandler = Handler(this@DailyFragment)

        }


        val swipeBtnHeight = ViewGroup.LayoutParams.MATCH_PARENT
        val swipeBtnWidth = 200
        val swipeBtnTextSize = 20
        with(recycleView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setSwipeMenuCreator { _, swipeRightMenu, _ ->
                val deleteItem = SwipeMenuItem(context)
                        .setText(R.string.projects_swipe_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(swipeBtnTextSize)
                        .setBackgroundColor(Color.RED)
                        .setWidth(swipeBtnWidth)
                        .setHeight(swipeBtnHeight)
                swipeRightMenu.addMenuItem(deleteItem)
            }

            setSwipeMenuItemClickListener {menuBridge: SwipeMenuBridge ->
                menuBridge.closeMenu()
                val adapterPosition = menuBridge.adapterPosition
                val menuPosition = menuBridge.position
                println("click menu, position: $menuPosition")
                when (menuPosition) {
                    0 -> {
                        val projectId = listAdapter.list[adapterPosition].id
                        itemClickListener.itemDelete(projectId)
                    }
                }
            }

            adapter = listAdapter
        }


        setHasOptionsMenu(true)

        return root
    }

    override fun onStart() {
        super.onStart()
        setupService()
    }

    /**
     * 开启服务
     */
    private fun setupService() {
        val intent = Intent(context, StepService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        context.startService(intent)
    }

    override fun onDestroy() {
        //取消服务绑定
        context.unbindService(serviceConnection)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        println("daily fragment resume")
        presenter.start()
    }

    override fun showItems(list: List<Daily>) {
        listAdapter.list = list
    }

    override fun setEndRefresh() {
        pullRefreshLayout.setRefreshing(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.daily_add_btn -> {
                val intent = Intent(context, DailyEditActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance() = DailyFragment()
    }
}