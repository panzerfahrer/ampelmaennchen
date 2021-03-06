package ampelmaennchen

import ampelmaennchen.config.RaspberryConfigurator
import ampelmaennchen.lights.PedestrianLight
import ampelmaennchen.lights.PedestrianLightControl
import com.pi4j.io.gpio.GpioFactory
import io.ktor.application.Application
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlin.concurrent.thread

fun Application.raspberry() {

    val gpio = GpioFactory.getInstance()

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        gpio.unexportAll()
        gpio.shutdown()
    })

    val raspberryConfig = RaspberryConfigurator(environment.config)

    val red = with(raspberryConfig.getPinConfiguration("red")) {
        val redPin = gpio.provisionDigitalOutputPin(pin, name, pinState)
        RaspberryLightSwitch("Red", redPin)
    }

    val green = with(raspberryConfig.getPinConfiguration("green")) {
        val greenPin = gpio.provisionDigitalOutputPin(pin, name, pinState)
        RaspberryLightSwitch("Green", greenPin)
    }

    val pedestrianLight = PedestrianLight(red, green)
    val actionHandler = RaspberryLightActionHandler()
    val lightControl = PedestrianLightControl(pedestrianLight, actionHandler)

    Server(this, lightControl)
}

fun main(args: Array<String>) {
    val environment = commandLineEnvironment(args)
    embeddedServer(Jetty, environment).start()
}