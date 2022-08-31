package com.wutsi.application.shell

import com.wutsi.application.shared.WutsiBffApplication
import com.wutsi.platform.core.WutsiApplication
import com.wutsi.platform.messaging.WutsiMessaging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@WutsiApplication
@WutsiMessaging
@WutsiBffApplication
@SpringBootApplication
@EnableScheduling
class Application

fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
