package com.xxx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.xxx.http.OKHttpClientBuilder;
import com.xxx.service.ChunqiuService;
import com.xxx.util.RandomUserAgent;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TaskRun {

	public static void main(String[] args) throws InterruptedException {
		List<String[]> routeList = new ArrayList<String[]>();
		routeList.add(new String[] { "成都", "广州" });
		routeList.add(new String[] { "广州", "成都" });
		
		int days = 2; // 抓取多少天
		
		Retrofit.Builder retrBuilder = new Retrofit.Builder()
		        .baseUrl("https://flights.ch.com")
				.addConverterFactory(ScalarsConverterFactory.create())
				// .addCallAdapterFactory(RxJava2CallAdapterFactory.create() //同步
				.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
		
		Observable.range(1, days) // 循环多少天
					.flatMap(it -> Observable.fromArray(routeList.toArray())
							.map(route -> {  // 每个航线都和这个日期组合
								String date = new DateTime().plusDays(it).toString("yyyy-MM-dd") ;
								return new String[] { ((String[]) route)[0], ((String[]) route)[1], date };
							}))
					.flatMap(route -> Observable.create(subscriber -> {
						// 实际抓取中这里需要用getHttpClient(String proxyHost, int proxyPort)获取代理请求httpclient
    					    Retrofit retrofit = retrBuilder.client(OKHttpClientBuilder.getHttpClient()).build();
                            ChunqiuService service = retrofit.create(ChunqiuService.class);
                            String referer = "https://flights.ch.com/OSA-WUH.html?" + System.currentTimeMillis();
                            String userAgent = RandomUserAgent.getRandomUserAgent();
                            Call<String> call = service.query(referer, userAgent, route[0], route[1], route[2], 0, 1, "null", "false", "false", "false", "false", 1, 0, "false");
                            try {
                                Response<String> res = call.execute();
                                subscriber.onNext(res.body());
                            } catch (Exception ex) {
                                subscriber.onError(ex);
                            }
                            subscriber.onComplete();
				        }).retryWhen(errors -> errors
                            .flatMap(e -> {
                                e.printStackTrace();
                                return Observable.just(e);
                            })
                            .zipWith(Observable.range(1, 3), (n, i) -> i)
                            .flatMap(retryCount -> {
                                System.out.println(String.format("retry %s,%s,%s", route[0], route[1], route[2]));
                                return Observable.timer(200, TimeUnit.MILLISECONDS);
                        })).subscribeOn(Schedulers.io())
					)
					.blockingForEach(System.out::println);

	}
	
}
