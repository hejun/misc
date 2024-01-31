package io.github.hejun.misc.tv.test;

import io.github.hejun.misc.tv.service.TvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TvTest
 *
 * @author HeJun
 */
@SpringBootTest
public class TvTest {

	@Autowired
	private TvService tvService;

	@Test
	public void testRun() throws Exception {
		var source = "https://ldncctvwbcdcnc.v.wscdns.com/ldncctvwbcd/cdrmldcctv1_1/index.m3u8";
		tvService.loadSource(source);
	}
}
