package com.example.demo.util

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

@Configuration
open class SpringContextUtil : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContextUtil.applicationContext = applicationContext
    }

    companion object {
        //获取applicationContext
        lateinit var applicationContext: ApplicationContext

        //通过name获取 Bean.
        fun getBean(name: String): Any {
            return applicationContext.getBean(name)
        }

        //通过class获取Bean.
        fun getBean(requiredType: KClass<*>): Any? {
            return applicationContext!!.getBean(requiredType.java)
        }

        //通过name,以及Clazz返回指定的Bean
        fun <T> getBean(name: String, clazz: Class<T>): T {
            return applicationContext.getBean(name, clazz)
        }
    }
}