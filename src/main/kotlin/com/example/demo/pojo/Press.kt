package com.example.demo.pojo

open class Press(data: String, timeStamp: Long) {
    var timeStamp: Long = timeStamp
    var data: String = data

    companion object {
        val pianoNotes = listOf<String>(
            "[data-key=\"65\"]",
            "[data-key=\"83\"]",
            "[data-key=\"68\"]",
            "[data-key=\"70\"]",
            "[data-key=\"71\"]",
            "[data-key=\"72\"]",
            "[data-key=\"74\"]",
            "[data-key=\"75\"]",
            "[data-key=\"76\"]",
            "[data-key=\"186\"]",
            "[data-key=\"87\"]",
            "[data-key=\"69\"]",
            "[data-key=\"84\"]",
            "[data-key=\"89\"]",
            "[data-key=\"85\"]",
            "[data-key=\"79\"]",
            "[data-key=\"80\"]"
        )
    }
}