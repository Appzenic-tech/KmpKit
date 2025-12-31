package com.appzenic.kmpkit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform