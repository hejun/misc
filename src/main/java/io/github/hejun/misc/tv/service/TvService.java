package io.github.hejun.misc.tv.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 播放源Service
 *
 * @author HeJun
 */
@Slf4j
@Service
public class TvService {

	static {
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	public void loadSource(String spec) throws Exception {
		log.info("开始加载播放源: {}", spec);
		URL url = new URL(spec);
		try (InputStream inputStream = url.openStream()) {
			String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			this.parseSource(content);
		}
	}

	private void parseSource(String source) throws Exception {
		log.info("播放源: \n{}", source);
	}
}
