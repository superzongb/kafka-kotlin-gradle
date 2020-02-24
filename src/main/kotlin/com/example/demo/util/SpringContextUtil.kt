package com.example.demo.util

import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

class SpringContextUtil {
    companion object {
        private var applicationContext: ApplicationContext? = null

        //获取上下文
        fun getApplicationContext(): ApplicationContext? {
            return applicationContext
        }

        //设置上下文
        fun setApplicationContext(applicationContext: ApplicationContext) {
            SpringContextUtil.applicationContext = applicationContext
        }

        //通过名字获取上下文中的bean
        fun getBean(name: String?): Any? {
            return applicationContext!!.getBean(name)
        }

        //通过类型获取上下文中的bean
        fun getBean(requiredType: KClass<*>): Any? {
            return applicationContext!!.getBean(requiredType.java)
        }


    }


}