package com.example.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NerdLauncherActivity"

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }

    private fun setupAdapter(){
     //MAIN 액션이며, CATEGORY_LAUNCHER로 암시적 인텐트를 생성한다.
     val startupIntent = Intent(Intent.ACTION_MAIN).apply {
         addCategory(Intent.CATEGORY_LAUNCHER)
     }
     //첫번째 인자로 전달된 인텐트와 일치하는 필터를 갖는 모든 액티비티의 데이터가 반환된다.
     //두번째 인자로 정수 값의 플래그를 지정하여 반환 결과를 변경할 수 있다.
     val activities = packageManager.queryIntentActivities(startupIntent, 0)

     //sort 함수로 알파벳 순서로 정렬
     activities.sortWith(Comparator{a, b -> String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(packageManager).toString(), b.loadLabel(packageManager).toString())})

     Log.i(TAG, "Found ${activities.size} activities")
      recyclerView.adapter = ActivityAdapter(activities)
    }


    //ViewHolder
    private class ActivityHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private val nameTextView = itemView as TextView
        private lateinit var resolveInfo : ResolveInfo

        //onClickListener
        init {
            nameTextView.setOnClickListener(this)
        }
        fun bindActivity(resolveInfo: ResolveInfo){
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            nameTextView.text = appName
        }

        //onClickListener를 통해 패키지 명과, 클래스를 받아와 명시적 인텐트로 넘겨준다.
        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = view.context
            context.startActivity(intent)
        }

    }

    //Adapter
    private class ActivityAdapter(val activities : List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {
                val layoutInflater = LayoutInflater.from(container.context)
                val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, container, false)
                return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
                val resolveInfo = activities[position]
                holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }


    }
}