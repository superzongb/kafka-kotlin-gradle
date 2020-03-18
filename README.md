# Kafka + Springboot + Avro

---------------------

这是一个体验 kafka + springboot + avro 的简单工程。

启动工程后，访问 localhost:8080，页面打开后会和服务端创建一个websocket连接，通过websocket将敲击钢琴的消息发送到后端，并转发到 kafka 的 Piano topic 中。在网页控制台输入 websocket.send("replay"), 可以启动回放功能，后端通过kafka consumer从topic订阅消息。在网页控制台输入 websocket.send("stream")，则可以在后端创建一个stream。



## kafka

参照https://blog.gmem.cc/apache-kafka-study-note 笔记，下载并启动一个zookeeper 和一个 kafka 服务

这个工程中包含一个 producer 和 两个分属不同 group 的 consumer。关于kafka的基本概念，这里不在赘述，参考上面提到的笔记。

## Avro + Confluent Schema Registry

在Kafka 中的消息是采用byte[]的格式存储的，这个时候，要将一个类型存储到/或从Kafka中取出，需要涉及到序列化的问题。在Avro和Kafka Schema Registry的帮助下，使用Avro序列化的Kafka Producers和Kafka Consumer都处理模式管理以及记录的序列化。

关于 Confluent Schema Registry 的基本教程可以参考 https://blog.csdn.net/weixin_41609807/article/details/103820327



### 需要引入的库

```
compile('io.confluent:kafka-avro-serializer:4.1.1')
compile('io.confluent:kafka-schema-registry-client:4.1.1')
```

### 关键的配置

```
//Producer
props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
props["value.serializer"] = "io.confluent.kafka.serializers.KafkaAvroSerializer"
props["schema.registry.url"] = "http://127.0.0.1:8081"

//Consumer
props["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
props["value.deserializer"] = "io.confluent.kafka.serializers.KafkaAvroDeserializer"
props[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
//这项可以直接反序列化生成具体的数据类，否则只生成GenericRecord
props[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"

```

注意，序列化和反序列化器分别是：

>  io.confluent.kafka.serializers.KafkaAvroSerializer
>
>  io.confluent.kafka.serializers.KafkaAvroDeserializer

Avro 能够处理的类型包括：

* Defined in Class: io.confluent.kafka.serializers.AbstractKafkaAvroSerDe

```java
protected Schema getSchema(Object object) {
  if (object == null) {
     return (Schema)primitiveSchemas.get("Null");
  } else if (object instanceof Boolean) {
     return (Schema)primitiveSchemas.get("Boolean");
  } else if (object instanceof Integer) {
     return (Schema)primitiveSchemas.get("Integer");
  } else if (object instanceof Long) {
     return (Schema)primitiveSchemas.get("Long");
  } else if (object instanceof Float) {
     return (Schema)primitiveSchemas.get("Float");
  } else if (object instanceof Double) {
     return (Schema)primitiveSchemas.get("Double");
  } else if (object instanceof String) {
     return (Schema)primitiveSchemas.get("String");
  } else if (object instanceof byte[]) {
     return (Schema)primitiveSchemas.get("Bytes");
  } else if (object instanceof IndexedRecord) {
     return ((IndexedRecord)object).getSchema();
  } else {
     throw new IllegalArgumentException("Unsupported Avro type. Supported types are null, Boolean, Integer, Long, Float, Double, String, byte[] and IndexedRecord");
  }
}
```

可见，除去基本类型外，必须是 *org.apache.avro.generic.IndexedRecord*。但实际上，为了能够让Avro能够直接将数据反序列化为具体的类，需要让这个类实现 *org.apache.avro.specific.SpecificRecord* 接口。

```java
public interface SpecificRecord extends IndexedRecord {
}
```

本项目中的例子是 *com.example.demo.pojo.Press*

1. 包含对应的schema

   ```kotlin
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
   ```

```kotlin
override fun getSchema(): Schema {
    return SCHEMA
}
```

2. 必须有一个**无参数的构造方法**

3. 重载 *put* 和 *get* 方法，用于序列化和反序列化

   ```kotlin
   override fun put(field_index: Int, value: Any?)
   override fun get(field_index: Int): Any
   ```



## Kafka Stream

