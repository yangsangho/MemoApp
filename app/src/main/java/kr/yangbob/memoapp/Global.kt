package kr.yangbob.memoapp

import android.annotation.SuppressLint
import android.app.Activity
import com.tedpark.tedpermission.rx2.TedRx2Permission

enum class Mode {
    Add, Detail, Edit
}

@SuppressLint("CheckResult")
fun checkPermissionAndRun(
        activity: Activity,
        permissionList: Array<String>,
        rationaleMsgId: Int,
        deniedMsgId: Int,
        run: () -> Unit
) {
    if (!TedRx2Permission.isGranted(activity, *permissionList)) {
        val deniedList = TedRx2Permission.getDeniedPermissions(activity, *permissionList).toTypedArray()
        TedRx2Permission.with(activity)
                .setRationaleMessage(rationaleMsgId)
                .setPermissions(*deniedList)
                .setDeniedMessage(deniedMsgId)
                .request()
                .subscribe({ tedPermissionResult ->
                    if (tedPermissionResult.isGranted) run()
                }, { })
    } else {
        run()
    }
}