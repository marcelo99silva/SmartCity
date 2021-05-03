package ipvc.ei20799.smartcity.ui.mapa

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MotionEvent
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.dataclasses.Report
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.android.synthetic.main.activity_view_report.*

class ViewReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_report)

        val report = intent.extras?.get("report") as Report

        val decodedString: ByteArray = Base64.decode(report.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        ViewTituloReport.text = report.title
        ViewTipoReport.text = report.type_id
        ViewDescricaoReport.text = report.description
        viewImage.setImageBitmap(decodedByte)
        ViewReportDate.text = report.time

        ViewDescricaoReport.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }
}