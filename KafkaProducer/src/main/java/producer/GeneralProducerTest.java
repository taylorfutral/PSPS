package producer;



public class GeneralProducerTest {

//    public GeneralProducerTest() {
//    }

    // Returns how long it takes to push data for the situation below
    public static long timeTest_pushData() {
        GeneralProducer gp = new GeneralProducer();

        long startTime = System.nanoTime();
        gp.pushData("dogs", "./");
        long endTime = System.nanoTime();
        gp.getProducer().close();

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        return duration;
    }

    public static void main(String[] args) {
        GeneralProducerTest.timeTest_pushData();
    }

}