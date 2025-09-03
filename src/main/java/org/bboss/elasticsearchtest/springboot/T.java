//package com.icoderoad.demo;
//
//import com.icoderoad.demo.model.RecommendationResult;
//import com.icoderoad.demo.model.UserBehavior;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
//import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//public class RecommendationJob {
//
//    public static void main(String[] args) throws Exception {
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("group.id", "test");
//
//        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>("user-behavior-topic", new SimpleStringSchema(), properties);
//        kafkaConsumer.setStartFromEarliest();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        DataStream<UserBehavior> userBehaviors = env.addSource(kafkaConsumer)
//                .map((MapFunction<String, UserBehavior>) value -> objectMapper.readValue(value, UserBehavior.class));
//
//        DataStream<Tuple2<String, Map<String, Integer>>> productCountsPerUser = userBehaviors
//                .filter(behavior -> behavior.getAction().equals("purchase"))
//                .keyBy(UserBehavior::getUserId)
//                .flatMap((value, out) -> {
//                    Map<String, Integer> productCounts = new HashMap<>();
//                    productCounts.put(value.getProductId(), productCounts.getOrDefault(value.getProductId(), 0) + 1);
//                    out.collect(Tuple2.of(value.getUserId(), productCounts));
//                })
//                .keyBy(Tuple2::f0)
//                .reduce((t1, t2) -> {
//                    t1.f1.forEach((productId, count) -> t2.f1.merge(productId, count, Integer::sum));
//                    return t1;
//                });
//
//        DataStream<RecommendationResult> recommendations = productCountsPerUser
//                .map((MapFunction<Tuple2<String, Map<String, Integer>>, RecommendationResult>) value -> {
//                    RecommendationResult result = new RecommendationResult();
//                    result.setUserId(value.f0);
//                    List<String> recommendedProducts = new ArrayList<>(value.f1.keySet());
//                    result.setRecommendedProducts(recommendedProducts.subList(0, Math.min(5, recommendedProducts.size())));
//                    return result;
//                });
//
//        FlinkKafkaProducer<String> kafkaProducer = new FlinkKafkaProducer<>("recommendations-topic",
//                (RecommendationResult recommendation) -> objectMapper.writeValueAsString(recommendation),
//                properties);
//
//        recommendations.map(ObjectMapper::writeValueAsString).addSink(kafkaProducer);
//
//        env.execute("Recommendation Job");
//    }
//}
