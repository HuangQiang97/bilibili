package com.imooc.bilibili.service.config;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * FastJson基本配置，并构建json<->对象转换器
 *
 * @author huangqiang
 * @date 2022/3/19 22:43
 * @see
 * @since
 */
@Configuration
public class JsonHttpMessageConverterConfig {

    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                //开启缩进
                SerializerFeature.PrettyFormat,
                // fastjson中格式化时跳过null对象
                // 将null字符串表达为空
                SerializerFeature.WriteNullStringAsEmpty,
                // 将null列表表达为空
                SerializerFeature.WriteNullListAsEmpty,
                // 将null map表达为空
                SerializerFeature.WriteMapNullValue,
                // 显示时将map内元素排序
                SerializerFeature.MapSortField,
                // 关闭间接引用，集合中存储多个相同对象时，在json格式化时除第一个对象外其它对象都表述为第一个对象的引用，通过关闭间接引用让所有对象都输出值而非引用。
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastConverter);
    }
}
