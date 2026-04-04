package com.example.keuangankita.ui.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barColors = listOf(
        Color.parseColor("#FF7043"),
        Color.parseColor("#42A5F5"),
        Color.parseColor("#66BB6A"),
        Color.parseColor("#AB47BC"),
        Color.parseColor("#FFA726"),
        Color.parseColor("#78909C"),
        Color.parseColor("#EF5350"),
        Color.parseColor("#26C6DA"),
        Color.parseColor("#D4E157")
    )

    private val paintBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val paintValue = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color          = Color.WHITE
        textAlign      = Paint.Align.CENTER
        isFakeBoldText = true
        textSize       = 28f
    }
    private val paintLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color     = Color.parseColor("#B0B0B0")
        textAlign = Paint.Align.CENTER
        textSize  = 28f
    }
    private val paintGrid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color       = Color.parseColor("#333333")
        strokeWidth = 1.5f
        style       = Paint.Style.STROKE
    }

    // Lebar tiap bar dalam dp - FIXED agar tidak mengecil saat banyak kategori
    private val BAR_WIDTH_DP  = 48f
    private val BAR_SPACE_DP  = 32f  // jarak antar bar
    private val PAD_START_DP  = 16f
    private val PAD_END_DP    = 16f
    private val PAD_TOP_DP    = 36f
    private val PAD_BOTTOM_DP = 48f

    private var data: Map<String, Double> = emptyMap()

    fun setData(dataMap: Map<String, Double>) {
        data = dataMap
        // Paksa ukur ulang agar width menyesuaikan jumlah bar
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dp = resources.displayMetrics.density
        val barCount = data.size.coerceAtLeast(1)

        // Total lebar konten = padding + (barWidth + space) * jumlah bar
        val contentWidth = (PAD_START_DP + PAD_END_DP +
                barCount * BAR_WIDTH_DP +
                (barCount - 1) * BAR_SPACE_DP) * dp

        // Lebar minimum adalah lebar layar, tapi bisa lebih lebar kalau banyak bar
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val finalWidth  = contentWidth.coerceAtLeast(parentWidth)

        // Tinggi tetap fixed
        val finalHeight = (PAD_TOP_DP + PAD_BOTTOM_DP + 180f) * dp

        setMeasuredDimension(finalWidth.toInt(), finalHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return
        if (width == 0 || height == 0) return

        val entries  = data.entries.sortedByDescending { it.value }
        val maxValue = entries.maxOf { it.value }
        if (maxValue <= 0) return

        val dp       = resources.displayMetrics.density
        val padL     = PAD_START_DP  * dp
        val padT     = PAD_TOP_DP    * dp
        val padB     = PAD_BOTTOM_DP * dp
        val barW     = BAR_WIDTH_DP  * dp
        val spacing  = (BAR_WIDTH_DP + BAR_SPACE_DP) * dp
        val chartH   = height.toFloat() - padT - padB
        val chartW   = width.toFloat() - padL - PAD_END_DP * dp

        paintValue.textSize = 11f * dp
        paintLabel.textSize = 11f * dp

        // Garis grid horizontal
        paintGrid.alpha = 60
        for (i in 1..3) {
            val gy = padT + chartH - (chartH * i / 4f)
            canvas.drawLine(padL, gy, padL + chartW, gy, paintGrid)
        }

        // Gambar setiap bar
        entries.forEachIndexed { index, entry ->
            val color   = barColors[index % barColors.size]
            val barH    = ((entry.value / maxValue) * chartH).toFloat().coerceAtLeast(4f * dp)
            val centerX = padL + index * spacing + barW / 2f
            val left    = padL + index * spacing
            val right   = left + barW
            val top     = padT + chartH - barH
            val bottom  = padT + chartH

            // Bar solid
            paintBar.color  = color
            paintBar.alpha  = 255
            paintBar.shader = null
            canvas.drawRoundRect(RectF(left, top, right, bottom), 8f * dp, 8f * dp, paintBar)

            // Highlight putih atas bar
            paintBar.color = Color.WHITE
            paintBar.alpha = 25
            canvas.drawRoundRect(RectF(left, top, right, top + barH * 0.35f), 8f * dp, 8f * dp, paintBar)
            paintBar.alpha = 255

            // Nilai
            paintValue.color = Color.WHITE
            paintValue.alpha = 230
            val valueText = formatShort(entry.value)
            if (barH > 28f * dp) {
                canvas.drawText(valueText, centerX, top + 16f * dp, paintValue)
            } else {
                canvas.drawText(valueText, centerX, top - 5f * dp, paintValue)
            }

            // Label kategori
            paintLabel.alpha = 255
            canvas.drawText(entry.key, centerX, bottom + 16f * dp, paintLabel)
        }
    }

    private fun formatShort(value: Double): String {
        return when {
            value >= 1_000_000_000 -> "${(value / 1_000_000_000).toInt()}M"
            value >= 1_000_000     -> String.format("%.1fJt", value / 1_000_000)
            value >= 1_000         -> "${(value / 1_000).toInt()}rb"
            else                   -> value.toInt().toString()
        }
    }
}
