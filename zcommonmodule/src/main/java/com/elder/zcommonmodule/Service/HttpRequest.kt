package com.elder.zcommonmodule.Service

import android.util.Log
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.elder.zcommonmodule.Http.NetWorkManager
import com.elder.zcommonmodule.Service.Error.ExceptionEngine
import com.elder.zcommonmodule.Service.Error.HttpServerResponseError
import com.elder.zcommonmodule.Service.Error.ServerResponseError
import com.elder.zcommonmodule.Service.Login.LoginService
import com.elder.zcommonmodule.Service.Login.PrivateService
import com.elder.zcommonmodule.Service.Login.SocialService
import com.elder.zcommonmodule.Service.Team.RoadBookService
import com.elder.zcommonmodule.Service.Team.TeamService
import com.elder.zcommonmodule.USER_TOKEN
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.cs.tec.library.Base.Utils.context


class HttpRequest {
    companion object {
        val instance: HttpRequest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpRequest()
        }
    }


    var startDriver: HttpInteface.startDriverResult? = null

    var CreateResult: HttpInteface.CreateTeamResult? = null
    var JoinResult: HttpInteface.JoinTeamResult? = null
    var LGresult: HttpInteface.LoginResult? = null
    var QueryRollResult: HttpInteface.QueryRollInfo? = null
    var CheckTeamResult: HttpInteface.CheckTeamStatus? = null
    var getMakerList: HttpInteface.getMakerList? = null
    var RoadBookGuideList: HttpInteface.getRoadBookList? = null
    var RoadBookDetail: HttpInteface.RoadBookDetail? = null
    var SearchRoadBook: HttpInteface.SearchRoadBook? = null
    var downLoadRoad: HttpInteface.DownLoadRoodBook? = null
    var myRoadBook: HttpInteface.getMyRoadBook? = null

    //个人跳转后数据
    var socialGetdynamicList: HttpInteface.SocialMyDynamicList? = null
    var privateGetFansList: HttpInteface.PrivateFansList? = null
    var privateGetFocusList: HttpInteface.PrivateFocusList? = null
    var privateLikeList: HttpInteface.PrivateLikeList? = null
    var privateRestoreList: HttpInteface.PrivateRestoreList? = null
    var deleteSocialResult: HttpInteface.deleteSocialResult? = null
    var homeResult: HttpInteface.HomeResult? = null


    fun getRbookGuideResult(result: HttpInteface.getRoadBookList) {
        this.RoadBookGuideList = result
    }


    fun getMakerListResult(result: HttpInteface.getMakerList) {
        this.getMakerList = result
    }

    fun setLoginResult(result: HttpInteface.LoginResult) {
        this.LGresult = result
    }

    fun setCheckStatusResult(result: HttpInteface.CheckTeamStatus) {
        this.CheckTeamResult = result
    }

    fun setCreateTeamResult(result: HttpInteface.CreateTeamResult) {
        this.CreateResult = result
    }

    fun setJoinTeamResult(result: HttpInteface.JoinTeamResult) {
        this.JoinResult = result
    }

    fun setQueryRollInfoResult(result: HttpInteface.QueryRollInfo) {
        this.QueryRollResult = result
    }

    fun Login(map: HashMap<String, String>) {
        NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.login(NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
            return@map it
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.doOnError {
            LGresult?.LoginError(it)
        }?.onErrorResumeNext(Observable.empty())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                LGresult?.LoginSuccess(t)
            }

            override fun onError(e: Throwable) {
                LGresult?.LoginError(e)
            }
        })
    }


    fun startDriver(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.startDriver(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            startDriver?.startDriverError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                startDriver?.startDriverSuccess(t)
            }

            override fun onError(e: Throwable) {
                startDriver?.startDriverError(e)
            }

        })
    }


    fun createTeam(map: HashMap<String, String>) {
        //joinAddr
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.createTeam(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            CreateResult?.CreateTeamError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                CreateResult?.CreateTeamSuccess(t)
            }

            override fun onError(e: Throwable) {
                CreateResult?.CreateTeamError(e)
            }
        })
    }

    fun JoinTeam(map: HashMap<String, String>) {
        //joinAddr
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.JoinTeam(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            JoinResult?.JoinTeamError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                JoinResult?.JoinTeamSuccess(t)
            }

            override fun onError(e: Throwable) {
                JoinResult?.JoinTeamError(e)
            }
        })
    }


    fun checkTeamStatus(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.queryTeamInvalid(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.doOnError {
            CheckTeamResult?.CheckTeamStatusError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                CheckTeamResult?.CheckTeamStatusSucccess(t)
            }

            override fun onError(e: Throwable) {
                CheckTeamResult?.CheckTeamStatusError(e)
            }
        })
    }

    fun getManagerName(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.queryTeamRoleInfo(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            QueryRollResult?.QueryRollInfoError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                QueryRollResult?.QueryRollInfoSuccess(t)
            }

            override fun onError(e: Throwable) {
                QueryRollResult?.QueryRollInfoError(e)
            }
        })
    }


    fun getMakerList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.getMakerList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getMakerList?.getMakerListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getMakerList?.getMakerListSuccess(t)
            }

            override fun onError(e: Throwable) {
                getMakerList?.getMakerListError(e)
            }
        })
    }

    fun getRoadBookList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getRoadBookGuideList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RoadBookGuideList?.getRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RoadBookGuideList?.getRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                RoadBookGuideList?.getRoadBookError(e)
            }
        })
    }

    fun getRoadBookDetail(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getRoadBookDetail(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RoadBookDetail?.getRoadBookDetailError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RoadBookDetail?.getRoadBookDetailSuccess(t)
            }

            override fun onError(e: Throwable) {
                RoadBookDetail?.getRoadBookDetailError(e)
            }
        })
    }


    fun SearchRoadBookDetail(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.searchRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            SearchRoadBook?.SearchRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                SearchRoadBook?.SearchRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                SearchRoadBook?.SearchRoadBookError(e)
            }
        })
    }


    fun DownLoadRoadBook(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.downloadRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
            return@map it
        }?.doOnError {
            downLoadRoad?.DownLoadRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                downLoadRoad?.DownLoadRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                downLoadRoad?.DownLoadRoadBookError(e)
            }
        })
    }


    fun getMyRoadBook(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getMyRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            myRoadBook?.getMyRoadBookkError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                myRoadBook?.getMyRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                myRoadBook?.getMyRoadBookkError(e)
            }
        })
    }


    var postPhotoResult: HttpInteface.SocialUploadPhoto? = null
    var DynamicListResult: HttpInteface.SocialDynamicsList? = null


    var DynamicLikeResult: HttpInteface.SocialDynamicsLike? = null
    var DynamicCommentResult: HttpInteface.SocialDynamicsComment? = null
    var DynamicCommentListResult: HttpInteface.SocialDynamicsCommentList? = null
    var DynamicFocusResult: HttpInteface.SocialDynamicsFocus? = null
    var DynamicCollectionResult: HttpInteface.SocialDynamicsCollection? = null
    var DynamicLikerListResult: HttpInteface.SocialDynamicsLikerList? = null
    var DynamicFocusListResult: HttpInteface.SocialDynamicsFocuserList? = null


    fun postMuiltyPhoto(part: MultipartBody) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.uploadPhoto(token, part.parts())?.map(ServerResponseError())?.doOnError {
            postPhotoResult?.postPhotoError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                postPhotoResult?.postPhotoSuccess(t)
            }

            override fun onError(e: Throwable) {
                postPhotoResult?.postPhotoError(e)
            }

        })
    }


    var resultReleaseDynamics: HttpInteface.SocialReleaseDynamics? = null

    fun postReleaseDynamics(map: HashMap<String, Any>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.releaseDynamics(token, NetWorkManager.instance.getBaseRequestBodyAny(map)!!)?.map(ServerResponseError())?.doOnError {
            Log.e("result", "错误出现位置2" + it.message)
            resultReleaseDynamics?.ResultReleaseDynamicsError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                resultReleaseDynamics?.ResultReleaseDynamicsSuccess(t)
            }

            override fun onError(e: Throwable) {
                Log.e("result", "错误出现位置1" + e.message)
                resultReleaseDynamics?.ResultReleaseDynamicsError(e)
            }
        })
    }

    fun getDynamicsLike(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Like(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicLikeResult?.ResultLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicLikeResult?.ResultLikeError(e)
            }
        })
    }


    fun getDynamicsCommonList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getCommentList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCommentListResult?.ResultCommentListSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCommentListResult?.ResultCommentListError(e)
            }
        })
    }


    fun getDynamicsCommon(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Comment(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCommentResult?.ResultCommentSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCommentResult?.ResultCommentError(e)
            }
        })
    }


    fun getDynamicsCollection(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Collection(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCollectionResult?.ResultCollectionSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCollectionResult?.ResultCollectionError(e)
            }
        })
    }


    fun getDynamicsFocus(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Focus(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicFocusResult?.ResultFocusSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicFocusResult?.ResultFocusError(e)
            }
        })
    }


    fun getDynamicsList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getDynamicList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            Log.e("result", "请求回来了吗2")
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                Log.e("result", "请求回来了吗")
                DynamicListResult?.ResultSDListSuccess(t)
            }

            override fun onError(e: Throwable) {
                Log.e("result", "请求回来了吗1")
                DynamicListResult?.ResultSDListError(e)
            }
        })
    }

    fun getDynamicsLikeList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.LikerList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicLikerListResult?.ResultLikerError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicLikerListResult?.ResultLikerSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicLikerListResult?.ResultLikerError(e)
            }
        })
    }


    fun getDynamicsFocusList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.FocuserList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicFocusListResult?.ResultFocuserError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicFocusListResult?.ResultFocuserSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicFocusListResult?.ResultFocuserError(e)
            }
        })
    }


    //个人

    fun getSocialMyDynamicsList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.MyDynamicsList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            socialGetdynamicList?.ResultSocialMyDynamicError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                socialGetdynamicList?.ResultSocialMyDynamicSuccess(t)
            }

            override fun onError(e: Throwable) {
                socialGetdynamicList?.ResultSocialMyDynamicError(e)
            }
        })
    }

    fun getPrivateLikeList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyLikeList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateLikeList?.ResultPrivateLikeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateLikeList?.ResultPrivateLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateLikeList?.ResultPrivateLikeError(e)
            }
        })
    }

    fun getPrivateFansList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyFansList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateGetFansList?.ResultPrivateFansError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateGetFansList?.ResultPrivateFansSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateGetFansList?.ResultPrivateFansError(e)
            }
        })
    }

    fun getPrivateFocusList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyFocuserList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateGetFocusList?.ResultPrivateFocusError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateGetFocusList?.ResultPrivateFocusSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateGetFocusList?.ResultPrivateFocusError(e)
            }
        })
    }


    fun getPrivateCollection(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyCollection(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateRestoreList?.ResultPrivateDynamicError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }


            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateRestoreList?.ResultPrivateDynamicSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateRestoreList?.ResultPrivateDynamicError(e)
            }
        })
    }


    fun getHome(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.Home(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            homeResult?.ResultHomeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                homeResult?.ResultHomeSuccess(t)
            }

            override fun onError(e: Throwable) {
                homeResult?.ResultHomeError(e)

            }
        })
    }


    var RankingResut: HttpInteface.getRankResult? = null

    fun QueryRanking(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.QueryRanking(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RankingResut?.ResultRankingError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RankingResut?.ResultRankingSuccess(t)
            }

            override fun onError(e: Throwable) {
                RankingResut?.ResultRankingError(e)
            }
        })
    }


    var canalierResult: HttpInteface.CanalierResult? = null

    fun CanalierHome(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getCanalier(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            canalierResult?.ResultCanalierError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                canalierResult?.ResultCanalierSuccess(t)
            }

            override fun onError(e: Throwable) {
                canalierResult?.ResultCanalierError(e)
            }
        })
    }


    var queryMember: HttpInteface.SearchMember? = null

    fun SearchMember(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.QueryMember(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            queryMember?.SearchError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                queryMember?.SearchSuccess(t)
            }

            override fun onError(e: Throwable) {
                queryMember?.SearchError(e)
            }
        })
    }


    var getLikeResult: HttpInteface.GetLikeResult? = null


    fun GetLike(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getLikeResult(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getLikeResult?.getLikeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getLikeResult?.GetLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                getLikeResult?.getLikeError(e)
            }
        })
    }

    fun deleteSocial(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.deleteSocial(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            deleteSocialResult?.deleteSocialError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                deleteSocialResult?.deleteSocialSuccess(t)
            }

            override fun onError(e: Throwable) {
                deleteSocialResult?.deleteSocialError(e)
            }
        })
    }
}