package com.android.statussavvy.FB

import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.statussavvy.R
import com.android.statussavvy.databinding.DownloadActivityBinding
import com.android.statussavvy.utils.DataSet
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.moon.baselibrary.base.BaseActivity
import java.io.File
import java.text.DecimalFormat

class DownloadActivity : BaseActivity<DownloadActivityBinding>() {
    var downloadStatusList: ArrayList<DownloadDataHolder> = ArrayList<DownloadDataHolder>()
    var adapter: DownloadAdapter? = null

    companion object {
        fun readableFileSize(j: Long): String? {
            if (j <= 0) {
                return "0"
            }
            val d = j.toDouble()
            val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                d / Math.pow(
                    1024.0,
                    log10.toDouble()
                )
            ) + " " + arrayOf("B", "kB", "MB", "GB", "TB")[log10]
        }
    }

    override fun initM() {
        setDownloadVideos()

        setListener()
    }

    private fun setListener() {
        binding.closeBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setDownloadVideos() {
        Dexter.withActivity(this).withPermission("android.permission.WRITE_EXTERNAL_STORAGE")
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    showDownloadedStatus()
                    if (downloadStatusList.size == 0) {
                        binding.closeBtn.visibility = View.VISIBLE
                    }
                    val file: File
                    file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            getString(R.string.app_name)
                        )
                    } else {
                        File(
                            Environment.getExternalStorageDirectory(),
                            getString(R.string.app_name)
                        )
                    }
                    if (!file.exists()) {
                        file.mkdirs()
                    }

                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    if (!permissionDeniedResponse.isPermanentlyDenied) {
                        Toast.makeText(
                            this@DownloadActivity,
                            "Temporory Permission Rejected ",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@DownloadActivity,
                            "Permanently Permission Rejected ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()

        val rv = binding.downloadRecycleView
        adapter = DownloadAdapter(this, downloadStatusList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)


    }

    private fun showDownloadedStatus() {
        var i: Int
        try {
            val listFiles: Array<File>
            listFiles = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + "Status Savvy"
                ).listFiles()
            } else {
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "Status Savvy"
                ).listFiles()
            }

//            File[] listFiles = new File(Environment.getExternalStorageDirectory() + File.separator + "Status Savvy").listFiles();
            // Arrays.sort(listFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE)
            this.downloadStatusList.clear()
            if (listFiles.size != 0) {
                for (file in listFiles) {
                    val file2 = file.toString()
                    val file3 = File(file2)
                    if (file3.length() > 0) {
                        val name = file3.name
                        i = if (name.contains(DataSet.INSTA_TAG)) {
                            1
                        } else if (name.contains(DataSet.WA_TAG)) {
                            2
                        } else {
                            if (name.contains("tiktok")) 3 else 0
                        }
                        this.downloadStatusList.add(
                            DownloadDataHolder(
                                file2,
                                file3.name,
                                i,
                                readableFileSize(file3.totalSpace)
                            )
                        )
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.download_activity
    }

    fun readableFileSize(j: Long): String? {
        if (j <= 0) {
            return "0"
        }
        val d = j.toDouble()
        val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            d / Math.pow(
                1024.0,
                log10.toDouble()
            )
        ) + " " + arrayOf("B", "kB", "MB", "GB", "TB")[log10]
    }
}