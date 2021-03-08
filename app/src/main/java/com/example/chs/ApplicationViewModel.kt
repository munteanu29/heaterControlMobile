package com.example.chs

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.util.*


class ApplicationViewModelFactory : ViewModelProvider.Factory {

    @NonNull
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApplicationViewModel::class.java)) {
            val repository = ApplicationRepository(ApplicationDataSource())
            return ApplicationViewModel(repository) as T
        }

        return null!!
    }
}


class ApplicationViewModel constructor(private val repository: ApplicationRepository): ViewModel(){
    companion object {
        fun instantiate(activity: FragmentActivity): ApplicationViewModel {

            return ViewModelProviders
                .of(activity, ApplicationViewModelFactory()).get(ApplicationViewModel::class.java)
        }
    }
    fun login( username: String, password: String): Single<LoginResponse>{
        return repository.login(LoginRequest(username,password))
    }
    fun getTemperature(): Single<TemperatureResponse> {
        return repository.getTemperature()
    }
    fun setTemperature( temp: Int): Single<TemperatureResponse> {
        return repository.setTemperature(temp)
    }

    fun schedule( heaterSchedule: HeaterSchedule): Single<HeaterTimeResponse> {
        return repository.schedule(heaterSchedule)
    }
    fun getSchedule(): Single<Long> {
        return repository.getSchedule()
    }

}

class ApplicationRepository constructor(private val applicationDataSource: ApplicationDataSource){
    fun login(loginRequest: LoginRequest) : Single<LoginResponse>{
        return applicationDataSource.login(loginRequest)

    }

    fun getTemperature() : Single<TemperatureResponse> {
        return applicationDataSource.getTemperature()

    }
    fun getSchedule() : Single<Long> {
        return applicationDataSource.getSchedule()

    }

    fun setTemperature(temp:Int) : Single<TemperatureResponse> {
        return applicationDataSource.setTemperature(temp)

    }
    fun schedule(heaterSchedule:HeaterSchedule) : Single<HeaterTimeResponse> {
        return applicationDataSource.schedule(heaterSchedule)

    }



}

class ApplicationDataSource{

    fun login(loginRequest: LoginRequest) : Single<LoginResponse> {
        return ChsApplication.instance
            .authClient
            .authService
            .login(loginRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTemperature() : Single<TemperatureResponse> {
        return ChsApplication.instance
            .authClient
            .authService
            .getTemperature()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    fun getSchedule() : Single<Long> {
        return ChsApplication.instance
            .authClient
            .authService
            .getSchedule()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun setTemperature(temp: Int) : Single<TemperatureResponse> {
        return ChsApplication.instance
            .authClient
            .authService
            .setTemperature(temp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun schedule(temp: HeaterSchedule) : Single<HeaterTimeResponse> {
        return ChsApplication.instance
            .authClient
            .authService
            .schedule(temp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}

data class LoginRequest(val email:String, val password:String)

data class TemperatureRequest(val temperature: Int)

data class LoginResponse(val token: String)
data class HeaterTimeResponse(val time: Long)

data class TemperatureResponse(val temperature:Float, val setTemperature: Float, val isOn: Boolean)

data class HeaterSchedule( val Hour: Int,val Minute: Int, val FinalHouseTemperature: Float)
//data class HeaterSchedule(val HeaterStartTime: LocalDateTime, val HeaterFinishedTime: LocalDateTime, val InitialHouseTemperature: Float,val InitialOutsideTemperature: Float,val FinalOutsideTemperature: Float,val FinalHouseTemperature: Float,val HeaterAverageTemperature: Float,val OutsideAverageTemperature: Float,val HeatingTime: Float,val UserId: String)