package com.cn.lucky.morning.limit.service;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * RequestLimitService
 * 限流检测 接口类
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.service
 * @date 2021/11/24 16:06
 */
public interface RequestLimitService {
    /**
     * 检测是否限流
     *
     * @param dto 限流参数
     * @return 是否被限流 true: 被限流  false: 未限流，放行
     */
    boolean checkRequestLimit(RequestLimitDTO dto);

    /**
     * 获取当前限流类型
     *
     * @return 限流类型
     */
    RequestLimitType getType();

    /**
     * 获取带注解方法列表
     *
     * @param resourcePatternResolver 资源查询
     * @param requestLimitType        注解类型
     * @param scanPackage             扫描包路径
     * @return 带注解方法列表
     */
    default List<RequestLimitDTO> getTokenLimitList(ResourcePatternResolver resourcePatternResolver, RequestLimitType requestLimitType, String scanPackage) {
        try {
            List<RequestLimitDTO> list = new ArrayList<>();

            Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + scanPackage + "/**/*.class");

            MetadataReaderFactory metaReader = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {
                MetadataReader reader = metaReader.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();

                Set<MethodMetadata> annotatedMethods = annotationMetadata.getAnnotatedMethods(RequestLimit.class.getCanonicalName());
                annotatedMethods.forEach(methodMetadata -> {
                    RequestLimit limit = methodMetadata.getAnnotations().get(RequestLimit.class).synthesize();
                    if (!requestLimitType.equals(limit.type())) {
                        return;
                    }
                    RequestLimitDTO dto = new RequestLimitDTO();
                    dto.setKey(methodMetadata.getMethodName());
                    dto.setLimit(limit);
                    list.add(dto);
                });
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
