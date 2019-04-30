package com.moon.core.net;

import com.moon.core.lang.JoinerUtil;
import org.junit.jupiter.api.Test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author benshaoye
 * @date 2018/9/17
 */
class InternetUtilTestTest {

    @Test
    void testGetLocalIP() throws UnknownHostException {
        System.out.println(InternetUtil.getLocalIP4());

        InetAddress address = InetAddress.getLocalHost();

        System.out.println(address.toString());
        System.out.println(address.hashCode());
        System.out.println(JoinerUtil.join(address.getAddress()));
        System.out.println(address.getCanonicalHostName());
        System.out.println(address.getHostAddress());

        Inet6Address.getLocalHost();
    }

    static class Time {
        private int hour;
        private int minute;
        private int second;

        public Time(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            System.out.println(this.second);
            System.out.println(this.minute);
            System.out.println(this.hour);
        }

        int m1;
        int m2;
        int h1;
        int h2;

        public void addsecond(int second1) {
            this.second += second1;
            m1 = this.second / 60;
            this.second = this.second % 60;
            /*addminute(m1);*/
        }

        public void addminute(int minute1) {
            this.minute = this.minute + minute1 + m1;
            h1 = this.minute / 60;
            this.minute = this.minute % 60;
            /*addhour(h1);*/
        }

        public void addhour(int hour1) {
            this.hour = this.hour + hour1 + h1;
            this.hour = this.hour % 24;
        }

        /*时间减法*/
        public void susecond(int second2) {
            this.second -= second2;
            if (this.second < 0) {
                m2 = this.second / 60 - 1;
                this.second = this.second % 60 + 60;
            }
        }

        public void suminute(int minute2) {
            this.minute = this.minute - minute2 + m2;
            if (this.minute < 0) {
                h2 = this.minute / 60 - 1;
                this.minute = this.minute % 60 + 60;
            }
        }

        public void suhour(int hour2) {
            this.hour = this.hour - hour2 + h2;
            if (this.hour < 0) {
                this.hour = this.hour % 24 + 24;
            }
        }

        public void print1() {
            System.out.println("现在时间是" + this.hour + "时" + this.minute + "分" + this.second + "秒");
        }

        public static void testers(String[] args) {
            /*Scanner input=new Scanner(System.in);*/

            Time time = new Time(2, 13, 14);

            time.addsecond(0);
            time.addminute(0);
            time.addhour(0);

            time.susecond(86340);
            time.suminute(65);
            time.suhour(24);
            time.print1();
        }
    }
}