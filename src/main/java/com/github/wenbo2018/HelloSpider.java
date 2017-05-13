package com.github.wenbo2018;

import org.slf4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wenbo.shen on 2017/5/13.
 */
public class HelloSpider implements PageProcessor {
    /***
     * 爬取大众点评商户信息
     */
    private static int size = 0;// 共抓取到的文章数量
    // 抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        if (!page.getUrl().regex("http://www.dianping.com/shop/\\d").match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@class='shop-list']").links()// 限定文章列表获取区域
                    .regex("/shop/\\d+")
                    .replace("/shop/", "http://www.dianping.com/shop/")// 巧用替换给把相对url转换成绝对url
                    .all());
            List<String> list = page.getHtml().xpath("//div[@class='pages']").links()// 限定其他列表页获取区域
                    .regex(".*/search/category/1/90/g25475.*")
                    //.replace("/search/", "http://www.dianping.com/search/")//用替换给把相对url转换成绝对url
                    .all();
            page.addTargetRequests(list);
        } else {
            //抽取信息
            size++;
            String article = page.getHtml().xpath("//div[@class='shop-name']/h1/text()").get();
            System.err.println(article);
        }
    }

    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("【爬虫开始】请耐心等待一大波数据到你碗里来...");
        startTime = System.currentTimeMillis();
        // 从用户博客首页开始抓，开启5个线程，启动爬虫
        Spider.create(new HelloSpider()).addUrl("http://www.dianping.com/search/category/1/90/g25475sh9003dt9703").thread(5).run();
        endTime = System.currentTimeMillis();
        System.out.println("【爬虫结束】共抓取" + size + "篇文章，耗时约" + ((endTime - startTime) / 1000) + "秒，已保存到数据库，请查收！");

    }

}