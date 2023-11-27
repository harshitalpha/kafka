package com.example.kafka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaApis {

    private final KafkaProducer kafkaProducer;
    public final ReactiveKafkaProducer reactiveKafkaProducer;

    public KafkaApis(KafkaProducer kafkaProducer, ReactiveKafkaProducer reactiveKafkaProducer) {
        this.kafkaProducer = kafkaProducer;
        this.reactiveKafkaProducer = reactiveKafkaProducer;
    }

    @PostMapping("/send/{msg}/{part}")
    public ResponseEntity publishMessage(@PathVariable("msg") String msg, @PathVariable("part") String partition) {
        kafkaProducer.publishMessage(msg, partition);
        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/bulk/send/{count}")
    public ResponseEntity publishMessageBulk(@PathVariable("count") int count) {
        kafkaProducer.publishMessageBulk(count);
        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/reactive/send/{msg}")
    public ResponseEntity publishReactiveMessage(@PathVariable("msg") String msg) {
        reactiveKafkaProducer.publishMessage(msg);
        return ResponseEntity.ok("DONE");
    }


    @PostMapping("/reactive/bulk/send/{count}")
    public ResponseEntity publishReactiveMessage(@PathVariable("count") int count) {
        reactiveKafkaProducer.publishBulkMessage(count);
        return ResponseEntity.ok("DONE");
    }


}
