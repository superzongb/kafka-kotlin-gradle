package com.example.demo.pojo

import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.IndexedRecord

open class Press(data: String, timeStamp: Long) : IndexedRecord {
    var timeStamp: Long = timeStamp
    var data: String = data

    constructor (record: GenericRecord): this(record.get("data").toString(), record.get("timeStamp") as Long)

    companion object {
        val PIANO_NOTES = listOf<String>(
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

        const val SCHEMA_STR: String = "{\n" +
                "  \"namespace\": \"com.example.demo.pojo\",\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"Press\",\n" +
                "  \"fields\": [\n" +
                "    {\"name\": \"timeStamp\", \"type\": \"long\"},\n" +
                "    {\"name\": \"data\", \"type\": \"string\"}\n" +
                "  ]\n" +
                "}"

        val SCHEMA: Schema = Schema.Parser().parse(SCHEMA_STR)
    }

    override fun getSchema(): Schema {
        return SCHEMA
    }

    override fun put(field_index: Int, value: Any?) {
        when (field_index) {
            0 -> timeStamp = value as Long
            1 -> data = value as String
            else -> throw org.apache.avro.AvroRuntimeException("Bad index")
        }
    }

    override fun get(field_index: Int): Any {
        return when (field_index) {
            0 -> timeStamp
            1 -> data
            else -> throw org.apache.avro.AvroRuntimeException("Bad index")
        }
    }

    override fun toString(): String {
        return "Press(timeStamp=$timeStamp, data='$data')"
    }


}