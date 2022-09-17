package vritant.projects.newsdaily

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel : ViewModel() {

    private var newsList = MutableLiveData<List<News>>()
    private lateinit var connectionLiveData: ConnectionLiveData

    fun getData(context: Context?,category : String): LiveData<List<News>> {


        if (context != null) {
            newsList = FetchNews(category,context).getData() as MutableLiveData<List<News>>
        }

        return newsList
    }

    fun networkAvailable(context: Context?) : LiveData<Boolean>
    {
        if (context!=null)
            connectionLiveData = ConnectionLiveData(context)

        return connectionLiveData
    }

}