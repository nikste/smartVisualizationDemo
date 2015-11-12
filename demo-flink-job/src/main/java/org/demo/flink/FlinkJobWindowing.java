package org.demo.flink;

import org.apache.flink.api.common.state.OperatorState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.RMQTopology;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.demo.connections.RabbitMQQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikste on 05.11.15.
 */
public class FlinkJobWindowing {

    static Logger log = LoggerFactory.getLogger(FlinkJobCoMapRandomSample.class);

    static class ControlObject {
        public ControlObject(double samplingRate) {
            this.changeInDiscardProbability = samplingRate;
        }

        double changeInDiscardProbability;
    }

    public static void main(String[] args) {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // init data source
        DataStream<String> data = env.addSource(new RMQSource<>("localhost", RabbitMQQueueManager.DATA_QUEUE_NAME, new SimpleStringSchema()));
        // init control source
        final DataStream<String> control = env.addSource(new RMQSource<>("localhost", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, new SimpleStringSchema()));

        ConnectedStreams<String, String> fullStream = data.connect(control);

        // merge streams
        DataStream<Tuple2<Integer, String>> dataStream = fullStream.flatMap(new CoFlatMapFunction<String, String, Tuple2<Integer, String>>() {
            @Override
            public void flatMap1(String s, Collector<Tuple2<Integer, String>> collector) throws Exception {
                collector.collect(new Tuple2<>(0, s));
            }

            @Override
            public void flatMap2(String controlObject, Collector<Tuple2<Integer, String>> collector) throws Exception {
                // message will be number,number,value (1 => control message, y => control message type, z => control message value)
                collector.collect(new Tuple2<>(1, controlObject));
            }
        });


        DataStream<String> outputStream = dataStream
                .windowAll(TumblingTimeWindows.of(Time.of(1, TimeUnit.SECONDS)))//GlobalWindows.create())//Time.of(1, TimeUnit.SECONDS), Time.of(1, TimeUnit.SECONDS)))
                        //.trigger(ObjectCountTrigger.of(1000))
                        //.evictor(new ObjectCountEvictor())
                .apply(new AllWindowFunction<Tuple2<Integer, String>, String, TimeWindow>() {
                    //double downsamplefactor = 0.1;

                    @Override
                    public void apply(TimeWindow window, Iterable<Tuple2<Integer, String>> values, Collector<String> out) throws Exception {
                        {
                            //"bernoulli sampling with exact amount of elements:
                            //Random random = new Random();

                            Iterator<Tuple2<Integer, String>> iterator = values.iterator();

                            int counter = 0;
                            while (iterator.hasNext()) {
                                Tuple2<Integer, String> next = iterator.next();
                                if (next.f0 == 1) {
                                    continue;
                                }
                                //if (counter < 500 ) {
                                out.collect(next.f1);
                                counter++;
                                //}
                            }
                        }
                    }
                });


        outputStream.addSink(new RMQSink<String>("localhost", RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, new RMQTopology.StringToByteSerializer()));

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //////////////////////////TRIGGER////////////////////////////////////////

    public static class ObjectCountTrigger<W extends Window> implements Trigger<Tuple2<Integer, String>, W> {
        private static final long serialVersionUID = 1L;

        private long maxCount;

        private ObjectCountTrigger(long maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public TriggerResult onElement(Tuple2<Integer, String> element, long timestamp, W window, TriggerContext ctx) throws Exception {
            OperatorState<Long> count = ctx.getKeyValueState("count", 0L);
            OperatorState<Long> mCount = ctx.getKeyValueState("mCount", 0L);

            long currentCount = 0;
            if (element.f0 == 0) {
                currentCount = count.value() + 1;
            } else {
                // control element:
                //maxCount = (long) (1.0d / Double.parseDouble(element.f1));
                maxCount = 1000;
                currentCount = count.value();
            }
            count.update(currentCount);
            mCount.update(maxCount);
            if (currentCount >= maxCount) {
                count.update(0L);

                return TriggerResult.FIRE;
            }
            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onEventTime(long time, W window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public String toString() {
            return "CountTrigger(" + maxCount + ")";
        }

        /**
         * Creates a trigger that fires once the number of elements in a pane reaches the given count.
         *
         * @param maxCount The count of elements at which to fire.
         * @param <W>      The type of {@link Window Windows} on which this trigger can operate.
         */
        public static <W extends Window> ObjectCountTrigger<W> of(long maxCount) {
            return new ObjectCountTrigger<>(maxCount);
        }
    }

    private static class ObjectCountEvictor<W extends Window> implements Evictor<Object, W> {

        @Override
        public int evict(Iterable<StreamRecord<Object>> elements, int size, W window) {
            return 0;
        }
    }
}