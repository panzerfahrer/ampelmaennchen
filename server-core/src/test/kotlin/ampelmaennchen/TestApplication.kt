package ampelmaennchen

import ampelmaennchen.lights.LightActionCallHandler
import ampelmaennchen.lights.actions.LightActionCallable
import ampelmaennchen.lights.actions.LightActionRunnable
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.response.respondText

fun Application.test() {
    createTestServer(this, actionHandler = object : LightActionCallHandler {
        suspend override fun perform(call: ApplicationCall, action: LightActionRunnable) {
            action.run()
            call.respondText("tested: $action")
        }

        suspend override fun perform(call: ApplicationCall, action: LightActionCallable<out Any>) {
            val result = action.call()
            call.respondText("tested: $action => $result")
        }
    })
}