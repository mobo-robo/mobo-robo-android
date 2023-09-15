import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModel
import com.keyvalue.keycode.mobrain.login.LoginUiState
import com.keyvalue.keycode.mobrain.login.model.LoginResponse
import com.keyvalue.keycode.mobrain.util.PreferenceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query



class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState(isLoading = false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private lateinit var apiService: ApiService;


//   fun session()
//    {
//        PreferenceHelper.getSharedPreferenceString(context,PreferenceHelper.DEVICE_ID,it)
//    }

    fun login(hash: String,context: Context) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true
            )
        }


        val retrofit = Retrofit.Builder()
            .baseUrl("https://d76e-103-181-238-106.ngrok.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        val call: Call<LoginResponse> = apiService.getPosts(APIRequest(secret = hash))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Log.d("response", response.body().toString());
                    val response = response.body()
                    response?.data?.deviceId?.let {
                        Log.d("deviceId", it)
                        PreferenceHelper.setSharedPreferenceString(context,PreferenceHelper.DEVICE_ID,it)
                    };
                    // Process the data here
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("error", "err");
            }
        })

    }
}

interface ApiService {
    @POST("/v1/device/create")
    fun getPosts(
        @Body request: APIRequest,
    ): Call<LoginResponse>
}

data class APIRequest(var secret: String? = null)

