package com.example.mensajesthreads

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.concurrent.thread

private lateinit var btStart : Button
private lateinit var btStop: Button
private lateinit var tvHilo1 : TextView
private lateinit var tvHilo2 : TextView
private lateinit var etNumero : EditText

// Codigos de mensaje para identificar los hilos
private val MENSAJE_CODE_HILO1=1
private val MENSAJE_CODE_HILO2=2

private var contadorH1=0
private var contadorH2=0
private var isRunning = false

private lateinit var handler: Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // enlazar componentes visuales
        btStart = findViewById(R.id.btStart)
        btStop = findViewById(R.id.btStop)
        tvHilo1 = findViewById(R.id.tvHilo1)
        tvHilo2 = findViewById(R.id.tvHilo2)
        etNumero = findViewById(R.id.etNumero)

        // iniciar el handler
        handler = object : Handler(
            Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    MENSAJE_CODE_HILO1 ->{
                        // actualizar contador hilo1: cronometro
                        contadorH1++
                        tvHilo1.text= "Hilo 1: ${msg.obj} | Tiempo: ${contadorH1} s"
                    }
                    MENSAJE_CODE_HILO2 -> {
                        // actualizar contador hilo1: cronometro
                        contadorH1++
                        tvHilo2.text= "Hilo 2: ${msg.obj} | Tiempo: ${contadorH2} s"
                    }
                }
            }
        }// fuera del handler
        btStart.setOnClickListener {
            startHilos()
        }
        btStop.setOnClickListener {
            stopHilos()
        }
    }
    private fun startHilos(){
        if (! isRunning){
            isRunning= true
            btStart.isEnabled = false
            btStop.isEnabled = true
            contadorH1 = 0
            contadorH2 = 0
            tvHilo1.text = "Hilo 1 Iniciando"
            tvHilo2.text = "Hilo 2 Iniciando"
            thread {
                var conadorLocal = 0
                while(isRunning){
                    conadorLocal += 2
                    // obtendiendo el mensage desde el handler
                    // aqui tambien le paso el obj
                    val message = handler.obtainMessage(
                        MENSAJE_CODE_HILO1,
                        "Contando .... ${conadorLocal}")
                    // enviamos el mensaje al hanlder,
                    // el handler procesa todo en el hilo principal
                    handler.sendMessage(message)
                    Thread.sleep(1000)
                }
            }
            thread{
                val textoNumero = etNumero.toString()
                val n = textoNumero.toIntOrNull()?:10

                for(i in 0 ..n){
                    if(!isRunning) break
                    val binTexto = Integer.toBinaryString(i)
                    val message = handler.obtainMessage(
                        MENSAJE_CODE_HILO2,
                        "Numero $i en Binario es : $binTexto "
                    )
                    handler.sendMessage(message)

                    Thread.sleep(2000)
                }
                if (isRunning)
                {
                    handler.post({stopHilos()})
                }

            }
        }
    }
    private fun stopHilos(){
        isRunning = false
        btStart.isEnabled = true
        btStop.isEnabled = false
        handler.removeCallbacksAndMessages(null)
    }
}