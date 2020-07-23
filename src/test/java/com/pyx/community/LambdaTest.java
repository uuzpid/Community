package com.pyx.community;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class LambdaTest {


    @Test
    public void test(){
        Integer integer = addEnum(100,
                x -> x + x);
        System.out.println(integer);
    }

    private Integer addEnum(Integer num,MyInterface myInterface){
        Integer add = myInterface.add(num);
        return add;
    }

    @Test
    public void test2(){
        String str = changeString("str", x -> {
            char a = x.charAt(0);
            int value = Integer.valueOf(a);
            value = value -32;
            char c = (char)value;
            return c+x.substring(1);
        });
        System.out.println(str);
    }

    public String changeString(String string,TestInterface testInterface){
        return testInterface.getValue(string);
    }


    @Test
    public void test3() {
        List<String> list = new ArrayList<>();
        Stream<String> stream = list.stream();
        List list1 = new ArrayList();
        Stream stream1 = list1.stream();
        int i = IntStream.rangeClosed(0, 100)
                .parallel()
                .reduce(0, Integer::sum);
        System.out.println(i);
    }

    @Test
    public void test4(){
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        //2020-07-12T15:25:15.423

        //定义自己指定的日期
        LocalDateTime of =
                LocalDateTime.of(2015, 10, 19, 13, 22, 23);
        System.out.println(of);//2015-10-19T13:22:23

        //在当前的年数上加两年，还有加天，加月等等
        LocalDateTime localDateTime1 = localDateTime.plusYears(2);
        System.out.println(localDateTime1);//2022-07-12T15:29:09.255

        //相应的有减多少年等等
        LocalDateTime localDateTime2 = localDateTime.minusYears(1);
        System.out.println(localDateTime2);//2019-07-12T15:30:03.435

        System.out.println(localDateTime.getYear());//2020
        System.out.println(localDateTime.getMonth());//JULY
        System.out.println(localDateTime.getMonth().getValue());//7
        System.out.println(localDateTime.getMonthValue());//7
        System.out.println(localDateTime.getDayOfMonth());
        System.out.println(localDateTime.getHour());
        System.out.println(localDateTime.getSecond());
        System.out.println(localDateTime.getMinute());

    }
    @Test
    public void test5(){
        Instant instant = Instant.now();
        //2020-07-12T07:34:59.082Z 与系统时间不相同
        //默认获取UTC时区
        System.out.println(instant);

        //2020-07-12T15:37:25.220+08:00
        //手动增加偏移量，相较与UTC+8小时
        OffsetDateTime offsetDateTime =
                instant.atOffset(ZoneOffset.ofHours(8));
        System.out.println(offsetDateTime);

        //1594539580231 toEpochMilli()转成对应的毫秒值
        System.out.println(instant.toEpochMilli());
        System.out.println(System.currentTimeMillis());

        //1970-01-01T00:00:01Z 加一秒
        Instant instant1 = Instant.ofEpochSecond(1);
        System.out.println(instant1);
    }

    @Test
    public void test6(){
        Instant instant = Instant.now();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant instant1 = Instant.now();

        Duration duration = Duration.between(instant, instant1);
        System.out.println(duration);//PT1.001S 默认显示格式
        //duraton 有两个不同的方法格式获取时间 toXXXX，getXXX
        System.out.println(duration.toMillis());//1001 毫秒
        System.out.println(duration.getNano());//1000000 纳秒

        LocalTime localTime = LocalTime.now();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalTime localTime1 = LocalTime.now();

        System.out.println(Duration.between(localTime,localTime1).toMillis());//1001 毫秒
    }

    @Test
    public void test7(){
        LocalDate of = LocalDate.of(2015, 1, 1);
        LocalDate now = LocalDate.now();
        Period period = Period.between(of, now);
        System.out.println(period);//P5Y6M11D
        System.out.println(period.getYears());//5
        System.out.println(period.getMonths());//6
        System.out.println(period.getDays());//11
    }

    @Test
    public void test8(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);//2020-07-12T15:56:21.231
        //2020-07-10T16:12:23.077 用with 将月份指定为10号
        System.out.println(now.withDayOfMonth(10));

        LocalDateTime with = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        System.out.println(with);//2020-07-19T16:14:42.784 下个周日

        /**
         * 自定义：下一个工作日
         */
        LocalDateTime localDateTime1 = now.with((l) -> {
            LocalDateTime localDateTime = (LocalDateTime) l;
            DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
            if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
                return localDateTime.plusDays(3);//周五加三天就是下周一
            } else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
                return localDateTime.plusDays(2);
            } else
                return localDateTime.plusDays(1);
        });
        System.out.println(localDateTime1);
    }

    @Test
    public void test9(){
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        LocalDateTime ldf = LocalDateTime.now();
        String format = ldf.format(dtf);
        System.out.println(format);//2020-07-12
        //指定自己需要的格式化日期格式
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss SSS");
        String format1 = dtf2.format(ldf);
        System.out.println(format1);//2020年07月12日 16:24:39 779

        /**
         * 以dtf2格式的format1，时间类型，重新解析
         * format1的格式必须是dtf2
         */
        LocalDateTime localDateTime = ldf.parse(format1,dtf2);
        System.out.println(localDateTime);//2020-07-12T16:26:10.693
    }

    @Test
    public void test10(){
        Set<String> set = ZoneId.getAvailableZoneIds();
        set.forEach(System.out::println);
    }

    @Test
    public void test11(){
        /**
         * 指定时区
         */
        LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Tallinn"));
        System.out.println(ldt);//2020-07-12T11:34:04.767
        /**
         * 构建一个带时区显示的时间
         */
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Tallinn"));
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Tallinn"));
        System.out.println(zonedDateTime);//2020-07-12T11:34:04.768+03:00[Europe/Tallinn]
    }
}

