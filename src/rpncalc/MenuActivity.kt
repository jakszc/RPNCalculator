package com.example.jakszc.rpncalc

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_menu.*
import java.math.RoundingMode

class MenuActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var hashOfColors = hashMapOf<String, Int>()
    var listOfColors = listOf<String>()
    var listOfNumbers = listOf<Int>()
    var selectedColor = ""
    var precision = 3
    val exampleNumber: Double = 1.1234567891

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        hashOfColors = hashMapOf(getString(R.string.color_name_1) to ContextCompat.getColor(this, R.color.color_1),
                getString(R.string.color_name_2) to ContextCompat.getColor(this, R.color.color_2),
                getString(R.string.color_name_3) to ContextCompat.getColor(this, R.color.color_3),
                getString(R.string.color_name_4) to ContextCompat.getColor(this, R.color.color_4),
                getString(R.string.color_name_5) to ContextCompat.getColor(this, R.color.color_5),
                getString(R.string.color_name_6) to ContextCompat.getColor(this, R.color.color_6))
        listOfColors = listOf(getString(R.string.color_name_1), getString(R.string.color_name_2), getString(R.string.color_name_3),
                getString(R.string.color_name_4), getString(R.string.color_name_5), getString(R.string.color_name_6))
        listOfNumbers = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        val extras = intent.extras ?: return
        selectedColor = extras.getString("oldColor")
        precision = extras.getInt("precision")
        hashOfColors[selectedColor]?.let { ExampleLayout.setBackgroundColor(it) }

        spinner1.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfColors)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1!!.adapter = aa
        spinner1.setSelection(listOfColors.indexOf(selectedColor))

        spinner2.onItemSelectedListener = this
        val aa2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfNumbers)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2!!.adapter = aa2
        spinner2.setSelection(listOfNumbers.indexOf(precision))
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            R.id.spinner1 -> {
                selectedColor = listOfColors[position]
                hashOfColors[selectedColor]?.let { ExampleLayout.setBackgroundColor(it) }
            }
            R.id.spinner2 -> {
                precision = listOfNumbers[position]
                ExampleText.text = exampleNumber.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toString()
            }
        }
    }

    fun doneClicked(v: View){
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_button_click)
        v.startAnimation(anim)
        val data = Intent()
        data.putExtra("newColor", selectedColor)
        data.putExtra("newPrecision", precision)
        setResult(Activity.RESULT_OK,data)

        finish()
    }
}
