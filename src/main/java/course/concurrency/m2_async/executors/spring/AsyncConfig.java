package course.concurrency.m2_async.executors.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig {

//    @Bean
//    public Executor applicationTaskExecutor() {
//        return Executors.newSingleThreadExecutor();
//    }
//
//    @Bean(name = "executor_1")
//    public Executor getExecutor_1() {
////        return new ThreadPoolExecutor(
////                4,
////                8,
////                5000, TimeUnit.MILLISECONDS,
////                new LinkedBlockingQueue<>());
//        return Executors.newSingleThreadExecutor();
//    }
//
//    @Bean(name = "executor_2")
//    public Executor getExecutor_2() {
////        return new ThreadPoolExecutor(
////                4,
////                8,
////                5000, TimeUnit.MILLISECONDS,
////                new LinkedBlockingQueue<>());
//        return Executors.newSingleThreadExecutor();
//    }

}
