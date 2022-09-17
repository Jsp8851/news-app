package vritant.projects.newsdaily

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var articleList = MutableLiveData<List<News>>()
    private lateinit var connectionLiveData: ConnectionLiveData

    fun getData(context: Context?): LiveData<List<News>> {

        if (context != null) {
            articleList = FetchNews(CATEGORY_GENERAL,context).getData() as MutableLiveData<List<News>>
        }

        return articleList
    }

    fun networkAvailable(context: Context?) : LiveData<Boolean>
    {
        if (context!=null)
            connectionLiveData = ConnectionLiveData(context)

        return connectionLiveData
    }

}