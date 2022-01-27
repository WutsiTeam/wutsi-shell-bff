package com.wutsi.application.shell

import com.wutsi.application.shared.WutsiBffApplication
import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@WutsiApplication
@WutsiBffApplication
@SpringBootApplication
@EnableScheduling
@EnableAsync
class Application

fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
