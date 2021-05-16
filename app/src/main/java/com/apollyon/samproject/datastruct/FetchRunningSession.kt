package com.apollyon.samproject.datastruct

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.apollyon.samproject.RunActivity

class FetchRunningSession : ActivityResultContract<Context, RunningSession?>() {

    override fun createIntent(context: Context, input: Context?): Intent =
            Intent(input, RunActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): RunningSession? =
            when{
                resultCode != Activity.RESULT_OK -> null
                else -> intent?.getParcelableExtra<RunningSession>("session")
            }
}