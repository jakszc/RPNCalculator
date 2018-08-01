package com.example.jakszc.rpncalc

import java.io.Serializable

class StackHistory : Serializable{
    var stackHistory : MutableList<MutableList<Double>> = mutableListOf(mutableListOf())

    fun addToHistory(currentStack: MutableList<Double> ){
        stackHistory.add(currentStack.toMutableList())
    }

    fun getPreviousFromHistory(): MutableList<Double> {
        if(stackHistory.size > 1){
            stackHistory.removeAt(stackHistory.size-1)
        }
        return stackHistory[stackHistory.size-1].toMutableList()
    }

    fun getCurrentFromHistory(): MutableList<Double> {
        return stackHistory[stackHistory.size-1].toMutableList()
    }
}