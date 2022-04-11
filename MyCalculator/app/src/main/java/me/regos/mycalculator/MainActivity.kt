package me.regos.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.ArithmeticException
import java.lang.Exception
import kotlin.math.max
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null
    private var lastNumeric : Boolean = false
    private var lastDot : Boolean = false
    private var alreadyFloating : Boolean = false
    private var limit : Int = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View){
        if(tvInput?.text?.length!! < limit || !lastNumeric) {
            tvInput?.append((view as Button).text)
            lastNumeric = true
            lastDot = false
        } else {

            Toast.makeText(this, "Equation too long", Toast.LENGTH_LONG).show()
        }
    }

    fun onClear(view: View){
        tvInput?.text = ""
        lastNumeric = false
        lastDot = false
        alreadyFloating = false
    }

    fun onDecimalPoint(view: View){
        if(lastNumeric && !lastDot && !alreadyFloating){
            tvInput?.append(".")
            lastNumeric = false
            lastDot = true
            alreadyFloating = true
        }
    }

    fun onOperator(view: View){

        tvInput?.text?.let {
            if(((it.isEmpty() && (view as Button).text == "-") || (lastNumeric && !isOperatorAdded(it.toString()))) && tvInput?.text?.length!! <= limit){
                tvInput?.append((view as Button).text)
                lastNumeric = false
                lastDot = false
                alreadyFloating = false
            }
        }
    }

    fun onEqual(view : View){
        if(lastNumeric){
            var tvValue = tvInput?.text.toString()
            var prefix = ""

            try{
                if(tvValue.startsWith("-")){
                    prefix = "-"
                    tvValue = tvValue.substring(1)
                }
                when {
                    tvValue.contains("-") -> {

                        val parameters = determineEquation(tvValue, "-", prefix)

                        tvInput?.text = removeZeroAfterDot(((parameters.first.toDouble() - parameters.second.toDouble()).toDouble() / parameters.third).toString())

                    }
                    tvValue.contains("+") -> {

                        val parameters = determineEquation(tvValue, "+", prefix)

                        tvInput?.text = removeZeroAfterDot(((parameters.first.toDouble() + parameters.second.toDouble()).toDouble() / parameters.third).toString())

                    }
                    tvValue.contains("/") -> {

                        val parameters = determineEquation(tvValue, "/", prefix)

                        tvInput?.text = removeZeroAfterDot((parameters.first.toDouble() / parameters.second.toDouble()).toString())

                    }
                    tvValue.contains("*") -> {

                        val parameters = determineEquation(tvValue, "*", prefix)

                        tvInput?.text = removeZeroAfterDot(((parameters.first.toDouble() * parameters.second.toDouble()).toDouble() / (parameters.third * parameters.third)).toString())

                    }
                }

                alreadyFloating = tvInput?.text?.contains(".") == true

            }catch (e: ArithmeticException){
                e.printStackTrace()
            }

        }
    }

    private fun determineEquation(value: String, delimiter: String, prefix: String) : Triple<String, String, Long>{

        //prepare values

        val splitValue = value.split(delimiter)

        var one = removeZeroAfterDot(splitValue[0])
        var two = removeZeroAfterDot(splitValue[1])


        if (prefix.isNotEmpty()) {
            one = prefix + one
        }

        //determine multiplier

        var oneDotMultiplier = 0
        var twoDotMultiplier = 0

        if (one.contains(".")) {
            oneDotMultiplier = one.length - 1 - one.indexOf(".")
            one = one.replace(".", "")
        }

        if (two.contains(".")) {
            twoDotMultiplier = two.length - 1 - two.indexOf(".")
            two = two.replace(".", "")
        }


        val maxi = 10.0.pow(maxOf(oneDotMultiplier, twoDotMultiplier)).toLong()


        if(oneDotMultiplier == 0){
            one = (one.toLong() * maxi).toString()
        }
        if(twoDotMultiplier == 0){
            two = (two.toLong() * maxi).toString()
        }

        return Triple(one, two, maxi)
    }

    private fun removeZeroAfterDot(result : String) : String{
        var value = result
        if(value.contains(".") && !value.contains("E")){

            var substring = "." + value.substringAfter(".")

            var digitEncountered = false

            for(i in substring.length-1 downTo 0)
                if(!digitEncountered){
                    if(value.endsWith("0") || value.endsWith(".")) {
                        value = value.dropLast(1)
                    } else{
                        digitEncountered = true
                    }
                }

        }
        return value
    }

    private fun isOperatorAdded(value : String) : Boolean{
        return if(value.startsWith("-")){
            false
        }else{
            value.contains("/")
                    || value.contains("*")
                    || value.contains("+")
                    || value.contains("-")
        }
    }
}