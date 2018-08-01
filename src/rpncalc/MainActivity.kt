package com.example.jakszc.rpncalc

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), View.OnClickListener, MediaPlayer.OnCompletionListener {

    var mp = MediaPlayer()
    val requestCode=10000
    var hashOfColors = hashMapOf<String, Int>()
    var screenColor: String = ""
    var precision = 10
    var stack : MutableList<Double> = mutableListOf()
    var stackHistory : StackHistory = StackHistory()
    var currVal : Double = 0.0
    var muteBool : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hashOfColors = hashMapOf(getString(R.string.color_name_1) to
                ContextCompat.getColor(this, R.color.color_1),
                getString(R.string.color_name_2) to ContextCompat.getColor(this, R.color.color_2),
                getString(R.string.color_name_3) to ContextCompat.getColor(this, R.color.color_3),
                getString(R.string.color_name_4) to ContextCompat.getColor(this, R.color.color_4),
                getString(R.string.color_name_5) to ContextCompat.getColor(this, R.color.color_5),
                getString(R.string.color_name_6) to ContextCompat.getColor(this, R.color.color_6))

        if(savedInstanceState == null) {
            screenColor = getString(R.string.color_name_6)
            hashOfColors[screenColor]?.let { ScreenLayout.setBackgroundColor(it) }
            hashOfColors[screenColor]?.let { bMenu.setBackgroundColor(it) }
            textViewPrecision.text = getString(R.string.precision) + " " + precision.toString()
            printStack()
        }

        b1.setOnClickListener(this)
        b2.setOnClickListener(this)
        b3.setOnClickListener(this)
        b4.setOnClickListener(this)
        b5.setOnClickListener(this)
        b6.setOnClickListener(this)
        b7.setOnClickListener(this)
        b8.setOnClickListener(this)
        b9.setOnClickListener(this)
        b0.setOnClickListener(this)

        bDot.setOnClickListener(this)
        bSign.setOnClickListener(this)
        bDivision.setOnClickListener(this)
        bMultiplication.setOnClickListener(this)
        bMinus.setOnClickListener(this)
        bPlus.setOnClickListener(this)
        bRoot.setOnClickListener(this)
        bPower.setOnClickListener(this)

        bEnter.setOnClickListener(this)
        bMute.setOnClickListener(this)
        bDrop.setOnClickListener(this)
        bSwap.setOnClickListener(this)
        bUndo.setOnClickListener(this)
        bClear.setOnClickListener(this)
        bMenu.setOnClickListener(this)

        mp.setOnCompletionListener {this}
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("precision", precision)
        outState?.putString("color", screenColor)
        outState?.putSerializable("stackHistory", stackHistory)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        precision = savedInstanceState?.getInt("precision") ?: 3
        screenColor = savedInstanceState?.getString("color") ?: "Pink"
        stackHistory = savedInstanceState?.getSerializable("stackHistory") as StackHistory
        stack = stackHistory.getCurrentFromHistory()
        printStack()
        hashOfColors[screenColor]?.let { ScreenLayout.setBackgroundColor(it) }
        hashOfColors[screenColor]?.let { bMenu.setBackgroundColor(it) }
        textViewPrecision.text = getString(R.string.precision) + " " + precision.toString()
    }

    override fun onClick(v: View) {
        if(!muteBool){
            mp.reset()
            mp = MediaPlayer.create(this, R.raw.button_click)
            mp.start()
        }
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_button_click)
        v.startAnimation(anim)

        when(v.id){
            R.id.b0 -> addNumber(0)
            R.id.b1 -> addNumber(1)
            R.id.b2 -> addNumber(2)
            R.id.b3 -> addNumber(3)
            R.id.b4 -> addNumber(4)
            R.id.b5 -> addNumber(5)
            R.id.b6 -> addNumber(6)
            R.id.b7 -> addNumber(7)
            R.id.b8 -> addNumber(8)
            R.id.b9 -> addNumber(9)
            R.id.bDot -> addDot()
            R.id.bEnter -> enterStack()
            R.id.bMenu -> menuButtonClick()
            R.id.bSign -> changeSignStack()
            R.id.bPlus -> addStack()
            R.id.bMinus -> substractStack()
            R.id.bDivision -> divideStack()
            R.id.bMultiplication -> multiplyStack()
            R.id.bRoot -> squareStack()
            R.id.bPower -> powerStack()
            R.id.bMute -> muteButtons()
            R.id.bDrop -> dropStack()
            R.id.bSwap -> swapStack()
            R.id.bUndo -> undoStack()
            R.id.bClear -> clearStack()
        }
    }

    override fun onCompletion(mp1: MediaPlayer?) {
        mp1!!.reset()
    }

    fun printStack () {
        textStack1.text = ""
        textStack2.text = ""
        textStack3.text = ""
        if(!stack.isEmpty()){
            var stackElem = stack[stack.size-1]
            stackElem = stackElem.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toDouble()
            textStack1.text = stackElem.toString()
            if(stack.size > 1){
                stackElem = stack[stack.size-2]
                stackElem = stackElem.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toDouble()
                textStack2.text = stackElem.toString()
            }
            if(stack.size > 2){
                stackElem = stack[stack.size-3]
                stackElem = stackElem.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toDouble()
                textStack3.text = stackElem.toString()
            }
        }
        else{
            textStack0.text = "--"
        }
    }

    fun addNumber(i: Int){
        if(textStack0.text.length < 16){
            if(textStack0.text == "--"){
                textStack0.text = i.toString()
            }
            else if ((textStack0.text == "0")){
                textStack0.text = i.toString()
            }
            else {
                var actText = textStack0.text
                actText = "$actText$i"
                textStack0.text = actText
            }
        }
    }

    fun addDot(){
        if((textStack0.text.length < 26) and (!textStack0.text.contains(".")) and (textStack0.text != "--")){
            var actText = textStack0.text
            textStack0.text = "$actText."
        }
    }

    fun enterStack(){
        if((textStack0.text != "--") and (textStack0.text != "")){
            currVal = textStack0.text.toString().toDouble()
            stack.add(currVal)
            stackHistory.addToHistory(stack)
            printStack()
            textStack0.text = ""
        }
    }

    fun menuButtonClick(){
        showActivity()
    }

    fun showActivity(){
        val i=Intent(this, MenuActivity::class.java)
        i.putExtra("oldColor", screenColor)
        i.putExtra("precision", precision)
        startActivityForResult(i, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== this.requestCode && resultCode== Activity.RESULT_OK){
            if(data != null){
                if(data.hasExtra("newColor")){
                    screenColor=data.extras.getString("newColor")
                    hashOfColors[screenColor]?.let { ScreenLayout.setBackgroundColor(it) }
                    hashOfColors[screenColor]?.let { bMenu.setBackgroundColor(it) }
                }
                if(data.hasExtra("newPrecision")){
                    precision=data.extras.getInt("newPrecision")
                    textViewPrecision.text = getString(R.string.precision) + " " + precision.toString()
                }
                printStack()
            }
        }
    }

    fun changeSignStack(){
            if((textStack0.text != "--") and (textStack0.text != "0") and (textStack0.text != "") and (textStack0.text != "0.")){
                if(textStack0.text.toString()[0] != '-'){
                    var actText = textStack0.text
                    textStack0.text = "-$actText"
                }
                else{
                    var actText = textStack0.text
                    actText = actText.substring(1)
                    textStack0.text = actText
                }
            }
    }

    fun addStack(){
        if(stack.size > 1){
            stack[stack.size-2] += stack[stack.size-1]
            stack = stack.dropLast(1).toMutableList()
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun substractStack(){
        if(stack.size > 1){
            stack[stack.size-2] -= stack[stack.size-1]
            stack = stack.dropLast(1).toMutableList()
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun divideStack(){
        if(stack.size > 1){
            stack[stack.size-2] /= stack[stack.size-1]
            stack = stack.dropLast(1).toMutableList()
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun multiplyStack(){
        if(stack.size > 1){
            stack[stack.size-2] *= stack[stack.size-1]
            stack = stack.dropLast(1).toMutableList()
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun squareStack(){
        if(stack.size > 0){
            stack[stack.size-1] = sqrt(stack[stack.size-1])
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun powerStack(){
        if(stack.size > 1){
            stack[stack.size-2] = stack[stack.size-2].pow(stack[stack.size-1])
            stack = stack.dropLast(1).toMutableList()
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun muteButtons(){
        muteBool = !muteBool
    }

    fun dropStack(){
        if(stack.size > 0){
            stack = stack.dropLast(1).toMutableList()
        }
        printStack()
    }

    fun swapStack(){
        if(stack.size > 1){
            var temp = stack[stack.size-2]
            stack[stack.size-2] = stack[stack.size-1]
            stack[stack.size-1] = temp
            printStack()
            stackHistory.addToHistory(stack)
        }
    }

    fun undoStack(){
        stack.clear()
        try{
            stack = stackHistory.getPreviousFromHistory()
        }catch (e: Exception){

        }
        printStack()
    }

    fun clearStack(){
        stack.clear()
        printStack()
        stackHistory.addToHistory(stack)
    }
}
